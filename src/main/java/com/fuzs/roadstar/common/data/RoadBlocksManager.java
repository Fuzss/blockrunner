package com.fuzs.roadstar.common.data;

import com.fuzs.puzzleslib_rs.config.json.JsonConfigFileUtil;
import com.fuzs.roadstar.RoadStar;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class RoadBlocksManager extends JsonReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(RoadEntry.class, new RoadEntry.Serializer())
            .registerTypeAdapter(RoadEntry.BlockEntry.class, new RoadEntry.Serializer())
            .registerTypeAdapter(RoadEntry.TagEntry.class, new RoadEntry.Serializer())
            .setPrettyPrinting().disableHtmlEscaping().create();
    private static final Set<RoadEntry> DEFAULT_ROAD_BLOCKS = new RoadEntry.Builder()
            .add(Blocks.GRASS_PATH, 1.5)
            .add(BlockTags.STONE_BRICKS, 1.2)
            .get();

    private final ResourceLocation roadBlocksName = new ResourceLocation(RoadStar.MODID, "road_blocks");
    private Set<RoadEntry> roadBlocks = Sets.newHashSet();

    public RoadBlocksManager() {

        super(GSON, RoadStar.MODID);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, @Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn) {

        Set<RoadEntry> roadBlocks = Sets.newHashSet();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {

            if (entry.getKey().equals(this.roadBlocksName)) {

                Type token = new TypeToken<Set<RoadEntry>>(){}.getType();
                roadBlocks = GSON.fromJson(entry.getValue(), token);
            } else {

                RoadStar.LOGGER.warn("Datapack tried to define unused {} road blocks file, ignoring", entry.getKey());
                RoadStar.LOGGER.warn("Only {} is a valid file name", this.roadBlocksName.getPath() + ".json");
            }
        }

        this.roadBlocks = roadBlocks;
    }

    public boolean isRoadBlock(Block block) {

        return this.roadBlocks.stream().anyMatch(entry -> entry.test(block));
    }

    public double getSpeedFactor(Block block) {

        return this.roadBlocks.stream()
                .filter(entry -> entry.test(block))
                .findFirst()
                .map(RoadEntry::getSpeedFactor)
                .orElse(1.0);
    }

    public static void serialize(String jsonName, File jsonFile) {

        Type token = new TypeToken<Set<RoadEntry>>(){}.getType();
        JsonElement jsonElement = GSON.toJsonTree(DEFAULT_ROAD_BLOCKS, token);
        JsonConfigFileUtil.saveToFile(jsonName, jsonFile, jsonElement);
    }

}
