package fuzs.blockrunner.fabric;

import com.mojang.serialization.Codec;
import fuzs.blockrunner.DataMapToken;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

public record FabricDataMapToken<R, T>(DataMapType<R, T> type) implements DataMapToken<R, T> {

    @Override
    public ResourceKey<Registry<R>> registryKey() {
        return this.type.registryKey();
    }

    @Override
    public ResourceLocation id() {
        return this.type.id();
    }

    @Override
    public Codec<T> codec() {
        return this.type.codec();
    }

    @Override
    public @Nullable Codec<T> networkCodec() {
        return this.type.networkCodec();
    }

    @Override
    public boolean mandatorySync() {
        return this.type.mandatorySync();
    }

    public static <R, T> DataMapType<R, T> unwrap(DataMapToken<R, T> token) {
        return ((FabricDataMapToken<R, T>) token).type;
    }
}
