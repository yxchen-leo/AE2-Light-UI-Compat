package local.ae2lightuicompat;

import appeng.client.gui.widgets.IconButton;
import local.ae2lightuicompat.mixin.WidgetRenderAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

/** Keeps addon rendering inside the same pose, including draws after super.renderWidget. */
public final class ToolbarRenderScope {
    private static final ThreadLocal<AbstractWidget> CURRENT = new ThreadLocal<>();
    private ToolbarRenderScope() {}

    public static boolean contains(Object widget) { return CURRENT.get() == widget; }
    public static boolean active() { return CURRENT.get() != null; }

    public static void render(AbstractWidget widget, GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        var original = (WidgetRenderAccess) widget;
        if (!(widget instanceof ToolbarButton marker) || !marker.ae2lightuicompat$isToolbarButton()
                || widget.getWidth() != 16 || widget.getHeight() != 16
                || widget instanceof IconButton icon && icon.isHalfSize() || !ClientTheme.isToolbarEnabled()) {
            original.ae2lightuicompat$renderOriginal(graphics, mouseX, mouseY, partial);
            return;
        }
        var previous = CURRENT.get();
        ToolbarRenderer.frame(graphics, widget);
        graphics.pose().pushPose();
        try {
            graphics.pose().translate(0, 1 + (widget.isHovered() ? 1 : 0), 3);
            CURRENT.set(widget);
            original.ae2lightuicompat$renderOriginal(graphics, mouseX, mouseY, partial);
            graphics.flush();
        } finally {
            if (previous == null) CURRENT.remove(); else CURRENT.set(previous);
            graphics.pose().popPose();
        }
    }
}
