package com.fuzs.roadstar.common;

import com.fuzs.puzzleslib_rs.element.AbstractElement;
import com.fuzs.puzzleslib_rs.element.registry.ElementRegistry;
import com.fuzs.puzzleslib_rs.element.side.ISidedElement;
import com.fuzs.roadstar.RoadStar;
import com.fuzs.roadstar.common.element.RoadStarElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public class RoadStarElements extends ElementRegistry {

    public static final AbstractElement ROAD_STAR = register("road_star", RoadStarElement::new);

    /**
     * create overload so this class and its elements are loaded
     */
    public static void setup(String namespace) {

        ElementRegistry.setup(namespace);
    }

    /**
     * register an element to the namespace of the active mod container
     * @param key identifier for this element
     * @param supplier supplier for element to be registered
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    private static <T extends AbstractElement & ISidedElement> AbstractElement register(String key, Supplier<T> supplier) {

        return register(RoadStar.MODID, key, supplier, FMLEnvironment.dist);
    }

    /**
     * register an element to the namespace of the active mod container
     * @param key identifier for this element
     * @param supplier supplier for element to be registered
     * @param dist physical side to register on
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    private static <T extends AbstractElement & ISidedElement> AbstractElement register(String key, Supplier<T> supplier, Dist dist) {

        return register(RoadStar.MODID, key, supplier, dist);
    }

}
