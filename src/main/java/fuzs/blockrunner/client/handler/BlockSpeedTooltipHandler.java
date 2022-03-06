package fuzs.blockrunner.client.handler;

import fuzs.blockrunner.data.BlockSpeedManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockSpeedTooltipHandler {
    @SubscribeEvent
    public void onItemTooltip(final ItemTooltipEvent evt) {
        if (evt.getItemStack().getItem() instanceof BlockItem item) {
            Block block = item.getBlock();
            if (BlockSpeedManager.INSTANCE.hasCustomSpeed(block)) {
                evt.getToolTip().add(new TranslatableComponent("block.blockrunner.speed_multiplier", BlockSpeedManager.INSTANCE.getSpeedFactor(block)).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
