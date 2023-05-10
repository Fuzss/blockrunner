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
abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    protected void getBlockSpeedFactor(CallbackInfoReturnable<Float> callback) {
        // use same block position as the getBlockSpeedFactor implementation
        if (BlockSpeedManager.INSTANCE.hasBlockSpeed(this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock())) {
            callback.setReturnValue(1.0F);
        }
    }

    @Inject(method = "onChangedBlock", at = @At("TAIL"))
    protected void onChangedBlock(BlockPos pos, CallbackInfo callback) {
        // check if block not air or player is elytra flying
        if (this.shouldRemoveSoulSpeed(this.getBlockStateOn())) {
            this.blockrunner$removeBlockSpeed();
        }
        this.blockrunner$tryAddBlockSpeed();
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos, CallbackInfo callback) {
        if (!this.level.isClientSide && onGroundIn && this.fallDistance > 0.0F) {
            this.blockrunner$removeBlockSpeed();
            this.blockrunner$tryAddBlockSpeed();
        }
    }

    @Unique
    private void blockrunner$removeBlockSpeed() {
        AttributeInstance attribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute != null) {
            if (attribute.getModifier(BlockSpeedManager.SPEED_MODIFIER_BLOCK_SPEED_UUID) != null) {
                attribute.removeModifier(BlockSpeedManager.SPEED_MODIFIER_BLOCK_SPEED_UUID);
            }
        }
    }

    @Unique
    protected void blockrunner$tryAddBlockSpeed() {
        if (this.getBlockStateOn().isAir() || (LivingEntity.class.cast(this) instanceof Player player && player.getAbilities().flying)) {
            return;
        }
        // check the block the entity is directly on to be able to support very thin blocks such as carpet
        double speedFactor = BlockSpeedManager.INSTANCE.getSpeedFactor(this.getBlockStateOn().getBlock());
        AttributeInstance attribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute == null || speedFactor == 1.0) return;
        double baseValue = attribute.getBaseValue();
        speedFactor = speedFactor * baseValue - baseValue;
        attribute.addTransientModifier(new AttributeModifier(BlockSpeedManager.SPEED_MODIFIER_BLOCK_SPEED_UUID, "Block speed boost", speedFactor, AttributeModifier.Operation.ADDITION));
    }

    @Shadow
    public abstract AttributeInstance getAttribute(Attribute attribute);

    @Shadow
    protected abstract boolean shouldRemoveSoulSpeed(BlockState state);
}
