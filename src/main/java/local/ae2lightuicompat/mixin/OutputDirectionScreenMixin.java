package local.ae2lightuicompat.mixin;

import appeng.api.orientation.RelativeSide;
import local.ae2lightuicompat.ClientTheme;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.pedroksl.advanced_ae.client.gui.OutputDirectionScreen", remap = false)
public abstract class OutputDirectionScreenMixin {
    @Inject(method = "getButtonPosition", at = @At("RETURN"), cancellable = true)
    private void ae2lightuicompat$position(RelativeSide side, CallbackInfoReturnable<int[]> cir) {
        if (!ClientTheme.has(ClientTheme.OUTPUT_DIRECTIONS)) return;
        int[] old = cir.getReturnValue();
        cir.setReturnValue(new int[]{old[0], switch (side) {
            case TOP -> 6;
            case FRONT, LEFT, RIGHT -> 28;
            case BACK, BOTTOM -> 50;
        }});
    }
}
