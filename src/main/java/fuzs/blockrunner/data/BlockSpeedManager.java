package fuzs.blockrunner.data;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import fuzs.blockrunner.BlockRunner;
import fuzs.blockrunner.network.message.S2CBlockSpeedMessage;
import fuzs.puzzleslib.core.EnvTypeExecutor;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BlockSpeedManager implements PreparableReloadListener {
    public static final BlockSpeedManager INSTANCE = new BlockSpeedManager();
    private static final String CONFIG_FILE_NAME = BlockRunner.MOD_ID + ".json";
    private static final Set<SpeedHolderValue> DEFAULT_BLOCK_SPEEDS = new SpeedHolderValue.Builder()
            .add(Blocks.DIRT_PATH, 1.5)
            .add(BlockTags.STONE_BRICKS, 1.2)
            .build();

    private final Set<SpeedHolderValue> customBlockSpeedValues = Sets.newHashSet();
    private Map<Block, Double> customBlockSpeeds;

    @Override
    public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_10780_, ResourceManager p_10781_, ProfilerFiller p_10782_, ProfilerFiller p_10783_, Executor p_10784_, Executor p_10785_) {
        return p_10780_.wait(Unit.INSTANCE).thenRunAsync(this::load, p_10785_);
    }

    private void load() {
        JsonConfigFileUtil.getAndLoad(CONFIG_FILE_NAME, this::serialize, this::deserialize);
        EnvTypeExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> BlockRunner.NETWORK.sendToAll(new S2CBlockSpeedMessage(this.serialize(this.customBlockSpeedValues))));
    }

    private void dissolve() {
        if (this.customBlockSpeeds == null) {
            Map<Block, Double> customBlockSpeeds = Maps.newHashMap();
            try {
                for (SpeedHolderValue value : this.customBlockSpeedValues) {
                    value.addValues(customBlockSpeeds);
                }
            } catch (Exception e) {
                BlockRunner.LOGGER.error(e.getMessage());
                customBlockSpeeds.clear();
            }
            this.customBlockSpeeds = customBlockSpeeds;
        }
    }

    public boolean hasCustomSpeed(Block block) {
        this.dissolve();
        return this.customBlockSpeeds.containsKey(block);
    }

    public double getSpeedFactor(Block block) {
        this.dissolve();
        return this.customBlockSpeeds.getOrDefault(block, 1.0);
    }

    private void serialize(File jsonFile) {
        JsonConfigFileUtil.saveToFile(jsonFile, this.serialize(DEFAULT_BLOCK_SPEEDS));
    }

    private JsonObject serialize(Set<SpeedHolderValue> values) {
        JsonObject jsonElements = new JsonObject();
        for (SpeedHolderValue value : values) {
            value.serialize(jsonElements);
        }
        return jsonElements;
    }

    private void deserialize(FileReader reader) {
        this.deserialize(JsonConfigFileUtil.GSON.fromJson(reader, JsonObject.class));
    }

    public void deserialize(JsonObject jsonElements) {
        this.customBlockSpeedValues.clear();
        this.customBlockSpeeds = null;
        Set<SpeedHolderValue> customBlockSpeedValues = Sets.newHashSet();
        for (Map.Entry<String, JsonElement> entry : jsonElements.entrySet()) {
            String key = entry.getKey();
            double speedValue = entry.getValue().getAsDouble();
            if (key.startsWith("#")) {
                TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(key.substring(1)));
                customBlockSpeedValues.add(new SpeedHolderValue.TagValue(tag, speedValue));
            } else {
                ResourceLocation resourcelocation = new ResourceLocation(key);
                if (ForgeRegistries.BLOCKS.containsKey(resourcelocation)) {
                    Block block = ForgeRegistries.BLOCKS.getValue(resourcelocation);
                    customBlockSpeedValues.add(new SpeedHolderValue.BlockValue(block, speedValue));
                } else {
                    throw new JsonSyntaxException("Unknown block type '" + resourcelocation + "', valid types are: " + Joiner.on(", ").join(ForgeRegistries.BLOCKS.getKeys()));
                }
            }
        }
        this.customBlockSpeedValues.addAll(customBlockSpeedValues);
    }
}
