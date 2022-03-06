package fuzs.blockrunner.client;

import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.client.handler.BlockSpeedTooltipHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = BlockRunner.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BlockRunnerClient {
    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        registerHandlers();
    }

    private static void registerHandlers() {
        BlockSpeedTooltipHandler blockSpeedTooltipHandler = new BlockSpeedTooltipHandler();
        MinecraftForge.EVENT_BUS.addListener(blockSpeedTooltipHandler::onItemTooltip);
    }
}
