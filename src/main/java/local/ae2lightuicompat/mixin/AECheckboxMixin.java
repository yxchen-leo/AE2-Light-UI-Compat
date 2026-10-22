package local.ae2lightuicompat.mixin;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;
import local.ae2lightuicompat.ClientTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Modern AE2 toggle atlas and icon-only mouse interaction. */
@Mixin(AECheckbox.class)
public abstract class AECheckboxMixin extends AbstractButton {
    @Shadow(remap = false) @Final private ScreenStyle style;

    protected AECheckboxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Unique
    private boolean ae2lightuicompat$contains(double x, double y) {
        boolean radio = ((AECheckbox) (Object) this).isRadio();
        return x >= getX() && x < getX() + (radio ? 14 : 22)
                && y >= getY() && y < getY() + (radio ? 14 : 12);
    }

    @Override
    protected boolean clicked(double x, double y) {
        if (!ClientTheme.has(ClientTheme.CHECKBOXES)) return super.clicked(x, y);
        return active && visible && ae2lightuicompat$contains(x, y);
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        if (!ClientTheme.has(ClientTheme.CHECKBOXES)) return super.isMouseOver(x, y);
        return active && visible && ae2lightuicompat$contains(x, y);
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void ae2lightuicompat$render(GuiGraphics graphics, int mouseX, int mouseY,
                                        float partialTick, CallbackInfo ci) {
        if (!ClientTheme.has(ClientTheme.CHECKBOXES)) return;
        AECheckbox checkbox = (AECheckbox) (Object) this;
        boolean radio = checkbox.isRadio();
        boolean hover = isMouseOver(mouseX, mouseY);
        // Match modern AE2: mouse focus must not keep an unchecked toggle lit.
        boolean highlight = hover && (radio || !isFocused());
        if (!hover) setFocused(false);
        int u = radio ? (highlight ? 42 : 28) : (highlight ? 22 : 0);
        int v = radio ? (checkbox.isSelected() ? 14 : 0) : (checkbox.isSelected() ? 40 : 28);
        Blitter.texture(ClientTheme.CHECKBOXES, 64, 64)
                .src(u, v, radio ? 14 : 22, radio ? 14 : 12)
                .dest(getX(), getY()).opacity(isActive() ? 1 : 0.5f).blit(graphics);
        var font = Minecraft.getInstance().font;
        int offset = radio ? 16 : 26;
        var lines = font.split(getMessage(), Math.max(1, width - offset));
        int y = getY() + (lines.size() <= 1 ? 4 : 1);
        int color = style.getColor(isActive() ? PaletteColor.DEFAULT_TEXT_COLOR
                : PaletteColor.MUTED_TEXT_COLOR).toARGB();
        for (var line : lines) {
            graphics.drawString(font, line, getX() + offset, y, color, false);
            y += font.lineHeight;
        }
        ci.cancel();
    }
}
