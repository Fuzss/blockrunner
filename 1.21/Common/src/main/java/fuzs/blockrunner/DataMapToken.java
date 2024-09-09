package fuzs.blockrunner;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface DataMapToken<R, T> {

    /**
     * {@return the key of the registry this data map is for}
     */
    ResourceKey<Registry<R>> registryKey();

    /**
     * {@return the ID of this data map}
     */
    ResourceLocation id();

    /**
     * {@return the codec used to decode values}
     */
    Codec<T> codec();

    /**
     * {@return the codec used to sync values}
     */
    @Nullable Codec<T> networkCodec();

    /**
     * {@return {@code true} if this data map must be present on the client, and {@code false} otherwise}
     */
    boolean mandatorySync();
}
