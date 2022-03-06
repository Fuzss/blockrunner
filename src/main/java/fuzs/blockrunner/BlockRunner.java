package fuzs.blockrunner;

import fuzs.blockrunner.data.CustomBlockSpeedManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BlockRunner.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRunner {
    public static final String MOD_ID = "blockrunner";
    public static final String MOD_NAME = "Block Runner";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        MinecraftForge.EVENT_BUS.addListener((final AddReloadListenerEvent evt1) -> evt1.addListener(CustomBlockSpeedManager.INSTANCE));
    }

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent evt) {
        CustomBlockSpeedManager.INSTANCE.load();
    }
}
