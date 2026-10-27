package local.ae2lightuicompat;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.Scrollbar;
import net.minecraft.resources.ResourceLocation;

/** Read modern AE2 sprites as standalone textures on Minecraft 1.20.1. */
public final class ScrollbarTheme {
    private static final ResourceLocation BIG = sprite("big_scroller");
    private static final ResourceLocation BIG_DISABLED = sprite("big_scroller_disabled");
    private static final ResourceLocation SMALL = sprite("small_scroller");
    private static final ResourceLocation SMALL_DISABLED = sprite("small_scroller_disabled");

    private ScrollbarTheme() {}

    private static ResourceLocation sprite(String name) {
        return new ResourceLocation("ae2", "textures/gui/sprites/" + name + ".png");
    }

    public static Blitter handle(Scrollbar.Style style, boolean enabled) {
        ResourceLocation texture = null;
        if (style == Scrollbar.DEFAULT) {
            texture = enabled ? BIG : BIG_DISABLED;
        } else if (style == Scrollbar.SMALL) {
            texture = enabled ? SMALL : SMALL_DISABLED;
        }
        // Leave addon-defined styles intact and restore the original renderer
        // immediately when the matching resource pack is disabled/reloaded.
        if (texture == null || !ClientTheme.isPackEnabled() || !ClientTheme.has(texture)) {
            return enabled ? style.enabledBlitter() : style.disabledBlitter();
        }
        // Both generations use 12x15 normal / 7x15 small handles. Using their
        // real reference size avoids the old Blitter's default 256x256 UV scale.
        int width = style.handleWidth();
        int height = style.handleHeight();
        return Blitter.texture(texture, width, height).src(0, 0, width, height);
    }
}
