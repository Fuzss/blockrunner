package fuzs.blockrunner.neoforge;

import com.mojang.serialization.Codec;
import fuzs.blockrunner.DataMapToken;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.Nullable;

public final class CommonAbstractionsImpl {

    @Nullable
    public static <R, T> T getData(DataMapToken<R, T> token, Holder<R> holder) {
        return holder.getData(NeoForgeDataMapToken.unwrap(token));
    }

    public static <R, T> DataMapToken<R, T> registerDataMap(ResourceLocation id, ResourceKey<Registry<R>> registry, Codec<T> codec) {
        return registerDataMap(DataMapType.builder(id, registry, codec).build());
    }

    public static <R, T> DataMapToken<R, T> registerDataMap(ResourceLocation id, ResourceKey<Registry<R>> registry, Codec<T> codec, Codec<T> networkCodec, boolean mandatory) {
        return registerDataMap(DataMapType.builder(id, registry, codec).synced(networkCodec, mandatory).build());
    }

    static <R, T> DataMapToken<R, T> registerDataMap(DataMapType<R, T> type) {
        IEventBus eventBus = NeoForgeModContainerHelper.getActiveModEventBus();
        eventBus.addListener((final RegisterDataMapTypesEvent evt) -> {
            evt.register(type);
        });
        return new NeoForgeDataMapToken<>(type);
    }
}
