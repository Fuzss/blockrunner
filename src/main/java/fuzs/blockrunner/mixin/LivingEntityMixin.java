package fuzs.blockrunner.mixin;

import fuzs.blockrunner.common.element.RoadStarElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

import java.util.UUID;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    private static final UUID ROAD_STAR_BOOST_ID = UUID.fromString("23237052-61AD-11EB-AE93-0242AC130002");

    public LivingEntityMixin(EntityType<?> entityTypeIn, Level worldIn) {

        super(entityTypeIn, worldIn);
    }

    @Inject(method = "updateFallState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;func_233641_cN_()V"))
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos, CallbackInfo callbackInfo) {
        this.removeRoadBlockBoost();
        this.applyRoadBlockBoost();
    }

    @Inject(method = "getSpeedFactor", at = @At("HEAD"), cancellable = true)
    protected void getSpeedFactor(CallbackInfoReturnable<Float> callbackInfo) {
        RoadStarElement element = RoadStarElements.getAs(RoadStarElements.ROAD_STAR);
        if (element.roadBlocksManager.isRoadBlock(this.getBlockUnderneath())) {
            callbackInfo.setReturnValue(1.0F);
        }
    }

    protected void removeRoadBlockBoost() {
        ModifiableAttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeinstance != null) {
            if (attributeinstance.getModifier(ROAD_STAR_BOOST_ID) != null) {
                attributeinstance.removeModifier(ROAD_STAR_BOOST_ID);
            }
        }
    }

    protected void applyRoadBlockBoost() {
        if (!this.getStateBelow().getBlock().isAir(this.getStateBelow(), this.world, this.getOnPosition())) {
            RoadStarElement element = RoadStarElements.getAs(RoadStarElements.ROAD_STAR);
            double roadBlockSpeed = element.roadBlocksManager.getSpeedFactor(this.getBlockUnderneath());
            if (roadBlockSpeed != 1.0) {
                ModifiableAttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attributeinstance != null) {
                    double baseValue = attributeinstance.getBaseValue();
                    roadBlockSpeed = roadBlockSpeed * baseValue - baseValue;
                    attributeinstance.applyNonPersistentModifier(new AttributeModifier(ROAD_STAR_BOOST_ID, "Road block boost", roadBlockSpeed, AttributeModifier.Operation.ADDITION));
                }

            }
        }
    }

    @Shadow
    public abstract ModifiableAttributeInstance getAttribute(Attribute attribute);

    @Inject(method = "frostWalk", at = @At("TAIL"))
    protected void frostWalk(BlockPos pos, CallbackInfo callbackInfo) {
        // check if block not air or player is elytra flying
        if (this.func_230295_b_(this.getStateBelow())) {
            this.removeRoadBlockBoost();
        }
        this.applyRoadBlockBoost();
    }

    @Shadow
    protected abstract boolean func_230295_b_(BlockState p_230295_1_);

    @Unique
    private Block getBlockUnderneath() {
        return this.level.getBlockState(this.getPositionUnderneath()).getBlock();
    }

}
