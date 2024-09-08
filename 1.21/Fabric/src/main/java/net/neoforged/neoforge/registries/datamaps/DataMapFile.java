/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.registries.datamaps;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record DataMapFile<T, R>(
        boolean replace,
        Map<Either<TagKey<R>, ResourceKey<R>>, Optional<DataMapEntry<T>>> values,
        List<DataMapEntry.Removal<T, R>> removals) {
    public static <T, R> Codec<DataMapFile<T, R>> codec(ResourceKey<Registry<R>> registryKey, DataMapType<R, T> dataMap) {
        final Codec<Either<TagKey<R>, ResourceKey<R>>> tagOrValue = ExtraCodecs.TAG_OR_ELEMENT_ID.xmap(
                l -> l.tag() ? Either.left(TagKey.create(registryKey, l.id())) : Either.right(ResourceKey.create(registryKey, l.id())),
                e -> e.map(t -> new ExtraCodecs.TagOrElementLocation(t.location(), true), r -> new ExtraCodecs.TagOrElementLocation(r.location(), false)));

        final Codec<List<DataMapEntry.Removal<T, R>>> removalsCodec;
        if (dataMap instanceof AdvancedDataMapType<R, T, ?>) {
            final var removalCodec = DataMapEntry.Removal.codec(tagOrValue, dataMap);
            final AdvancedDataMapType<R, T, DataMapValueRemover<R, T>> advanced = (AdvancedDataMapType<R, T, DataMapValueRemover<R, T>>) dataMap;
            removalsCodec = Codec.withAlternative(
                    Codec.withAlternative(removalCodec.listOf(), Codec.of(Encoder.error("Cannot encode with decode-only codec!"), tagOrValue.listOf()
                            .map(l -> l.stream().map(k -> new DataMapEntry.Removal<T, R>(k, Optional.empty())).toList()))),
                    Codec.of(Encoder.error("Cannot encode with decode-only codec!"), ExtraCodecs.strictUnboundedMap(tagOrValue, advanced.remover())
                            .map(map -> map.entrySet().stream()
                                    .map(entry -> new DataMapEntry.Removal<>(entry.getKey(), Optional.of(entry.getValue()))).toList())));
        } else {
            removalsCodec = tagOrValue.listOf()
                    .xmap(l -> l.stream().map(k -> new DataMapEntry.Removal<T, R>(k, Optional.empty())).toList(),
                            rm -> rm.stream().map(DataMapEntry.Removal::key).toList());
        }

        return RecordCodecBuilder.create(in -> in.group(
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(DataMapFile::replace),
                ExtraCodecs.strictUnboundedMap(tagOrValue, ExtraCodecs.optionalEmptyMap(DataMapEntry.codec(dataMap))).fieldOf("values").forGetter(DataMapFile::values),
                removalsCodec.optionalFieldOf("remove", List.of()).forGetter(DataMapFile::removals))
                .apply(in, DataMapFile::new));
    }
}
