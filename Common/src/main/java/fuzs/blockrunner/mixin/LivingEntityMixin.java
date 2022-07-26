package fuzs.blockrunner.mixin;

import fuzs.blockrunner.data.BlockSpeedManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    protected void checkFallDamage$head(double y, boolean onGroundIn, BlockState state, BlockPos pos, CallbackInfo callback) {
        if (!this.level.isClientSide && onGroundIn && this.fallDistance > 0.0F) {
            this.custom$removeCustomBlockSpeed();
            this.custom$tryAddCustomBlockSpeed();
        }
    }

    @Unique
    protected void custom$removeCustomBlockSpeed() {
        AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeinstance != null) {
            if (attributeinstance.getModifier(BlockSpeedManager.SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID) != null) {
                attributeinstance.removeModifier(BlockSpeedManager.SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID);
            }
        }
    }

    @Unique
    protected void custom$tryAddCustomBlockSpeed() {
        if (!this.getBlockStateOn().isAir()) {
            if ((!((Object) this instanceof Player player) || !player.getAbilities().flying) && this.custom$onCustomSpeedBlock()) {
                double customSpeed = BlockSpeedManager.INSTANCE.getSpeedFactor(this.level.getBlockState(this.custom$getBlockPosBelowThatAffectsMyMovement2()).getBlock());
                AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attributeinstance == null || customSpeed == 1.0) {
                    return;
                }
                double baseValue = attributeinstance.getBaseValue();
                customSpeed = customSpeed * baseValue - baseValue;
                attributeinstance.addTransientModifier(new AttributeModifier(BlockSpeedManager.SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID, "Custom block speed boost", customSpeed, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Shadow
    public abstract AttributeInstance getAttribute(Attribute p_21052_);

    @Unique
    protected boolean custom$onCustomSpeedBlock() {
        return BlockSpeedManager.INSTANCE.hasCustomSpeed(this.level.getBlockState(this.custom$getBlockPosBelowThatAffectsMyMovement2()).getBlock());
    }

    @Unique
    protected BlockPos custom$getBlockPosBelowThatAffectsMyMovement2() {
        // reduce 0.5000001 (8/16) to 0.4375001 (7/16) to allow slabs to work properly
        return new BlockPos(this.getX(), this.getBoundingBox().minY - 0.4375001, this.getZ());
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    protected void getBlockSpeedFactor$head(CallbackInfoReturnable<Float> callback) {
        if (this.custom$onCustomSpeedBlock()) callback.setReturnValue(1.0F);
    }

    @Inject(method = "onChangedBlock", at = @At("TAIL"))
    protected void onChangedBlock$tail(BlockPos pos, CallbackInfo callback) {
        // check if block not air or player is elytra flying
        if (this.shouldRemoveSoulSpeed(this.getBlockStateOn())) {
            this.custom$removeCustomBlockSpeed();
        }
        this.custom$tryAddCustomBlockSpeed();
    }

    @Shadow
    protected abstract boolean shouldRemoveSoulSpeed(BlockState state);
}
