package local.ae2lightuicompat.mixin;

import local.ae2lightuicompat.NavigationTab;
import local.ae2lightuicompat.TabRenderer;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Marks helper-created back buttons even when an addon uses another widget ID. */
@Mixin(targets = "appeng.client.gui.implementations.AESubScreen", remap = false)
public abstract class AESubScreenMixin {
    @ModifyArg(method = "addBackButton(Lappeng/menu/ISubMenu;Ljava/lang/String;Lappeng/client/gui/WidgetContainer;Lnet/minecraft/network/chat/Component;)V",
        at = @At(value = "INVOKE", target = "Lappeng/client/gui/WidgetContainer;add(Ljava/lang/String;Lnet/minecraft/client/gui/components/AbstractWidget;)V"),
        index = 1, require = 1, allow = 1)
    private static AbstractWidget ae2lightuicompat$markBack(AbstractWidget widget) {
        if (widget instanceof NavigationTab tab) tab.ae2lightuicompat$setNavigationRole(TabRenderer.BACK);
        return widget;
    }
}
