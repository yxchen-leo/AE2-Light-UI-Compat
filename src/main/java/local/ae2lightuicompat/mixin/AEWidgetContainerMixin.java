package local.ae2lightuicompat.mixin;

import java.util.function.Consumer;
import local.ae2lightuicompat.ClientTheme;
import local.ae2lightuicompat.NavigationTab;
import local.ae2lightuicompat.TabRenderer;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "appeng.client.gui.WidgetContainer", remap = false)
public abstract class AEWidgetContainerMixin {
    @Inject(method = "add(Ljava/lang/String;Lnet/minecraft/client/gui/components/AbstractWidget;)V",
            at = @At("HEAD"), require = 1)
    private void ae2lightuicompat$markTab(String id, AbstractWidget widget, CallbackInfo ci) {
        int role = TabRenderer.roleFor(id);
        if (role != TabRenderer.NONE && widget instanceof NavigationTab tab) {
            tab.ae2lightuicompat$setNavigationRole(role);
        }
    }

    @ModifyVariable(
        method = "populateScreen(Ljava/util/function/Consumer;Lnet/minecraft/client/renderer/Rect2i;Lappeng/client/gui/AEBaseScreen;)V",
        at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 1, remap = false)
    private Consumer<AbstractWidget> ae2lightuicompat$markWidgets(Consumer<AbstractWidget> original) {
        return ClientTheme.markOwned(original);
    }
}
