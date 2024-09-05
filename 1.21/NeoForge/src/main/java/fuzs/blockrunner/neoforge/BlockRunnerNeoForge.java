package fuzs.blockrunner.neoforge;

import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.data.ModBlockTagsProvider;
import fuzs.blockrunner.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(BlockRunner.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRunnerNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(BlockRunner.MOD_ID, BlockRunner::new);
        DataProviderHelper.registerDataProviders(BlockRunner.MOD_ID, ModBlockTagsProvider::new, ModLanguageProvider::new);
    }
}
