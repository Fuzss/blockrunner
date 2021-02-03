package com.fuzs.roadstar.common.element;

import com.fuzs.puzzleslib_rs.element.AbstractElement;
import com.fuzs.puzzleslib_rs.element.side.ICommonElement;
import com.fuzs.roadstar.common.data.RoadBlocksManager;
import net.minecraftforge.event.AddReloadListenerEvent;

public class RoadStarElement extends AbstractElement implements ICommonElement {

    public final RoadBlocksManager roadBlocksManager = new RoadBlocksManager();
    
    @Override
    public String getDescription() {

        return "Fine-tune your movement speed when walking on certain blocks.";
    }

    @Override
    public void setupCommon() {

        this.addListener(this::onAddReloadListener);
    }

    private void onAddReloadListener(final AddReloadListenerEvent evt) {

        evt.addListener(this.roadBlocksManager);
    }

}
