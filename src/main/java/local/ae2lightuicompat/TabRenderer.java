package local.ae2lightuicompat;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.TabButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/** Modern tab artwork on the legacy API; never replaces the tab's action. */
public final class TabRenderer {
    public static final int NONE = 0, BACK = 1, PRIORITY = 2, CRAFTING_STATUS = 3;
    private TabRenderer() {}

    public static int roleFor(String id) {
        return switch (id) {
            case "back", "button_back" -> BACK;
            case "openPriority" -> PRIORITY;
            case "craftingStatus" -> CRAFTING_STATUS;
            default -> NONE;
        };
    }

    public static void draw(GuiGraphics graphics, TabButton tab, Icon icon, ItemStack item, int role) {
        // The old terminal forces CORNER in code. The modern terminal uses BOX.
        var style = role == CRAFTING_STATUS ? TabButton.Style.BOX : tab.getStyle();
        int sx, sy, width, height;
        boolean focus = tab.isFocused();
        switch (style) {
            case CORNER -> { sx = 128; sy = focus ? 224 : 192; width = 25; height = 22; }
            case HORIZONTAL -> { sx = focus ? 150 : 128; sy = !focus && tab.isSelected() ? 150 : 128; width = height = 22; }
            default -> { sx = 160; sy = focus ? 224 : 192; width = height = focus ? 22 : 20; }
        }
        sprite(graphics, sx, sy, width, height, tab.getX(), tab.getY());
        int inset = switch (style) { case CORNER -> 1; case BOX -> 2; case HORIZONTAL -> 3; };
        int x = tab.getX() + inset, y = tab.getY() + inset - 1;
        if (role == BACK || role == PRIORITY) {
            sprite(graphics, role == BACK ? 96 : 144, role == BACK ? 16 : 64, 16, 16, x, y);
            return;
        }
        // Other tab icons remain in the old atlas: the two versions use different coordinates.
        if (icon != null) icon.getBlitter().dest(x, y).blit(graphics);
        if (item != null) {
            graphics.pose().pushPose();
            try {
                graphics.pose().translate(0, 0, 100);
                graphics.renderItem(item, x, y);
                graphics.renderItemDecorations(Minecraft.getInstance().font, item, x, y);
            } finally {
                graphics.pose().popPose();
            }
        }
    }

    private static void sprite(GuiGraphics graphics, int sx, int sy, int w, int h, int x, int y) {
        Blitter.texture(ClientTheme.TABS).src(sx, sy, w, h).dest(x, y).blit(graphics);
    }
}
