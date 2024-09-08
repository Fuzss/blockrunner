package fuzs.blockrunner.fabric;

import com.mojang.serialization.Codec;
import fuzs.blockrunner.DataMapToken;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryManager;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

public final class CommonAbstractionsImpl {

    @Nullable
    public static <R, T> T getData(DataMapToken<R, T> token, Holder<R> holder) {
        return holder.getData(FabricDataMapToken.unwrap(token));
    }

    public static <R, T> DataMapToken<R, T> registerDataMap(ResourceLocation id, ResourceKey<Registry<R>> registry, Codec<T> codec) {
        return registerDataMap(DataMapType.builder(id, registry, codec).build());
    }

    public static <R, T> DataMapToken<R, T> registerDataMap(ResourceLocation id, ResourceKey<Registry<R>> registry, Codec<T> codec, Codec<T> networkCodec, boolean mandatory) {
        return registerDataMap(DataMapType.builder(id, registry, codec).synced(networkCodec, mandatory).build());
    }

    static <R, T> DataMapToken<R, T> registerDataMap(DataMapType<R, T> type) {
        RegistryManager.registerDataMap(type);
        return new FabricDataMapToken<>(type);
    }
}
