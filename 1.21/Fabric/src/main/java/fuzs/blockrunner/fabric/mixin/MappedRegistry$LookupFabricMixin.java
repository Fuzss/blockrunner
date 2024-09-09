package fuzs.blockrunner.fabric.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.core.MappedRegistry$1")
abstract class MappedRegistry$LookupFabricMixin<T> implements HolderLookup.RegistryLookup<T> {
    @Shadow
    @Final
    private MappedRegistry<T> this$0;

    @Override
    public <A> @Nullable A getData(DataMapType<T, A> attachment, ResourceKey<T> key) {
        return this.this$0.getData(attachment, key);
    }
}
