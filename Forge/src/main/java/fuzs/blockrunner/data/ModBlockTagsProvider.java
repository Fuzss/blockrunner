package fuzs.blockrunner.data;

import fuzs.blockrunner.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(DataGenerator dataGenerator, String modId, @Nullable ExistingFileHelper fileHelper) {
        super(dataGenerator, modId, fileHelper);
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
