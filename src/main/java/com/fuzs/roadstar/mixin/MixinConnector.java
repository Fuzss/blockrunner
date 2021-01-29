package com.fuzs.roadstar.mixin;

import com.fuzs.roadstar.RoadStar;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

@SuppressWarnings("unused")
public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {

        Mixins.addConfiguration("META-INF/" + RoadStar.MODID + ".mixins.json");
    }

}
