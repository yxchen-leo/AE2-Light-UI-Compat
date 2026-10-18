package local.ae2lightuicompat.mixin;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.TabButton;
import local.ae2lightuicompat.ClientTheme;
import local.ae2lightuicompat.NavigationTab;
import local.ae2lightuicompat.TabRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TabButton.class)
public abstract class TabButtonMixin implements NavigationTab {
    @Shadow(remap = false) private Icon icon;
    @Shadow(remap = false) private ItemStack item;
    @Unique private int ae2lightuicompat$role;

    @Override
    public void ae2lightuicompat$setNavigationRole(int role) { ae2lightuicompat$role = role; }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true, require = 1)
    private void ae2lightuicompat$renderTab(GuiGraphics graphics, int mouseX, int mouseY,
                                          float partial, CallbackInfo ci) {
        if (!ClientTheme.isTabEnabled()) return;
        TabButton tab = (TabButton) (Object) this;
        if (tab.visible) TabRenderer.draw(graphics, tab, icon, item, ae2lightuicompat$role);
        ci.cancel();
    }
}
