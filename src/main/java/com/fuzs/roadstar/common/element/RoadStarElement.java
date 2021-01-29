package com.fuzs.roadstar.common.element;

import com.fuzs.puzzleslib_rs.config.json.JsonCommandUtil;
import com.fuzs.puzzleslib_rs.config.json.JsonConfigFileUtil;
import com.fuzs.puzzleslib_rs.element.AbstractElement;
import com.fuzs.puzzleslib_rs.element.side.ICommonElement;
import com.fuzs.roadstar.RoadStar;
import com.fuzs.roadstar.common.builder.RoadBlocksBuilder;
import net.minecraftforge.event.RegisterCommandsEvent;

public class RoadStarElement extends AbstractElement implements ICommonElement {

    private final String jsonRoadBlocksName = RoadStar.MODID + ".json";
    
    @Override
    public String getDescription() {

        return "";
    }

    @Override
    public void setupCommon() {

        this.addListener(this::onRegisterCommands);
    }

    @Override
    public void loadCommon() {

        JsonConfigFileUtil.load(this.jsonRoadBlocksName, RoadBlocksBuilder::serialize, RoadBlocksBuilder::deserialize);
    }

    private void onRegisterCommands(final RegisterCommandsEvent evt) {

        evt.getDispatcher().register(JsonCommandUtil.createReloadCommand(this.jsonRoadBlocksName, RoadStar.MODID, "command.reload.road_blocks", RoadBlocksBuilder::serialize, RoadBlocksBuilder::deserialize));
    }

}
