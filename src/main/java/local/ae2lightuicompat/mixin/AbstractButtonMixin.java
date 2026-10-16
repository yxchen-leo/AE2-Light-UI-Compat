package local.ae2lightuicompat.mixin;

import local.ae2lightuicompat.ClientTheme;
import local.ae2lightuicompat.ScopedButton;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = AbstractButton.class)
public abstract class AbstractButtonMixin implements ScopedButton {
    @Unique private boolean ae2lightuicompat$owned;

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
}
