package fuzs.blockrunner.mixin;

import com.google.common.base.Objects;
import fuzs.blockrunner.helper.BlockSpeedAttributeHelper;
import fuzs.blockrunner.world.level.block.data.BlockSpeed;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    @Shadow
    private BlockPos lastPos;

    public LivingEntityMixin(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    protected void getBlockSpeedFactor(CallbackInfoReturnable<Float> callback) {
        // use same block position as the getBlockSpeedFactor implementation
        BlockState blockState = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
        Holder.Reference<Block> holder = blockState.getBlock().builtInRegistryHolder();
        if (BlockSpeed.hasBlockSpeed(holder)) {
            callback.setReturnValue(1.0F);
        }
    }

    @Inject(method = "onChangedBlock", at = @At("TAIL"))
    protected void onChangedBlock(ServerLevel level, BlockPos pos, CallbackInfo callback) {
        this.blockrunner$onChangedBlock();
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo callback) {
        // also run this client-side for players to counter server delay when calculating fov effects
        if ((!this.level().isClientSide() || LivingEntity.class.cast(this) instanceof Player) && onGround
                && this.fallDistance > 0.0F) {
            BlockSpeedAttributeHelper.removeBlockSpeed(LivingEntity.class.cast(this));
            BlockSpeedAttributeHelper.tryAddBlockSpeed(LivingEntity.class.cast(this));
        }
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    public void baseTick(CallbackInfo callback) {
        // also run this client-side for players to counter server delay when calculating fov effects
        if (this.level().isClientSide() && LivingEntity.class.cast(this) instanceof Player) {
            BlockPos blockPos = this.blockPosition();
            if (!Objects.equal(this.lastPos, blockPos)) {
                this.lastPos = blockPos;
                // only run our own onChangedBlock implementation and not the whole vanilla method
                this.blockrunner$onChangedBlock();
            }
        }
    }

    @Unique
    private void blockrunner$onChangedBlock() {
        // check if block not air or player is elytra flying
        if (!this.getBlockStateOn().isAir() || this.isFallFlying()
                || LivingEntity.class.cast(this) instanceof Player player && player.getAbilities().flying) {
            BlockSpeedAttributeHelper.removeBlockSpeed(LivingEntity.class.cast(this));
        }
        BlockSpeedAttributeHelper.tryAddBlockSpeed(LivingEntity.class.cast(this));
    }

    @Shadow
    public abstract boolean isFallFlying();
}
