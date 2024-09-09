package fuzs.blockrunner.fabric.mixin;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.registries.datamaps.ILookupWithData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HolderLookup.RegistryLookup.class)
interface HolderLookup$RegistryLookupFabricMixin<T> extends ILookupWithData<T> {
}
