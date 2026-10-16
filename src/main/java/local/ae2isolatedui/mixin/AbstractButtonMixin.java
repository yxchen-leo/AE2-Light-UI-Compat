package local.ae2isolatedui.mixin;

import local.ae2isolatedui.ClientTheme;
import local.ae2isolatedui.ScopedButton;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = AbstractButton.class, remap = false)
public abstract class AbstractButtonMixin implements ScopedButton {
    @Unique private boolean ae2isolatedui$owned;

    @Override
    public void ae2isolatedui$markOwned() {
        ae2isolatedui$owned = true;
    }

    @ModifyArg(method = "m_87963_(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;m_280027_(Lnet/minecraft/resources/ResourceLocation;IIIIIIIIII)V"),
        index = 0, require = 1, remap = false)
    private ResourceLocation ae2isolatedui$texture(ResourceLocation original) {
        return ae2isolatedui$owned && ClientTheme.isPackEnabled() ? ClientTheme.BUTTONS : original;
    }

    @ModifyArg(method = "m_87963_(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/AbstractButton;m_280139_(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"),
        index = 2, require = 1, remap = false)
    private int ae2isolatedui$textColor(int original) {
        if (!ae2isolatedui$owned || !ClientTheme.isPackEnabled()) {
            return original;
        }
        return ClientTheme.textColor(original, ((AbstractButton) (Object) this).f_93623_);
    }
}
