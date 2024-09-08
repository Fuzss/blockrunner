package fuzs.blockrunner.fabric.mixin;

import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.IRegistryExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Registry.class)
interface RegistryFabricMixin<T> extends IRegistryExtension<T> {
}
