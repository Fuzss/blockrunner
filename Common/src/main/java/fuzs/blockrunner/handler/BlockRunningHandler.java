//package fuzs.blockrunner.handler;
//
//import fuzs.blockrunner.data.BlockSpeedManager;
//import fuzs.blockrunner.mixin.accessor.LivingEntityAccessor;
//import net.minecraft.core.BlockPos;
//import net.minecraft.util.Unit;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.ai.attributes.AttributeInstance;
//import net.minecraft.world.entity.ai.attributes.AttributeModifier;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.entity.player.Player;
//
//import java.util.Objects;
//import java.util.Optional;
//
//public class BlockRunningHandler {
//    private static BlockPos lastEntityPos;
//
//    public static Optional<Unit> onLivingTick$Start(LivingEntity entity) {
//        lastEntityPos = ((LivingEntityAccessor) entity).blockrunner$getLastPos();
//        return Optional.empty();
//    }
//
//    public static void onLivingTick$End(LivingEntity entity) {
//        BlockPos pos = ((LivingEntityAccessor) entity).blockrunner$getLastPos();
//        if (!Objects.equals(pos, lastEntityPos)) {
//            if (((LivingEntityAccessor) entity).blockrunner$callShouldRemoveSoulSpeed(entity.getBlockStateOn())) {
//                removeBlockSpeed(entity);
//            } else {
//
//            }
//        }
//    }
//
//    private static void removeBlockSpeed(LivingEntity entity) {
//        AttributeInstance attribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
//        if (attribute != null) {
//            if (attribute.getModifier(BlockSpeedManager.SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID) != null) {
//                attribute.removeModifier(BlockSpeedManager.SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID);
//            }
//        }
//    }
//
//    private static void tryAddBlockSpeed(LivingEntity entity) {
//        if (!entity.getBlockStateOn().isAir()) {
//            if ((!(entity instanceof Player player) || !player.getAbilities().flying) && entity.custom$onCustomSpeedBlock()) {
//                double speedFactor = BlockSpeedManager.INSTANCE.getSpeedFactor(entity.level.getBlockState(entity.custom$getBlockPosBelowThatAffectsMyMovement2()).getBlock());
//                AttributeInstance attribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
//                if (attribute == null || speedFactor == 1.0) return;
//                addAttributeModifier(speedFactor, attribute);
//            }
//        }
//    }
//
//    private static void addAttributeModifier(double speedFactor, AttributeInstance attribute) {
//        double baseValue = attribute.getBaseValue();
//        speedFactor = speedFactor * baseValue - baseValue;
//        attribute.addTransientModifier(new AttributeModifier(BlockSpeedManager.SPEED_MODIFIER_CUSTOM_BLOCK_SPEED_UUID, "Block speed boost", speedFactor, AttributeModifier.Operation.ADDITION));
//    }
//}
