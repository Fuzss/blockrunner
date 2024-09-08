package fuzs.blockrunner.fabric;

import fuzs.blockrunner.BlockRunner;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.network.configuration.RegistryDataMapNegotiation;
import net.neoforged.neoforge.network.payload.KnownRegistryDataMapsPayload;
import net.neoforged.neoforge.network.payload.KnownRegistryDataMapsReplyPayload;
import net.neoforged.neoforge.network.payload.RegistryDataMapSyncPayload;
import net.neoforged.neoforge.registries.ClientRegistryManager;
import net.neoforged.neoforge.registries.DataMapLoader;
import net.neoforged.neoforge.registries.RegistryManager;

import java.util.*;

public class BlockRunnerFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(BlockRunner.MOD_ID, BlockRunner::new);
        registerEventHandlers();
        registerNetworkMessages();
    }

    private static void registerEventHandlers() {
        DataMapLoader[] dataMapLoader = new DataMapLoader[1];
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(BlockRunner.id(DataMapLoader.PATH),
                (HolderLookup.Provider registries) -> {
                    return dataMapLoader[0] = new DataMapLoader(
                            ((ReloadableServerResources.ConfigurableRegistryLookup) registries).registryAccess);
                }
        );
        CommonLifecycleEvents.TAGS_LOADED.register((RegistryAccess registries, boolean client) -> {
            if (!client) {
                Objects.requireNonNull(dataMapLoader[0], "data map loader is null");
                dataMapLoader[0].apply();
            }
        });
        ServerConfigurationConnectionEvents.CONFIGURE.register(
                (ServerConfigurationPacketListenerImpl handler, MinecraftServer server) -> {
                    if (ServerConfigurationNetworking.canSend(handler, RegistryDataMapNegotiation.ID)) {
                        handler.addTask(new RegistryDataMapNegotiation(handler));
                    }
                });
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((ServerPlayer player, boolean joined) -> {
            RegistryManager.getDataMaps().forEach((registry, values) -> {
                final var regOpt = player.getServer().overworld().registryAccess().registry(registry);
                if (regOpt.isEmpty()) return;
                if (!ServerPlayNetworking.canSend(player, RegistryDataMapSyncPayload.TYPE)) {
                    return;
                }
                if (player.connection.connection.isMemoryConnection()) {
                    // Note: don't send data maps over in-memory connections, else the client-side handling will wipe non-synced data maps.
                    return;
                }
                final var playerMaps = player.connection.connection.channel.attr(
                        RegistryManager.ATTRIBUTE_KNOWN_DATA_MAPS).get();
                if (playerMaps == null) return; // Skip gametest players for instance
                handleSync(player, regOpt.get(), playerMaps.getOrDefault(registry, List.of()));
            });
        });
    }

    private static void registerNetworkMessages() {
        PayloadTypeRegistry.configurationC2S().register(KnownRegistryDataMapsReplyPayload.TYPE,
                KnownRegistryDataMapsReplyPayload.STREAM_CODEC
        );
        ServerConfigurationNetworking.registerGlobalReceiver(KnownRegistryDataMapsReplyPayload.TYPE,
                RegistryManager::handleKnownDataMapsReply
        );
        PayloadTypeRegistry.configurationS2C().register(KnownRegistryDataMapsPayload.TYPE,
                KnownRegistryDataMapsPayload.STREAM_CODEC
        );
        ClientConfigurationNetworking.registerGlobalReceiver(KnownRegistryDataMapsPayload.TYPE,
                ClientRegistryManager::handleKnownDataMaps
        );
        PayloadTypeRegistry.playS2C().register(RegistryDataMapSyncPayload.TYPE,
                RegistryDataMapSyncPayload.STREAM_CODEC
        );
        ClientPlayNetworking.registerGlobalReceiver(RegistryDataMapSyncPayload.TYPE,
                ClientRegistryManager::handleDataMapSync
        );
    }

    private static <T> void handleSync(ServerPlayer player, Registry<T> registry, Collection<ResourceLocation> attachments) {
        if (attachments.isEmpty()) return;
        final Map<ResourceLocation, Map<ResourceKey<T>, ?>> att = new HashMap<>();
        attachments.forEach(key -> {
            final var attach = RegistryManager.getDataMap(registry.key(), key);
            if (attach == null || attach.networkCodec() == null) return;
            att.put(key, registry.getDataMap(attach));
        });
        if (!att.isEmpty()) {
            ServerPlayNetworking.send(player, new RegistryDataMapSyncPayload<>(registry.key(), att));
        }
    }
}
