package fuzs.blockrunner.init;

import fuzs.blockrunner.BlockRunner;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModRegistry {
    public static final TagKey<Block> VERY_SLOW_BLOCKS_BLOCK_TAG = TagKey.create(Registry.BLOCK_REGISTRY, BlockRunner.id("very_slow_blocks"));
    public static final TagKey<Block> SLOW_BLOCKS_BLOCK_TAG = TagKey.create(Registry.BLOCK_REGISTRY, BlockRunner.id("slow_blocks"));
    public static final TagKey<Block> SLIGHTLY_SLOW_BLOCKS_BLOCK_TAG = TagKey.create(Registry.BLOCK_REGISTRY, BlockRunner.id("slightly_slow_blocks"));
    public static final TagKey<Block> SLIGHTLY_QUICK_BLOCKS_BLOCK_TAG = TagKey.create(Registry.BLOCK_REGISTRY, BlockRunner.id("slightly_quick_blocks"));
    public static final TagKey<Block> QUICK_BLOCKS_BLOCK_TAG = TagKey.create(Registry.BLOCK_REGISTRY, BlockRunner.id("quick_blocks"));
    public static final TagKey<Block> VERY_QUICK_BLOCKS_BLOCK_TAG = TagKey.create(Registry.BLOCK_REGISTRY, BlockRunner.id("very_quick_blocks"));

    public static void touch() {

    }
}
