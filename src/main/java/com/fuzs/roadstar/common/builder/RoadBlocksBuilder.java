package com.fuzs.roadstar.common.builder;

import com.fuzs.puzzleslib_rs.config.deserialize.EntryCollectionBuilder;
import com.fuzs.puzzleslib_rs.config.json.JsonConfigFileUtil;
import com.fuzs.puzzleslib_rs.config.json.JsonSerializationUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoadBlocksBuilder {

    private static final int FILE_FORMAT = 1;
    public static final Map<Block, Double> ROAD_BLOCKS = Maps.newHashMap();
    private static final Map<Block, Double> DEFAULT_ROAD_BLOCKS = new ImmutableMap.Builder<Block, Double>()
            .put(Blocks.GRASS_PATH, 1.5)
            .build();

    public static void serialize(String jsonName, File jsonFile) {

        JsonObject jsonobject = JsonSerializationUtil.getConfigBase(FILE_FORMAT, EntryCollectionBuilder.CONFIG_STRING);
        DEFAULT_ROAD_BLOCKS.forEach((key, value) -> jsonobject.addProperty(Objects.requireNonNull(key.getRegistryName()).toString(), value));

        JsonConfigFileUtil.saveToFile(jsonName, jsonFile, jsonobject);
    }

    public static void deserialize(FileReader reader) {

        JsonObject jsonObject = JsonConfigFileUtil.GSON.fromJson(reader, JsonObject.class);
        int fileFormat = JsonSerializationUtil.readFileFormat(jsonObject);
        if (fileFormat != FILE_FORMAT) {

            updateRoadBlocks(DEFAULT_ROAD_BLOCKS);
            return;
        }

        List<String> input = JsonSerializationUtil.getJsonElementStream(jsonObject)
                .map(entry -> entry.getKey().concat("," + entry.getValue().toString()))
                .collect(Collectors.toList());

        Map<Block, Double> roadBlocks = new EntryCollectionBuilder<>(ForgeRegistries.BLOCKS).buildEntryMap(input, (entry, value) -> value[0] > 0.0 && value[0] < 5.0, "Speed factor out of bounds").entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

        updateRoadBlocks(roadBlocks);
    }

    private static void updateRoadBlocks(Map<Block, Double> roadBlocks) {

        ROAD_BLOCKS.clear();
        ROAD_BLOCKS.putAll(roadBlocks);
    }

}
