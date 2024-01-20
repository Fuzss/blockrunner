package fuzs.blockrunner.init;

import fuzs.blockrunner.BlockRunner;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(BlockRunner.MOD_ID);
    public static final TagKey<Block> VERY_SLOW_BLOCKS_BLOCK_TAG = REGISTRY.registerBlockTag("very_slow_blocks");
    public static final TagKey<Block> SLOW_BLOCKS_BLOCK_TAG = REGISTRY.registerBlockTag("slow_blocks");
    public static final TagKey<Block> SLIGHTLY_SLOW_BLOCKS_BLOCK_TAG = REGISTRY.registerBlockTag("slightly_slow_blocks");
    public static final TagKey<Block> SLIGHTLY_QUICK_BLOCKS_BLOCK_TAG = REGISTRY.registerBlockTag("slightly_quick_blocks");
    public static final TagKey<Block> QUICK_BLOCKS_BLOCK_TAG = REGISTRY.registerBlockTag("quick_blocks");
    public static final TagKey<Block> VERY_QUICK_BLOCKS_BLOCK_TAG = REGISTRY.registerBlockTag("very_quick_blocks");

    public static void touch() {

    }
}
