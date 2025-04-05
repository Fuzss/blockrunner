package fuzs.blockrunner.data;

import fuzs.blockrunner.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ModBlockTagsProvider extends AbstractTagProvider<Block> {

    public ModBlockTagsProvider(DataProviderContext context) {
        super(Registries.BLOCK, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.VERY_SLOW_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.SLOW_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.SLIGHTLY_SLOW_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.SLIGHTLY_QUICK_BLOCKS_BLOCK_TAG).addTag(BlockTags.STONE_BRICKS);
        this.tag(ModRegistry.QUICK_BLOCKS_BLOCK_TAG).add(Blocks.DIRT_PATH);
        this.tag(ModRegistry.VERY_QUICK_BLOCKS_BLOCK_TAG);
    }
}
