package fuzs.blockrunner.client.helper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.config.ClientConfig;
import fuzs.blockrunner.data.BlockSpeedManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldOfViewHelper {

    public static boolean shouldRemoveBlockSpeedModifier(Player player) {
        if (!BlockRunner.CONFIG.get(ClientConfig.class).disableFieldOfViewChanges) return false;
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.isScoping()) return false;
        return player.getAttributes().hasModifier(Attributes.MOVEMENT_SPEED, BlockSpeedManager.SPEED_MODIFIER_BLOCK_SPEED_UUID);
    }

    public static float getFieldOfViewModifierWithoutBlockSpeed(Player player) {
        float fovModifier = player.getAbilities().flying ? 1.1F : 1.0F;
        double movementSpeed = calculateAttributeValueSkipping(player.getAttribute(Attributes.MOVEMENT_SPEED), BlockSpeedManager.SPEED_MODIFIER_BLOCK_SPEED_UUID);
        fovModifier *= ((float) movementSpeed / player.getAbilities().getWalkingSpeed() + 1.0F) / 2.0F;
        return fovModifier;
    }

    private static double calculateAttributeValueSkipping(AttributeInstance attributeInstance, UUID... skippedModifierIds) {

        double baseValue = attributeInstance.getBaseValue();

        Map<AttributeModifier.Operation, Set<AttributeModifier>> operationToModifiers = Stream.of(AttributeModifier.Operation.values()).collect(Collectors.toMap(Function.identity(), operation -> Sets.newHashSet(), (o1, o2) -> o1, () -> Maps.newEnumMap(AttributeModifier.Operation.class)));
        attributeInstance.getModifiers().stream()
                .filter(modifier -> !ArrayUtils.contains(skippedModifierIds, modifier.getId()))
                .forEach(modifier -> operationToModifiers.get(modifier.getOperation()).add(modifier));

        for (AttributeModifier attributeModifier : operationToModifiers.get(AttributeModifier.Operation.ADDITION)) {
            baseValue += attributeModifier.getAmount();
        }

        double baseValueCopy = baseValue;

        for (AttributeModifier attributeModifier : operationToModifiers.get(AttributeModifier.Operation.MULTIPLY_BASE)) {
            baseValueCopy += baseValue * attributeModifier.getAmount();
        }

        for (AttributeModifier attributeModifier : operationToModifiers.get(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
            baseValueCopy *= 1.0 + attributeModifier.getAmount();
        }

        return attributeInstance.getAttribute().sanitizeValue(baseValueCopy);
    }
}
