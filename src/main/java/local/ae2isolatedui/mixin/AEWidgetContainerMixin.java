package local.ae2isolatedui.mixin;

import java.util.function.Consumer;
import local.ae2isolatedui.ClientTheme;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "appeng.client.gui.WidgetContainer", remap = false)
public abstract class AEWidgetContainerMixin {
    @ModifyVariable(
        method = "populateScreen(Ljava/util/function/Consumer;Lnet/minecraft/client/renderer/Rect2i;Lappeng/client/gui/AEBaseScreen;)V",
        at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 1, remap = false)
    private Consumer<AbstractWidget> ae2isolatedui$markWidgets(Consumer<AbstractWidget> original) {
        return ClientTheme.markOwned(original);
    }
}
