package fuzs.blockrunner;

import fuzs.blockrunner.config.ClientConfig;
import fuzs.blockrunner.init.ModRegistry;
import fuzs.blockrunner.network.S2CBlockSpeedMessage;
import fuzs.blockrunner.world.level.block.data.BlockSpeedManager;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.server.AddDataPackReloadListenersCallback;
import fuzs.puzzleslib.api.event.v1.server.SyncDataPackContentsCallback;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class BlockRunner implements ModConstructor {
    public static final String MOD_ID = "blockrunner";
    public static final String MOD_NAME = "Block Runner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID).registerLegacyClientbound(
            S2CBlockSpeedMessage.class, S2CBlockSpeedMessage::new);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        SyncDataPackContentsCallback.EVENT.register(BlockSpeedManager.INSTANCE::onSyncDataPackContents);
        AddDataPackReloadListenersCallback.EVENT.register(
                (BiConsumer<ResourceLocation, Function<HolderLookup.Provider, PreparableReloadListener>> consumer) -> {
                    consumer.accept(id("block_speeds"), (HolderLookup.Provider registries) -> {
                        return BlockSpeedManager.INSTANCE;
                    });
                });
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
