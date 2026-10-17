package local.ae2lightuicompat.mixin;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import local.ae2lightuicompat.ToolbarRenderScope;
import local.ae2lightuicompat.ToolbarRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IconButton.class)
public abstract class IconButtonMixin {
    @Shadow(remap = false) protected abstract Icon getIcon();
    @Shadow(remap = false) protected abstract Item getItemOverlay();

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true, require = 1)
    private void ae2lightuicompat$renderToolbar(GuiGraphics graphics, int mouseX, int mouseY,
                                              float partial, CallbackInfo ci) {
        IconButton button = (IconButton) (Object) this;
        if (!ToolbarRenderScope.contains(this)) return;
        if (button.visible) {
            ToolbarRenderer.icon(graphics, button, getIcon(), getItemOverlay());
        }
        ci.cancel();
    }
}
