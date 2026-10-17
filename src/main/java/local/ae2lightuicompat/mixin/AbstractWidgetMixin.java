package local.ae2lightuicompat.mixin;

import local.ae2lightuicompat.ToolbarRenderScope;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/AbstractWidget;renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"),
            require = 1, allow = 1)
    private void ae2lightuicompat$renderScoped(AbstractWidget widget, GuiGraphics graphics,
                                             int mouseX, int mouseY, float partial) {
        ToolbarRenderScope.render(widget, graphics, mouseX, mouseY, partial);
    }
}
