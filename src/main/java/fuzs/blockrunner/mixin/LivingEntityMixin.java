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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private static final UUID SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID = UUID.fromString("23237052-61AD-11EB-AE93-0242AC130002");

    public LivingEntityMixin(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    protected void checkFallDamage$head(double y, boolean onGroundIn, BlockState state, BlockPos pos, CallbackInfo callbackInfo) {
        if (!this.level.isClientSide && onGroundIn && this.fallDistance > 0.0F) {
            this.removeCustomBlockSpeed();
            this.tryAddCustomBlockSpeed();
        }
    }

    @Unique
    protected void removeCustomBlockSpeed() {
        AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeinstance != null) {
            if (attributeinstance.getModifier(SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID) != null) {
                attributeinstance.removeModifier(SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID);
            }
        }
    }

    @Unique
    protected void tryAddCustomBlockSpeed() {
        if (!this.getBlockStateOn().isAir()) {
            if (this.onCustomSpeedBlock()) {
                double customSpeed = BlockSpeedManager.INSTANCE.getSpeedFactor(this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock());
                AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attributeinstance == null || customSpeed == 1.0) {
                    return;
                }
                double baseValue = attributeinstance.getBaseValue();
                customSpeed = customSpeed * baseValue - baseValue;
                attributeinstance.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID, "Custom block speed boost", customSpeed, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Shadow
    public abstract AttributeInstance getAttribute(Attribute p_21052_);

    @Unique
    protected boolean onCustomSpeedBlock() {
        return BlockSpeedManager.INSTANCE.hasCustomSpeed(this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock());
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    protected void getBlockSpeedFactor$head(CallbackInfoReturnable<Float> callbackInfo) {
        if (this.onCustomSpeedBlock()) callbackInfo.setReturnValue(1.0F);
    }

    @Inject(method = "onChangedBlock", at = @At("TAIL"))
    protected void onChangedBlock$tail(BlockPos pos, CallbackInfo callbackInfo) {
        // check if block not air or player is elytra flying
        if (this.shouldRemoveSoulSpeed(this.getBlockStateOn())) {
            this.removeCustomBlockSpeed();
        }
        this.tryAddCustomBlockSpeed();
    }

    @Shadow
    protected abstract boolean shouldRemoveSoulSpeed(BlockState state);
}
