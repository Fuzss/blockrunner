package fuzs.blockrunner.client.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.blockrunner.BlockRunner;
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
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpeedyClientHandler {

    public void onItemTooltip(ItemStack stack, TooltipFlag context, List<Component> lines) {
        if (!BlockRunner.CONFIG.client().blockSpeedMultiplierTooltip) return;
        if (stack.getItem() instanceof BlockItem item) {
            Block block = item.getBlock();
            if (BlockSpeedManager.INSTANCE.hasCustomSpeed(block)) {
                lines.add(Component.translatable("block.blockrunner.speedMultiplier", BlockSpeedManager.INSTANCE.getSpeedFactor(block)).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public float onComputeFovModifier(Player player, float fovModifier) {
        if (!BlockRunner.CONFIG.client().disableFieldOfViewChanges) return fovModifier;
        if (player.getAttributes().hasModifier(Attributes.MOVEMENT_SPEED, BlockSpeedManager.SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID)) {
            return this.getCustomFieldOfViewModifier(player);
        }
        return fovModifier;
    }

    private float getCustomFieldOfViewModifier(Player player) {
        float f = 1.0F;
        if (player.getAbilities().flying) {
            f *= 1.1F;
        }

        double movementSpeed = this.calculateValueSkipping(player.getAttribute(Attributes.MOVEMENT_SPEED), BlockSpeedManager.SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID);
        f *= ((float) movementSpeed / player.getAbilities().getWalkingSpeed() + 1.0F) / 2.0F;
        if (player.getAbilities().getWalkingSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
            f = 1.0F;
        }

        ItemStack itemStack = player.getUseItem();
        if (player.isUsingItem()) {
            if (itemStack.is(Items.BOW)) {
                int i = player.getTicksUsingItem();
                float g = (float)i / 20.0F;
                if (g > 1.0F) {
                    g = 1.0F;
                } else {
                    g *= g;
                }

                f *= 1.0F - g * 0.15F;
            } else if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.isScoping()) {
                return 0.1F;
            }
        }

        return Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get().floatValue(), 1.0F, f);
    }

    private double calculateValueSkipping(AttributeInstance attributeInstance, UUID... skippedModifierIds) {
        double d = attributeInstance.getBaseValue();

        Map<AttributeModifier.Operation, Set<AttributeModifier>> operationToModifiers = Stream.of(AttributeModifier.Operation.values())
                .collect(Collectors.toMap(Function.identity(), operation -> Sets.newHashSet(), (o1, o2) -> o1, () -> Maps.newEnumMap(AttributeModifier.Operation.class)));
        attributeInstance.getModifiers().stream()
                .filter(modifier -> {
                    for (UUID skippedId : skippedModifierIds) {
                        if (skippedId.equals(modifier.getId())) {
                            return false;
                        }
                    }
                    return true;
                })
                .forEach(modifier -> operationToModifiers.get(modifier.getOperation()).add(modifier));

        for(AttributeModifier attributeModifier : operationToModifiers.get(AttributeModifier.Operation.ADDITION)) {
            d += attributeModifier.getAmount();
        }

        double e = d;

        for(AttributeModifier attributeModifier2 : operationToModifiers.get(AttributeModifier.Operation.MULTIPLY_BASE)) {
            e += d * attributeModifier2.getAmount();
        }

        for(AttributeModifier attributeModifier2 : operationToModifiers.get(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
            e *= 1.0 + attributeModifier2.getAmount();
        }

        return attributeInstance.getAttribute().sanitizeValue(e);
    }
}
