package local.ae2lightuicompat.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractWidget.class)
public interface WidgetRenderAccess {
    @Invoker("renderWidget")
    void ae2lightuicompat$renderOriginal(GuiGraphics graphics, int mouseX, int mouseY, float partial);
}
