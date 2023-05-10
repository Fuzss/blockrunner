package fuzs.blockrunner;

import fuzs.blockrunner.config.ClientConfig;
import fuzs.blockrunner.network.message.S2CBlockSpeedMessage;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.network.MessageDirection;
import fuzs.puzzleslib.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockRunner implements ModConstructor {
    public static final String MOD_ID = "blockrunner";
    public static final String MOD_NAME = "Block Runner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder CONFIG = CoreServices.FACTORIES.clientConfig(ClientConfig.class, () -> new ClientConfig());
    public static final NetworkHandler NETWORK = CoreServices.FACTORIES.network(MOD_ID, true, true);

    @Override
    public void onConstructMod() {
        CONFIG.bakeConfigs(MOD_ID);
        registerMessages();
    }

    private static void registerMessages() {
        NETWORK.register(S2CBlockSpeedMessage.class, S2CBlockSpeedMessage::new, MessageDirection.TO_CLIENT);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
