package fuzs.blockrunner.neoforge;

import fuzs.blockrunner.DataMapToken;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public record NeoForgeDataMapToken<R, T>(DataMapType<R, T> type) implements DataMapToken<R, T> {

    public static <R, T> DataMapType<R, T> unwrap(DataMapToken<R, T> token) {
        return ((NeoForgeDataMapToken<R, T>) token).type;
    }
}
