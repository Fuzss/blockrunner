package fuzs.blockrunner;

import fuzs.blockrunner.data.BlockSpeedManager;
import fuzs.puzzleslib.core.CoreServices;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(BlockRunner.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRunnerForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        CoreServices.FACTORIES.modConstructor(BlockRunner.MOD_ID).accept(new BlockRunner());
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final AddReloadListenerEvent evt) -> evt.addListener(BlockSpeedManager.INSTANCE));
        MinecraftForge.EVENT_BUS.addListener((final PlayerEvent.PlayerLoggedInEvent evt) -> {
            BlockSpeedManager.INSTANCE.onPlayerLoggedIn(evt.getEntity());
        });
    }
}
