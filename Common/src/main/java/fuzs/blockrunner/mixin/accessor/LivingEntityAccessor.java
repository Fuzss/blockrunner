package fuzs.blockrunner.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Accessor("lastPos")
    BlockPos blockrunner$getLastPos();

    @Invoker("shouldRemoveSoulSpeed")
    boolean blockrunner$callShouldRemoveSoulSpeed(BlockState state);
}
