package local.ae2lightuicompat;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Implements the 1.21.1 toolbar appearance with the 1.20.1 rendering API. */
public final class ToolbarRenderer {
    private ToolbarRenderer() {}

    public static void background(GuiGraphics graphics, Rect2i screen, Rect2i bar) {
        if (bar.getWidth() <= 0 || bar.getHeight() <= 0) return;
        int x = screen.getX() + bar.getX() - 2;
        int y = screen.getY() + bar.getY() - 1;
        int width = bar.getWidth() + 1;
        int height = bar.getHeight() + 4;
        // Equivalent to the sprite's nine_slice metadata: top 2, bottom 4.
        // Tile the 20px middle rather than stretching the end caps.
        graphics.flush();
        graphics.pose().pushPose();
        try {
            graphics.pose().translate(0, 0, 1);
            strip(graphics, x, y, width, 0, 2);
            for (int offset = 2; offset < height - 4; offset += 20) {
                int length = Math.min(20, height - 4 - offset);
                strip(graphics, x, y + offset, width, 2, length);
            }
            strip(graphics, x, y + height - 4, width, 22, 4);
        } finally {
            graphics.pose().popPose();
        }
    }

    private static void strip(GuiGraphics graphics, int x, int y, int width, int sourceY, int height) {
        Blitter.texture(ClientTheme.TOOLBAR_BACKGROUND, 21, 26)
                .src(0, sourceY, 21, height).dest(x, y, width, height).blit(graphics);
    }

    public static void frame(GuiGraphics graphics, AbstractWidget button) {
        int x = button.getX();
        int y = button.getY() + (button.isHovered() ? 1 : 0);
        int sourceX = button.isHovered() ? 212 : button.isFocused() ? 194 : 176;
        graphics.flush();
        RenderSystem.enableDepthTest();
        graphics.pose().pushPose();
        try {
            graphics.pose().translate(0, 0, 2);
            Blitter.texture(ClientTheme.TOOLBAR_BUTTONS)
                    .src(sourceX, 128, 18, 20).dest(x - 1, y, 18, 20).blit(graphics);
        } finally {
            graphics.pose().popPose();
        }
    }

    public static void icon(GuiGraphics graphics, Button button, Icon icon, Item item) {
        if (item != null) {
            graphics.renderItem(new ItemStack(item), button.getX(), button.getY());
        } else if (icon != null && icon != Icon.TOOLBAR_BUTTON_BACKGROUND) {
            icon.getBlitter().opacity(button.active ? 1.0f : 0.5f)
                    .dest(button.getX(), button.getY()).blit(graphics);
        }
    }
}
