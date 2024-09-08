/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.registries.datamaps;

import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

/**
 * Event fired on the {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS game event bus} when the data maps of a
 * registry have either been {@linkplain UpdateCause#CLIENT_SYNC synced to the client} or
 * {@linkplain UpdateCause#SERVER_RELOAD reloaded on the server}.
 * <p>
 * This event can be used to build caches (like weighed lists) or for post-processing the data map values. <br> Remember
 * however that the data map values should <strong>not</strong> end up referencing their owner, as they're not copied
 * when attached to tags.
 */
@FunctionalInterface
public interface DataMapsUpdatedCallback {
    Event<DataMapsUpdatedCallback> EVENT = FabricEventFactory.create(DataMapsUpdatedCallback.class);

    /**
     * @param registryAccess {@return a registry access}
     * @param registry       {@return the registry that had its data maps updated}
     * @param cause          {@return the reason for the update}
     */
    void onDataMapsUpdated(RegistryAccess registryAccess, Registry<?> registry, UpdateCause cause);

    enum UpdateCause {
        /**
         * The data maps have been synced to the client.
         *
         * @implNote An event with this cause is <i>not</i> fired for the host of a single-player world, or for
         *         any {@linkplain Connection#isMemoryConnection() in-memory} connections.
         */
        CLIENT_SYNC,
        /**
         * The data maps have been reloaded on the server.
         *
         * @implNote Events with this cause are fired during the
         *         {@linkplain SimplePreparableReloadListener#apply(Object, ResourceManager, ProfilerFiller) apply
         *         phase} of a reload listener, and <strong>not</strong> after the reload is complete.
         */
        SERVER_RELOAD
    }
}
