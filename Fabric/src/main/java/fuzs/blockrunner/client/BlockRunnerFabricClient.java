package fuzs.blockrunner.client;

import fuzs.blockrunner.client.handler.BlockSpeedTooltipHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class BlockRunnerFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ItemTooltipCallback.EVENT.register(BlockSpeedTooltipHandler::onItemTooltip);
    }
}
