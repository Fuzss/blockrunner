package fuzs.blockrunner.mixin.client;

import com.mojang.authlib.GameProfile;
import fuzs.blockrunner.client.helper.FieldOfViewHelper;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractClientPlayer.class)
abstract class AbstractClientPlayerMixin extends Player {

    public AbstractClientPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @ModifyVariable(method = "getFieldOfViewModifier", at = @At(value = "STORE", ordinal = 2), ordinal = 0)
    public float getFieldOfViewModifier(float fovModifier) {
        // use a mixin here instead of using Forge's ComputeFovModifierEvent since this happens very early, and we'd have to reapply a lot of calculations otherwise
        if (FieldOfViewHelper.shouldRemoveBlockSpeedModifier(this)) {
            return FieldOfViewHelper.getFieldOfViewModifierWithoutBlockSpeed(this);
        }
        return fovModifier;
    }
}
