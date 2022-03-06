package fuzs.blockrunner.data;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import fuzs.blockrunner.BlockRunner;
import fuzs.puzzleslib.json.JsonConfigFileUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CustomBlockSpeedManager implements PreparableReloadListener {
    public static final CustomBlockSpeedManager INSTANCE = new CustomBlockSpeedManager();
    private static final String CONFIG_FILE_NAME = BlockRunner.MOD_ID + ".json";
    private static final Set<SpeedHolderValue> DEFAULT_BLOCK_SPEEDS = new SpeedHolderValue.Builder()
            .add(Blocks.DIRT_PATH, 1.5)
            .add(BlockTags.STONE_BRICKS, 1.2)
            .build();

    private final Map<Block, Double> customBlockSpeeds = Maps.newHashMap();

    @Override
    public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_10780_, ResourceManager p_10781_, ProfilerFiller p_10782_, ProfilerFiller p_10783_, Executor p_10784_, Executor p_10785_) {
        return p_10780_.wait(Unit.INSTANCE).thenRunAsync(this::load, p_10785_);
    }

    public void load() {
        JsonConfigFileUtil.getAndLoad(CONFIG_FILE_NAME, this::serialize, this::deserialize);
    }

    public boolean hasCustomSpeed(Block block) {
        return this.customBlockSpeeds.containsKey(block);
    }

    public double getSpeedFactor(Block block) {
        return this.customBlockSpeeds.getOrDefault(block, 1.0);
    }

    private void serialize(File jsonFile) {
        JsonObject jsonElements = new JsonObject();
        for (SpeedHolderValue value : DEFAULT_BLOCK_SPEEDS) {
            value.serialize(jsonElements);
        }
        JsonConfigFileUtil.saveToFile(jsonFile, jsonElements);
    }

    private void deserialize(FileReader reader) {
        this.customBlockSpeeds.clear();
        JsonObject jsonElements = JsonConfigFileUtil.GSON.fromJson(reader, JsonObject.class);
        for (Map.Entry<String, JsonElement> entry : jsonElements.entrySet()) {
            String key = entry.getKey();
            double speedValue = entry.getValue().getAsDouble();
            if (key.startsWith("#")) {
                TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(key.substring(1)));
                SpeedHolderValue.addValue(tag, speedValue, this.customBlockSpeeds);
            } else {
                ResourceLocation resourcelocation = new ResourceLocation(key);
                if (ForgeRegistries.BLOCKS.containsKey(resourcelocation)) {
                    Block block = ForgeRegistries.BLOCKS.getValue(resourcelocation);
                    SpeedHolderValue.addValue(block, speedValue, this.customBlockSpeeds);
                } else {
                    throw new JsonSyntaxException("Unknown block type '" + resourcelocation + "', valid types are: " + Joiner.on(", ").join(ForgeRegistries.BLOCKS.getKeys()));
                }
            }
        }
    }
}
