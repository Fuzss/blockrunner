package fuzs.blockrunner.fabric.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.ILookupWithData;
import net.neoforged.neoforge.registries.datamaps.IWithData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Holder.Reference.class)
abstract class HolderReferenceFabricMixin<T> implements IWithData<T> {
    @Shadow
    @Final
    private HolderOwner<T> owner;

    @Shadow
    public abstract ResourceKey<T> key();

    @Override
    public <T1> @Nullable T1 getData(DataMapType<T, T1> type) {
        return this.owner instanceof HolderLookup.RegistryLookup<T> lookup ? ((ILookupWithData<T>) lookup).getData(type, this.key()) : null;
    }
}
