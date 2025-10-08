package fuzs.blockrunner.neoforge.data;

import fuzs.blockrunner.init.ModRegistry;
import fuzs.blockrunner.world.level.block.data.BlockSpeed;
import fuzs.neoforgedatapackextensions.neoforge.api.v1.NeoForgeDataMapToken;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {

    public ModDataMapProvider(DataProviderContext context) {
        this(context.getPackOutput(), context.getRegistries());
    }

    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider registries) {
        Builder<BlockSpeed, Block> builder = this.builder(NeoForgeDataMapToken.unwrap(ModRegistry.BLOCK_SPEED_DATA_MAP_TYPE));
        register(builder, ModRegistry.VERY_SLOW_BLOCKS_BLOCK_TAG, 0.45);
        register(builder, ModRegistry.SLOW_BLOCKS_BLOCK_TAG, 0.65);
        register(builder, ModRegistry.SLIGHTLY_SLOW_BLOCKS_BLOCK_TAG, 0.85);
        register(builder, ModRegistry.SLIGHTLY_QUICK_BLOCKS_BLOCK_TAG, 1.15);
        register(builder, ModRegistry.QUICK_BLOCKS_BLOCK_TAG, 1.35);
        register(builder, ModRegistry.VERY_QUICK_BLOCKS_BLOCK_TAG, 1.55);
    }

    static void register(Builder<BlockSpeed, Block> builder, Block block, double speed) {
        builder.add(block.builtInRegistryHolder(), new BlockSpeed(speed), false);
    }

    static void register(Builder<BlockSpeed, Block> builder, TagKey<Block> tagKey, double speed) {
        builder.add(tagKey, new BlockSpeed(speed), false);
    }
}
