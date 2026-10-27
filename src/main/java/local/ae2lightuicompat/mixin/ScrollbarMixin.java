package local.ae2lightuicompat.mixin;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.Scrollbar;
import local.ae2lightuicompat.ScrollbarTheme;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Only substitute handle artwork; retain AE2's position and input calculations. */
@Mixin(value = Scrollbar.class, remap = false)
public abstract class ScrollbarMixin {
    @Redirect(method = "drawForegroundLayer", at = @At(value = "INVOKE",
            target = "Lappeng/client/gui/widgets/Scrollbar$Style;enabledBlitter()Lappeng/client/gui/style/Blitter;"),
            require = 1, allow = 1)
    private Blitter ae2lightuicompat$enabled(Scrollbar.Style style) {
        return ScrollbarTheme.handle(style, true);
    }

    @Redirect(method = "drawForegroundLayer", at = @At(value = "INVOKE",
            target = "Lappeng/client/gui/widgets/Scrollbar$Style;disabledBlitter()Lappeng/client/gui/style/Blitter;"),
            require = 1, allow = 1)
    private Blitter ae2lightuicompat$disabled(Scrollbar.Style style) {
        return ScrollbarTheme.handle(style, false);
    }
}
