package fuzs.blockrunner;

import fuzs.blockrunner.config.ClientConfig;
import fuzs.blockrunner.init.ModRegistry;
import fuzs.blockrunner.network.S2CBlockSpeedMessage;
import fuzs.blockrunner.world.level.block.data.BlockSpeedManager;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.AddReloadListenersContext;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerEvents;
import fuzs.puzzleslib.api.network.v2.MessageDirection;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockRunner implements ModConstructor {
    public static final String MOD_ID = "blockrunner";
    public static final String MOD_NAME = "Block Runner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class);
    public static final NetworkHandlerV2 NETWORK = NetworkHandlerV2.build(MOD_ID, true, true);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerMessages();
        registerHandlers();
    }

    private static void registerMessages() {
        NETWORK.register(S2CBlockSpeedMessage.class, S2CBlockSpeedMessage::new, MessageDirection.TO_CLIENT);
    }

    private static void registerHandlers() {
        PlayerEvents.LOGGED_IN.register(BlockSpeedManager.INSTANCE::onPlayerLoggedIn);
    }

    @Override
    public void onRegisterDataPackReloadListeners(AddReloadListenersContext context) {
        context.registerReloadListener("block_speeds", BlockSpeedManager.INSTANCE);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
