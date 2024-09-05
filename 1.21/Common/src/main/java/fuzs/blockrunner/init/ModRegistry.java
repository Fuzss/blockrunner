package fuzs.blockrunner.init;

import fuzs.blockrunner.BlockRunner;
import fuzs.puzzleslib.api.init.v3.tags.BoundTagFactory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModRegistry {
    static final BoundTagFactory TAGS = BoundTagFactory.make(BlockRunner.MOD_ID);
    public static final TagKey<Block> VERY_SLOW_BLOCKS_BLOCK_TAG = TAGS.registerBlockTag("very_slow_blocks");
    public static final TagKey<Block> SLOW_BLOCKS_BLOCK_TAG = TAGS.registerBlockTag("slow_blocks");
    public static final TagKey<Block> SLIGHTLY_SLOW_BLOCKS_BLOCK_TAG = TAGS.registerBlockTag("slightly_slow_blocks");
    public static final TagKey<Block> SLIGHTLY_QUICK_BLOCKS_BLOCK_TAG = TAGS.registerBlockTag("slightly_quick_blocks");
    public static final TagKey<Block> QUICK_BLOCKS_BLOCK_TAG = TAGS.registerBlockTag("quick_blocks");
    public static final TagKey<Block> VERY_QUICK_BLOCKS_BLOCK_TAG = TAGS.registerBlockTag("very_quick_blocks");

    public static void touch() {

    }
}
