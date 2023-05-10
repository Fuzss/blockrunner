package fuzs.blockrunner.client;

import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.client.handler.BlockSpeedTooltipHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = BlockRunner.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BlockRunnerForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final ItemTooltipEvent evt) -> {
            BlockSpeedTooltipHandler.onItemTooltip(evt.getItemStack(), evt.getFlags(), evt.getToolTip());
        });
    }
}
