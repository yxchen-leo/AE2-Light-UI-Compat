package local.ae2lightuicompat.mixin;

import appeng.client.gui.AEBaseScreen;
import local.ae2lightuicompat.ClientTheme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Modern slot artwork using the legacy screen's bounds and render depth. */
@Mixin(AEBaseScreen.class)
public abstract class SlotAppearanceMixin {
    @Redirect(method = "renderCustomSlotHighlight", remap = false,
            at = @At(value = "INVOKE", remap = true,
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(Lnet/minecraft/client/renderer/RenderType;IIIIIII)V"),
            require = 1, allow = 1)
    private void ae2lightuicompat$highlight(GuiGraphics graphics, RenderType layer,
            int x, int y, int right, int bottom, int colorTop, int colorBottom, int z) {
        if (!ClientTheme.isPackEnabled()) {
            graphics.fillGradient(layer, x, y, right, bottom, colorTop, colorBottom, z);
            return;
        }
        // AE2 already computes the correct bounds for ResizableSlot here.
        // Carry the legacy depth over to both the new border and its fill.
        graphics.pose().pushPose();
        try {
            graphics.pose().translate(0, 0, z);
            graphics.hLine(x, right, y - 1, 0xFFDAFFFF);
            graphics.hLine(x - 1, right, bottom, 0xFFDAFFFF);
            graphics.vLine(x - 1, y - 2, bottom, 0xFFDAFFFF);
            graphics.vLine(right, y - 2, bottom, 0xFFDAFFFF);
            graphics.fillGradient(layer, x, y, right, bottom, 0x669CD3FF, 0x669CD3FF, 0);
        } finally {
            graphics.pose().popPose();
        }
    }

    @ModifyConstant(method = "drawOptionalSlotBackground", remap = false,
            constant = @Constant(floatValue = 0.4f), require = 1, allow = 1)
    private float ae2lightuicompat$disabledSlotOpacity(float original) {
        return ClientTheme.isPackEnabled() ? 0.2f : original;
    }
}
