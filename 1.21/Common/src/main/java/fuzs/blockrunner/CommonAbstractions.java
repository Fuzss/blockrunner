package fuzs.blockrunner;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class CommonAbstractions {

    @Nullable
    @ExpectPlatform
    public static <R, T> T getData(DataMapToken<R, T> token, Holder<R> holder) {
        throw new RuntimeException();
    }

    @ExpectPlatform
    public static <R, T> DataMapToken<R, T> registerDataMap(ResourceLocation id, ResourceKey<Registry<R>> registry, Codec<T> codec) {
        throw new RuntimeException();
    }

    @ExpectPlatform
    public static <R, T> DataMapToken<R, T> registerDataMap(ResourceLocation id, ResourceKey<Registry<R>> registry, Codec<T> codec, Codec<T> networkCodec, boolean mandatory) {
        throw new RuntimeException();
    }
}
