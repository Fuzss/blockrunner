package com.fuzs.roadstar.common.builder;

import com.fuzs.puzzleslib_rs.config.json.JsonConfigFileUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.Map;

public class RoadBlocksManager extends JsonReloadListener {

    public RoadBlocksManager() {

        super(JsonConfigFileUtil.GSON, "gameplay");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {

    }

}
