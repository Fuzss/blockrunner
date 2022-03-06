package fuzs.blockrunner.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import fuzs.blockrunner.BlockRunner;
import fuzs.puzzleslib.json.JsonConfigFileUtil;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class RoadBlocksManager extends SimpleJsonResourceReloadListener {
    public static final RoadBlocksManager INSTANCE = new RoadBlocksManager();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(SpeedHolderValue.class, new SpeedHolderValue.Serializer())
            .registerTypeAdapter(SpeedHolderValue.BlockValue.class, new SpeedHolderValue.Serializer())
            .registerTypeAdapter(SpeedHolderValue.TagValue.class, new SpeedHolderValue.Serializer())
            .setPrettyPrinting().disableHtmlEscaping().create();
    private static final Set<SpeedHolderValue> DEFAULT_ROAD_BLOCKS = new SpeedHolderValue.Builder()
            .add(Blocks.DIRT_PATH, 1.5)
            .add(BlockTags.STONE_BRICKS, 1.2)
            .get();

    private final ResourceLocation fileName = new ResourceLocation(BlockRunner.MOD_ID, "road_blocks");
    private Map<Block, Double> customBlockSpeeds = Maps.newHashMap();

    public RoadBlocksManager() {
        super(GSON, BlockRunner.MOD_ID);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> p_10793_, ResourceManager manager, ProfilerFiller profiler) {

    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, @Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn) {

        Set<SpeedHolderValue> roadBlocks = Sets.newHashSet();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {

            if (entry.getKey().equals(this.fileName)) {

                Type token = new TypeToken<Set<SpeedHolderValue>>(){}.getType();
                roadBlocks = GSON.fromJson(entry.getValue(), token);
            } else {

                BlockRunner.LOGGER.warn("Datapack tried to define unused {} road blocks file, ignoring", entry.getKey());
                BlockRunner.LOGGER.warn("Only {} is a valid file name", this.fileName.getPath() + ".json");
            }
        }

        this.customBlockSpeeds = roadBlocks;
    }

    public boolean hasCustomSpeed(Block block) {
        return this.customBlockSpeeds.containsKey(block);
    }

    public double getSpeedFactor(Block block) {
        return this.customBlockSpeeds.getOrDefault(block, 1.0);
    }

    public static void serialize(String jsonName, File jsonFile) {
        Type token = new TypeToken<Set<SpeedHolderValue>>(){}.getType();
        JsonElement jsonElement = GSON.toJsonTree(DEFAULT_ROAD_BLOCKS, token);
        JsonConfigFileUtil.saveToFile(jsonName, jsonFile, jsonElement);
    }
}
