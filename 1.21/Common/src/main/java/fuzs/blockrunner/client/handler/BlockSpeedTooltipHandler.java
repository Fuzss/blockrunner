package fuzs.blockrunner.client.handler;

import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.config.ClientConfig;
import fuzs.blockrunner.world.level.block.data.BlockSpeedManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockSpeedTooltipHandler {
    public static final String KEY_SPEED_MULTIPLIER = "block.blockrunner.speedMultiplier";

    public static void onItemTooltip(ItemStack itemStack, List<Component> lines, Item.TooltipContext tooltipContext, @Nullable Player player, TooltipFlag tooltipFlag) {
        if (!BlockRunner.CONFIG.getHolder(ClientConfig.class).isAvailable() || !BlockRunner.CONFIG.get(
                ClientConfig.class).blockSpeedMultiplierTooltip) {
            return;
        }
        if (itemStack.getItem() instanceof BlockItem item && BlockSpeedManager.INSTANCE.hasBlockSpeed(
                item.getBlock())) {
            lines.add(Component.translatable(KEY_SPEED_MULTIPLIER,
                    BlockSpeedManager.INSTANCE.getSpeedFactor(item.getBlock())
            ).withStyle(ChatFormatting.GRAY));
        }
    }
}
