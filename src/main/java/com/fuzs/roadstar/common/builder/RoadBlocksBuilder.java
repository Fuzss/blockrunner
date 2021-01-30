package com.fuzs.roadstar.common.builder;

import com.fuzs.puzzleslib_rs.config.json.JsonConfigFileUtil;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Set;

public class RoadBlocksBuilder {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(RoadEntry.class, new RoadEntry.Serializer())
            .registerTypeAdapter(RoadEntry.BlockEntry.class, new RoadEntry.Serializer())
            .registerTypeAdapter(RoadEntry.TagEntry.class, new RoadEntry.Serializer())
            .create();

    public static final Set<RoadEntry> ROAD_BLOCKS = Sets.newHashSet();
    private static final Set<RoadEntry> DEFAULT_ROAD_BLOCKS = new RoadEntry.Builder()
            .add(Blocks.GRASS_PATH, 1.5)
            .add(BlockTags.STONE_BRICKS, 1.2)
            .build();

    public static void serialize(String jsonName, File jsonFile) {

        Type token = new TypeToken<Set<RoadEntry>>(){}.getType();
        JsonElement jsonElement = GSON.toJsonTree(DEFAULT_ROAD_BLOCKS, token);
        JsonConfigFileUtil.saveToFile(jsonName, jsonFile, jsonElement);
    }

    public static void deserialize(FileReader reader) {

        Type token = new TypeToken<Set<RoadEntry>>(){}.getType();
        Set<RoadEntry> set = GSON.fromJson(reader, token);
        ROAD_BLOCKS.clear();
        ROAD_BLOCKS.addAll(set);
    }

    public static boolean isRoadBlock(Block block) {

        return ROAD_BLOCKS.stream().anyMatch(entry -> entry.test(block));
    }

    public static double getSpeedFactor(Block block) {

        return ROAD_BLOCKS.stream()
                .filter(entry -> entry.test(block))
                .findFirst()
                .map(RoadEntry::getSpeedFactor)
                .orElse(1.0);
    }

}
