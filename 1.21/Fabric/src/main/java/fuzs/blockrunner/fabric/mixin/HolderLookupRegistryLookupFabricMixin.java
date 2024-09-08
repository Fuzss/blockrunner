package fuzs.blockrunner.fabric.mixin;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.registries.datamaps.ILookupWithData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HolderLookup.RegistryLookup.class)
interface HolderLookupRegistryLookupFabricMixin<T> extends ILookupWithData<T> {
}
