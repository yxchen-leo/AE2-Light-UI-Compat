package local.ae2lightuicompat.mixin;

import local.ae2lightuicompat.ClientTheme;
import local.ae2lightuicompat.ModernDirectionButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.pedroksl.advanced_ae.gui.patternencoder.DirectionInputButton", remap = false)
public abstract class AdvancedDirectionButtonMixin implements ModernDirectionButton {
    @Shadow ResourceLocation texture;
    @Shadow private boolean highlighted;

    public void ae2lightuicompat$select(boolean selected) { highlighted = selected; }

    @Inject(method = {"renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
                      "m_87963_(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"},
            at = @At("HEAD"), cancellable = true, require = 1, allow = 1)
    private void ae2lightuicompat$direction(GuiGraphics graphics, int mouseX, int mouseY,
                                           float partial, CallbackInfo ci) {
        if (!ClientTheme.has(ClientTheme.ADVANCED_LAYOUT)) return;
        var button = (AbstractWidget) (Object) this;
        var image = highlighted ? new ResourceLocation(texture.getNamespace(),
                texture.getPath().replace(".png", "_selected.png")) : texture;
        graphics.blit(image, button.getX(), button.getY(), 0, 0, 12, 14, 16, 16);
        ci.cancel();
    }
}
