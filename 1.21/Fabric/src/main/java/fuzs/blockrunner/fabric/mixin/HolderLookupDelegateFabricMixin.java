package fuzs.blockrunner.fabric.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.ILookupWithData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HolderLookup.RegistryLookup.Delegate.class)
interface HolderLookupDelegateFabricMixin<T> extends ILookupWithData<T> {

    @Shadow
    HolderLookup.RegistryLookup<T> parent();

    @Override
    default <A> @Nullable A getData(DataMapType<T, A> attachment, ResourceKey<T> key) {
        return ((ILookupWithData<T>) this.parent()).getData(attachment, key);
    }
}
