package local.ae2lightuicompat.mixin;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.widgets.VerticalButtonBar;
import local.ae2lightuicompat.ClientTheme;
import local.ae2lightuicompat.ToolbarButton;
import local.ae2lightuicompat.ToolbarRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VerticalButtonBar.class, remap = false)
public abstract class VerticalButtonBarMixin implements ICompositeWidget {
    @Inject(method = "add", at = @At("HEAD"), require = 1)
    private void ae2lightuicompat$markToolbar(Button button, CallbackInfo ci) {
        if (button instanceof ToolbarButton toolbarButton) {
            toolbarButton.ae2lightuicompat$markToolbarButton();
        }
    }

    // The later 4s are folded 2 * MARGIN expressions; only change button spacing.
    @ModifyConstant(method = "updateBeforeRender", constant = @Constant(intValue = 4, ordinal = 0),
            require = 1, allow = 1)
    private int ae2lightuicompat$spacing(int original) {
        return ClientTheme.isToolbarEnabled() ? 6 : original;
    }

    // AE2 15.x inherits an empty interface default; adding the implementation
    // here lets the existing WidgetContainer background pass draw the bar.
    @Override
    public void drawBackgroundLayer(GuiGraphics graphics, Rect2i screen, Point mouse) {
        if (ClientTheme.isToolbarEnabled()) {
            ToolbarRenderer.background(graphics, screen, getBounds());
        }
    }
}
