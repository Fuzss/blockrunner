package net.neoforged.neoforge.registries.datamaps;

import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public interface ILookupWithData<T> {

    @Nullable
    default <A> A getData(DataMapType<T, A> attachment, ResourceKey<T> key) {
        return null;
    }
}
