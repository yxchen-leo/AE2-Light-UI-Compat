package local.ae2lightuicompat;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;

/** The modern AE2 text palette, baseline and shadow-free rendering. */
public final class ButtonTextRenderer {
    private ButtonTextRenderer() {}

    public static int color(boolean active, boolean hovered) {
        return !active ? 0x413F54 : hovered ? 0x517497 : 0xF2F2F2;
    }

    public static void draw(GuiGraphics graphics, Font font, AbstractButton button, int oldColor) {
        int color = (oldColor & 0xFF000000) | color(button.active, button.isHovered());
        int left = button.getX() + 2;
        int right = button.getX() + button.getWidth() - 2;
        int width = font.width(button.getMessage());
        int baseline = button.getY() + (button.getHeight() - 9) / 2 + 1;
        if (width <= right - left) {
            int offset = !button.active ? -1 : button.isHovered() ? 0 : 1;
            graphics.drawString(font, button.getMessage(), button.getX() + (button.getWidth() - width) / 2,
                    baseline - offset, color, false);
        } else {
            int overflow = width - (right - left);
            double period = Math.max(overflow * 0.5, 3.0);
            double phase = Util.getMillis() / 1000.0 * Math.PI * 2 / period;
            int scroll = (int) (overflow * (Math.sin(Math.cos(phase) * Math.PI / 2) + 1) / 2);
            graphics.enableScissor(left, button.getY(), right, button.getY() + button.getHeight());
            try {
                graphics.drawString(font, button.getMessage(), left - scroll, baseline, color, false);
            } finally {
                graphics.disableScissor();
            }
        }
    }
}
