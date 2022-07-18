package fuzs.blockrunner.api.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public interface ComputeFovModifierCallback {
    Event<ComputeFovModifierCallback> EVENT = EventFactory.createArrayBacked(ComputeFovModifierCallback.class, listeners -> (Player player, float fovModifier) -> {
        for (ComputeFovModifierCallback event : listeners) {
            fovModifier = event.onComputeFovModifier(player, fovModifier);
        }
        return fovModifier;
    });

    /**
     * called when computing the field of view modifier on the client, mostly depending on {@link Attributes#MOVEMENT_SPEED}
     *
     * @param player        the client player this is calculated for
     * @param fovModifier   modifier as calculated by vanilla
     * @return              the modified modifier
     */
    float onComputeFovModifier(Player player, float fovModifier);
}
