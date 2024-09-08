package fuzs.blockrunner.fabric;

import fuzs.blockrunner.DataMapToken;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public record FabricDataMapToken<R, T>(DataMapType<R, T> type) implements DataMapToken<R, T> {

    public static <R, T> DataMapType<R, T> unwrap(DataMapToken<R, T> token) {
        return ((FabricDataMapToken<R, T>) token).type;
    }
}
