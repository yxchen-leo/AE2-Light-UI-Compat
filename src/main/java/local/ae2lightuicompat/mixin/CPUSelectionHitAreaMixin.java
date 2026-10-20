package local.ae2lightuicompat.mixin;

import appeng.client.Point;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.CPUSelectionList;
import appeng.menu.me.crafting.CraftingStatusMenu;
import local.ae2lightuicompat.ClientTheme;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The list background extends into the footer; invisible CPU rows must not intercept its buttons. */
@Mixin(value = CPUSelectionList.class, remap = false)
public abstract class CPUSelectionHitAreaMixin {
    @Shadow private Rect2i bounds;
    @Shadow @Final private Blitter buttonBg;
    @Shadow @Final private static int ROWS;

    @Inject(method = "hitTestCpu", at = @At("HEAD"), cancellable = true, require = 1)
    private void ae2lightuicompat$visibleRowsOnly(Point mouse,
            CallbackInfoReturnable<CraftingStatusMenu.CraftingCpuListEntry> cir) {
        if (ClientTheme.isTabEnabled()
                && mouse.getY() - bounds.getY() >= 19 + ROWS * (buttonBg.getSrcHeight() + 1)) {
            cir.setReturnValue(null);
        }
    }
}
