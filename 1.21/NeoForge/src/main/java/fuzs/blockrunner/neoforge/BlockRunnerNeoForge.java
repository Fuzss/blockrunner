package fuzs.blockrunner.neoforge;

import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.data.ModBlockTagsProvider;
import fuzs.blockrunner.neoforge.data.ModDataMapProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(BlockRunner.MOD_ID)
public class BlockRunnerNeoForge {

    public BlockRunnerNeoForge() {
        ModConstructor.construct(BlockRunner.MOD_ID, BlockRunner::new);
        DataProviderHelper.registerDataProviders(BlockRunner.MOD_ID, ModBlockTagsProvider::new, ModDataMapProvider::new);
    }
}
