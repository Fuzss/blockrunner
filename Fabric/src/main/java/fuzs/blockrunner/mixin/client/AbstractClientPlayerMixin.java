package fuzs.blockrunner.mixin.client;

import com.mojang.authlib.GameProfile;
import fuzs.blockrunner.api.client.event.ComputeFovModifierCallback;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    public AbstractClientPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, f, gameProfile, profilePublicKey);
    }

    @Inject(method = "getFieldOfViewModifier", at = @At("TAIL"), cancellable = true)
    public void getFieldOfViewModifier$injectTail(CallbackInfoReturnable<Float> callback) {
        // inject right at the end, after fov effects video setting has been applier
        callback.setReturnValue(ComputeFovModifierCallback.EVENT.invoker().onComputeFovModifier(this, callback.getReturnValue()));
    }
}
