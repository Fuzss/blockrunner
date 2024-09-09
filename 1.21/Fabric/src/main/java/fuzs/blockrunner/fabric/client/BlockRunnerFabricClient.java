package fuzs.blockrunner.fabric.client;

import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.client.BlockRunnerClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.neoforged.neoforge.network.payload.KnownRegistryDataMapsPayload;
import net.neoforged.neoforge.network.payload.RegistryDataMapSyncPayload;
import net.neoforged.neoforge.registries.ClientRegistryManager;

public class BlockRunnerFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(BlockRunner.MOD_ID, BlockRunnerClient::new);
        registerNetworkMessages();
    }

    private static void registerNetworkMessages() {
        ClientConfigurationNetworking.registerGlobalReceiver(KnownRegistryDataMapsPayload.TYPE,
                ClientRegistryManager::handleKnownDataMaps
        );
        ClientPlayNetworking.registerGlobalReceiver(RegistryDataMapSyncPayload.TYPE,
                ClientRegistryManager::handleDataMapSync
        );
    }
}
