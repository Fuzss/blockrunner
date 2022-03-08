package fuzs.blockrunner;

import fuzs.blockrunner.data.BlockSpeedManager;
import fuzs.blockrunner.network.message.S2CBlockSpeedMessage;
import fuzs.puzzleslib.network.MessageDirection;
import fuzs.puzzleslib.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BlockRunner.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRunner {
    public static final String MOD_ID = "blockrunner";
    public static final String MOD_NAME = "Block Runner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.of(MOD_ID);

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        registerHandlers();
        registerMessages();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final AddReloadListenerEvent evt) -> evt.addListener(BlockSpeedManager.INSTANCE));
        MinecraftForge.EVENT_BUS.addListener(BlockSpeedManager.INSTANCE::onPlayerLoggedIn);
    }

    private static void registerMessages() {
        NETWORK.register(S2CBlockSpeedMessage.class, S2CBlockSpeedMessage::new, MessageDirection.TO_CLIENT);
    }
}
