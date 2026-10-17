package local.ae2lightuicompat.mixin;

import appeng.client.gui.style.Blitter;
import local.ae2lightuicompat.ToolbarRenderScope;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Blitter.class, remap = false)
public abstract class BlitterMixin {
    @Shadow @Final private ResourceLocation texture;

    @Inject(method = "blit", at = @At("HEAD"), cancellable = true, require = 1)
    private void ae2lightuicompat$skipLegacyFrame(GuiGraphics graphics, CallbackInfo ci) {
        if (!ToolbarRenderScope.active()) return;
        Blitter blitter = (Blitter) (Object) this;
        if (texture.getNamespace().equals("ae2") && texture.getPath().equals("textures/guis/states.png")
                && blitter.getSrcX() == 240 && blitter.getSrcY() == 240
                && blitter.getSrcWidth() == 16 && blitter.getSrcHeight() == 16) {
            ci.cancel();
        }
    }
}
