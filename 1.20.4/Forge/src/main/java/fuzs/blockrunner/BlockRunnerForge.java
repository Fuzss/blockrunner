package fuzs.blockrunner;

import fuzs.blockrunner.data.ModBlockTagsProvider;
import fuzs.blockrunner.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(BlockRunner.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRunnerForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(BlockRunner.MOD_ID, BlockRunner::new);
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        evt.getGenerator().addProvider(true, new ModBlockTagsProvider(evt, BlockRunner.MOD_ID));
        evt.getGenerator().addProvider(true, new ModLanguageProvider(evt, BlockRunner.MOD_ID));
    }
}
