package net.neoforged.neoforge.registries.datamaps;

import net.minecraft.resources.ResourceKey;

import java.util.Map;

public interface IRegistryWithData<T> {

    Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> getDataMaps();
}
