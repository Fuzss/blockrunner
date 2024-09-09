package fuzs.blockrunner.fabric.client;

import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.client.BlockRunnerClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class BlockRunnerFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(BlockRunner.MOD_ID, BlockRunnerClient::new);
    }
}
