package com.fuzs.roadstar.common.element;

import com.fuzs.puzzleslib_rs.config.json.JsonCommandUtil;
import com.fuzs.puzzleslib_rs.config.json.JsonConfigFileUtil;
import com.fuzs.puzzleslib_rs.element.AbstractElement;
import com.fuzs.puzzleslib_rs.element.side.ICommonElement;
import com.fuzs.roadstar.RoadStar;
import com.fuzs.roadstar.common.builder.RoadBlocksBuilder;
import com.fuzs.roadstar.common.builder.RoadBlocksManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

public class RoadStarElement extends AbstractElement implements ICommonElement {

    public final RoadBlocksManager roadBlocksManager = new RoadBlocksManager();
    private final String jsonRoadBlocksName = RoadStar.MODID + ".json";
    
    @Override
    public String getDescription() {

        return "";
    }

    @Override
    public void setupCommon() {

//        this.addListener(this::onAddReloadListener);
        this.addListener(this::onRegisterCommands);
    }

    @Override
    public void loadCommon() {

        JsonConfigFileUtil.load(this.jsonRoadBlocksName, RoadBlocksBuilder::serialize, RoadBlocksBuilder::deserialize);
    }

    private void onAddReloadListener(final AddReloadListenerEvent evt) {

        evt.addListener(this.roadBlocksManager);
    }

    private void onRegisterCommands(final RegisterCommandsEvent evt) {

        evt.getDispatcher().register(JsonCommandUtil.createReloadCommand(this.jsonRoadBlocksName, RoadStar.MODID, "command.reload.road_blocks", RoadBlocksBuilder::serialize, RoadBlocksBuilder::deserialize));
    }

}
