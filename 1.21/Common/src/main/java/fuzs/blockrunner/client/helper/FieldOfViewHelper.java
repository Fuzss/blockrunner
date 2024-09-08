package fuzs.blockrunner.client.helper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.config.ClientConfig;
import fuzs.blockrunner.world.level.block.data.BlockSpeed;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldOfViewHelper {

    public static boolean shouldRemoveBlockSpeedModifier(Player player) {
        if (!BlockRunner.CONFIG.get(ClientConfig.class).disableFieldOfViewChanges) return false;
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.isScoping()) return false;
        return player.getAttributes().hasModifier(Attributes.MOVEMENT_SPEED,
                BlockSpeed.SPEED_MODIFIER_BLOCK_SPEED_IDENTIFIER
        );
    }

    public static float getFieldOfViewModifierWithoutBlockSpeed(Player player) {
        float fovModifier = player.getAbilities().flying ? 1.1F : 1.0F;
        AttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute != null) {
            double movementSpeed = calculateAttributeValueSkipping(attribute,
                    BlockSpeed.SPEED_MODIFIER_BLOCK_SPEED_IDENTIFIER
            );
            fovModifier *= ((float) movementSpeed / player.getAbilities().getWalkingSpeed() + 1.0F) / 2.0F;
        }
        return fovModifier;
    }

    private static double calculateAttributeValueSkipping(AttributeInstance attribute, ResourceLocation... skippedModifiers) {

        double baseValue = attribute.getBaseValue();

        Map<AttributeModifier.Operation, Set<AttributeModifier>> operationToModifiers = Stream.of(
                AttributeModifier.Operation.values()).collect(
                Collectors.toMap(Function.identity(), operation -> Sets.newHashSet(), (o1, o2) -> o1,
                        () -> Maps.newEnumMap(AttributeModifier.Operation.class)
                ));
        attribute.getModifiers()
                .stream()
                .filter(modifier -> !ArrayUtils.contains(skippedModifiers, modifier.id()))
                .forEach(modifier -> operationToModifiers.get(modifier.operation()).add(modifier));

        for (AttributeModifier attributeModifier : operationToModifiers.get(AttributeModifier.Operation.ADD_VALUE)) {
            baseValue += attributeModifier.amount();
        }

        double baseValueCopy = baseValue;

        for (AttributeModifier attributeModifier : operationToModifiers.get(
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE)) {
            baseValueCopy += baseValue * attributeModifier.amount();
        }

        for (AttributeModifier attributeModifier : operationToModifiers.get(
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)) {
            baseValueCopy *= 1.0 + attributeModifier.amount();
        }

        return attribute.getAttribute().value().sanitizeValue(baseValueCopy);
    }
}
