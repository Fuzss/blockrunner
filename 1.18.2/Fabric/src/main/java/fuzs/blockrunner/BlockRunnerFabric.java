package fuzs.blockrunner;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class BlockRunnerFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(BlockRunner.MOD_ID, BlockRunner::new);
    }
}
