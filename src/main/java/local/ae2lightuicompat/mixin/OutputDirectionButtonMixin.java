package local.ae2lightuicompat.mixin;

import local.ae2lightuicompat.ClientTheme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.pedroksl.ae2addonlib.client.widgets.OutputDirectionButton", remap = false)
public abstract class OutputDirectionButtonMixin extends AbstractWidget {
    protected OutputDirectionButtonMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }
    @Shadow private ItemStack item;
    @Shadow private boolean enabled;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ae2lightuicompat$size(CallbackInfo ci) {
        if (ClientTheme.has(ClientTheme.OUTPUT_DIRECTIONS)) height = 20;
    }

    @Inject(method = {"renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            "m_87963_(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"},
            at = @At("HEAD"), cancellable = true, require = 1, allow = 1)
    private void ae2lightuicompat$render(GuiGraphics graphics, int mouseX, int mouseY,
                                        float partial, CallbackInfo ci) {
        boolean modern = ClientTheme.has(ClientTheme.OUTPUT_DIRECTIONS);
        height = modern ? 20 : 18;
        if (!modern) return;
        AbstractWidget button = (AbstractWidget) (Object) this;
        graphics.blit(ClientTheme.OUTPUT_DIRECTIONS, button.getX(), button.getY(),
                enabled ? 194 : 176, 128, 18, 20, 256, 256);
        if (item != null) graphics.renderItem(item, button.getX() + 1, button.getY() + 1);
        ci.cancel();
    }
}
