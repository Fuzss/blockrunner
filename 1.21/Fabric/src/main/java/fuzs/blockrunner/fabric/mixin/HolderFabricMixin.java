package fuzs.blockrunner.fabric.mixin;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.datamaps.IWithData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Holder.class)
interface HolderFabricMixin<T> extends IWithData<T> {
}
