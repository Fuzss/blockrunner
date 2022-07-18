package fuzs.blockrunner;

import fuzs.blockrunner.data.BlockSpeedManager;
import fuzs.puzzleslib.core.CoreServices;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.packs.resources.CloseableResourceManager;

public class BlockRunnerFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CoreServices.FACTORIES.modConstructor().accept(new BlockRunner());
        registerHandlers();
    }

    private static void registerHandlers() {
        ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
            BlockSpeedManager.INSTANCE.load();
        });
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((MinecraftServer server, CloseableResourceManager resourceManager, boolean success) -> {
            BlockSpeedManager.INSTANCE.load();
        });
        ServerPlayConnectionEvents.JOIN.register((ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) -> {
            BlockSpeedManager.INSTANCE.onPlayerLoggedIn(handler.getPlayer());
        });
    }
}
