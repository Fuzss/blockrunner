package com.fuzs.roadstar;

import com.fuzs.puzzleslib_rs.PuzzlesLib;
import com.fuzs.roadstar.common.RoadStarElements;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(RoadStar.MODID)
public class RoadStar extends PuzzlesLib {

    public static final String MODID = "roadstar";
    public static final String NAME = "Road Star";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public RoadStar() {

        super();
        RoadStarElements.setup(MODID);
    }

}
