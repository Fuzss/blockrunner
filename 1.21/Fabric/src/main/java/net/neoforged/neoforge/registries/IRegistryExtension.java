/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * An extension for {@link Registry}, adding some additional functionality to vanilla registries, such as
 * callbacks and ID limits.
 * 
 * @param <T> the type of registry entries
 */
public interface IRegistryExtension<T> {
    /**
     * {@return the data map value attached with the object with the key, or {@code null} if there's no attached value}
     *
     * @param type the type of the data map
     * @param key  the object to get the value for
     * @param <A>  the data type
     */
    @Nullable
    default <A> A getData(DataMapType<T, A> type, ResourceKey<T> key) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@return the data map of the given {@code type}}
     *
     * @param <A> the data type
     */
    default <A> Map<ResourceKey<T>, A> getDataMap(DataMapType<T, A> type) {
        throw new UnsupportedOperationException();
    }
}
