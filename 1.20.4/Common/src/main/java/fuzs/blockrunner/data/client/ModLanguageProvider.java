package fuzs.blockrunner.data.client;

import fuzs.blockrunner.client.handler.BlockSpeedTooltipHandler;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.add(BlockSpeedTooltipHandler.KEY_SPEED_MULTIPLIER, "Speed Multiplier: %s");
    }
}
