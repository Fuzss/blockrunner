package fuzs.blockrunner.data;

import fuzs.blockrunner.init.ModRegistry;
import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends AbstractTagProvider.Blocks {

    public ModBlockTagsProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.VERY_SLOW_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.SLOW_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.SLIGHTLY_SLOW_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.SLIGHTLY_QUICK_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.QUICK_BLOCKS_BLOCK_TAG);
        this.tag(ModRegistry.VERY_QUICK_BLOCKS_BLOCK_TAG);
    }
}
