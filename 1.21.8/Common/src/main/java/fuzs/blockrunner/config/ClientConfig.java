package fuzs.blockrunner.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(description = "Add a tooltip to blocks that have an altered block speed with the multiplier.")
    public boolean blockSpeedMultiplierTooltip = true;
    @Config(description = "Prevent running on blocks with higher speeds from changing the field of view.")
    public boolean disableFieldOfViewChanges = true;
}
