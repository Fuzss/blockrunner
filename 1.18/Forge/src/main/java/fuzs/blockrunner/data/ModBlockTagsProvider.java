package fuzs.blockrunner.data;

import fuzs.blockrunner.init.ModRegistry;
import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ModBlockTagsProvider extends AbstractTagProvider.Blocks {

    public ModBlockTagsProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTags() {
        this.tag(ModRegistry.VERY_SLOW_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.SLOW_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.SLIGHTLY_SLOW_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.SLIGHTLY_QUICK_BLOCKS_BLOCK_TAG).addTag(BlockTags.STONE_BRICKS);
        this.tag(ModRegistry.QUICK_BLOCKS_BLOCK_TAG).add(Blocks.DIRT_PATH);
        this.tag(ModRegistry.VERY_QUICK_BLOCKS_BLOCK_TAG);
    }
}
