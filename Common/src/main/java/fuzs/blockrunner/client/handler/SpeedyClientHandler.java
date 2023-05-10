package fuzs.blockrunner.client.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.config.ClientConfig;
import fuzs.blockrunner.data.BlockSpeedManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpeedyClientHandler {

    public static void onItemTooltip(ItemStack stack, TooltipFlag context, List<Component> lines) {
        if (!BlockRunner.CONFIG.getHolder(ClientConfig.class).isAvailable() || !BlockRunner.CONFIG.get(ClientConfig.class).blockSpeedMultiplierTooltip) {
            return;
        }
        if (stack.getItem() instanceof BlockItem item && BlockSpeedManager.INSTANCE.hasBlockSpeed(item.getBlock())) {
            lines.add(Component.translatable("block.blockrunner.speedMultiplier", BlockSpeedManager.INSTANCE.getSpeedFactor(item.getBlock())).withStyle(ChatFormatting.GRAY));
        }
    }

    public static Optional<Float> onComputeFovModifier(Player player, float fovModifier, float newFovModifier) {
        if (!BlockRunner.CONFIG.get(ClientConfig.class).disableFieldOfViewChanges) return Optional.empty();
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.isScoping()) return Optional.empty();
        if (player.getAttributes().hasModifier(Attributes.MOVEMENT_SPEED, BlockSpeedManager.SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID)) {
            return Optional.of(getFieldOfViewModifierWithoutBlockSpeed(player));
        }
        return Optional.empty();
    }

    private static float getFieldOfViewModifierWithoutBlockSpeed(Player player) {
        float fovModifier = 1.0F;
        if (player.getAbilities().flying) {
            fovModifier *= 1.1F;
        }

        double movementSpeed = calculateAttributeValueSkipping(player.getAttribute(Attributes.MOVEMENT_SPEED), BlockSpeedManager.SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID);
        fovModifier *= ((float) movementSpeed / player.getAbilities().getWalkingSpeed() + 1.0F) / 2.0F;
        if (player.getAbilities().getWalkingSpeed() == 0.0F || Float.isNaN(fovModifier) || Float.isInfinite(fovModifier)) {
            fovModifier = 1.0F;
        }

        if (player.isUsingItem()) {
            if (player.getUseItem().is(Items.BOW)) {
                int i = player.getTicksUsingItem();
                float g = (float) i / 20.0F;
                if (g > 1.0F) {
                    g = 1.0F;
                } else {
                    g *= g;
                }

                fovModifier *= 1.0F - g * 0.15F;
            } else if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.isScoping()) {
                return 0.1F;
            }
        }

        return Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get().floatValue(), 1.0F, fovModifier);
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
