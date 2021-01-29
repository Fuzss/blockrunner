package com.fuzs.roadstar.mixin;

import com.fuzs.roadstar.common.builder.RoadBlocksBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    private static final UUID ROAD_STAR_BOOST_ID = UUID.fromString("23237052-61AD-11EB-AE93-0242AC130002");

    public LivingEntityMixin(EntityType<?> entityTypeIn, World worldIn) {

        super(entityTypeIn, worldIn);
    }

    @Inject(method = "updateFallState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;func_233641_cN_()V"))
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos, CallbackInfo callbackInfo) {

        this.removeRoadBlockBoost();
        this.applyRoadBlockBoost();
    }

    @Inject(method = "getSpeedFactor", at = @At("HEAD"), cancellable = true)
    protected void getSpeedFactor(CallbackInfoReturnable<Float> callbackInfo) {

        if (this.isAboveRoadBlock()) {

            callbackInfo.setReturnValue(1.0F);
        }
    }

    protected boolean isAboveRoadBlock() {

        return RoadBlocksBuilder.ROAD_BLOCKS.containsKey(this.world.getBlockState(this.getPositionUnderneath()).getBlock());
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

            double roadBlockSpeed = RoadBlocksBuilder.ROAD_BLOCKS.getOrDefault(this.world.getBlockState(this.getPositionUnderneath()).getBlock(), 1.0);
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

}
