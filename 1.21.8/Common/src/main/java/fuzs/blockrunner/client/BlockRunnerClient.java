package fuzs.blockrunner.client;

import fuzs.blockrunner.client.handler.BlockSpeedTooltipHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.gui.ItemTooltipCallback;

public class BlockRunnerClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ItemTooltipCallback.EVENT.register(BlockSpeedTooltipHandler::onItemTooltip);
    }
}
