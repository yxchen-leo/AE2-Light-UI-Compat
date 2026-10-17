package local.ae2lightuicompat.mixin;

import local.ae2lightuicompat.ClientTheme;
import local.ae2lightuicompat.ScopedButton;
import local.ae2lightuicompat.ToolbarButton;
import local.ae2lightuicompat.ButtonTextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractButton.class)
public abstract class AbstractButtonMixin implements ScopedButton, ToolbarButton {
    @Unique private boolean ae2lightuicompat$owned;
    @Unique private boolean ae2lightuicompat$toolbar;

    @Override
    public void ae2lightuicompat$markToolbarButton() { ae2lightuicompat$toolbar = true; }

    @Override
    public boolean ae2lightuicompat$isToolbarButton() { return ae2lightuicompat$toolbar; }

    @Override
    public void ae2lightuicompat$markOwned() {
        ae2lightuicompat$owned = true;
    }

    @ModifyArg(method = "renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitNineSliced(Lnet/minecraft/resources/ResourceLocation;IIIIIIIIII)V"),
        index = 0, require = 1)
    private ResourceLocation ae2lightuicompat$texture(ResourceLocation original) {
        return ae2lightuicompat$owned && ClientTheme.isPackEnabled() ? ClientTheme.BUTTONS : original;
    }

    @ModifyArg(method = "renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/AbstractButton;renderString(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"),
        index = 2, require = 1)
    private int ae2lightuicompat$textColor(int original) {
        if (!ae2lightuicompat$owned || !ClientTheme.isPackEnabled()) {
            return original;
        }
        return ClientTheme.textColor(original, ((AbstractButton) (Object) this).active);
    }

    @Inject(method = "renderString", at = @At("HEAD"), cancellable = true, require = 1)
    private void ae2lightuicompat$modernText(GuiGraphics graphics, Font font, int color, CallbackInfo ci) {
        if (ae2lightuicompat$owned && ClientTheme.isPackEnabled() && ClientTheme.isToolbarEnabled()) {
            ButtonTextRenderer.draw(graphics, font, (AbstractButton) (Object) this, color);
            ci.cancel();
        }
    }
}
