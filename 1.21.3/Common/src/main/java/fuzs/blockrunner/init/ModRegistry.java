package fuzs.blockrunner.init;

import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.world.level.block.data.BlockSpeed;
import fuzs.neoforgedatapackextensions.api.v1.DataMapRegistry;
import fuzs.neoforgedatapackextensions.api.v1.DataMapToken;
import fuzs.puzzleslib.api.init.v3.tags.BoundTagFactory;
import net.minecraft.core.registries.Registries;
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

    public static final DataMapToken<Block, BlockSpeed> BLOCK_SPEED_DATA_MAP_TYPE = DataMapRegistry.INSTANCE.register(
            BlockRunner.id("block_speeds"), Registries.BLOCK, BlockSpeed.CODEC, BlockSpeed.SPEED_CODEC, true);

    public static void touch() {
        // NO-OP
    }
}
