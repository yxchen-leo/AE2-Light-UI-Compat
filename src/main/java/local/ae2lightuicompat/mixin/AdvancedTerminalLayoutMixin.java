package local.ae2lightuicompat.mixin;

import local.ae2lightuicompat.ClientTheme;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "net.pedroksl.advanced_ae.client.gui.QuantumCrafterTermScreen", remap = false)
public abstract class AdvancedTerminalLayoutMixin {
    @ModifyConstant(method = {"init()V", "m_7856_()V"}, constant = @Constant(intValue = 97), require = 1, allow = 1)
    private int ae2lightuicompat$footerSpace(int old) {
        return ClientTheme.has(ClientTheme.ADVANCED_LAYOUT) ? 99 : old;
    }

    @ModifyConstant(method = {"init()V", "m_7856_()V"}, constant = @Constant(intValue = 114), require = 1, allow = 1)
    private int ae2lightuicompat$terminalHeight(int old) {
        return ClientTheme.has(ClientTheme.ADVANCED_LAYOUT) ? 116 : old;
    }

    @ModifyVariable(method = "blit", at = @At("HEAD"), argsOnly = true, require = 1)
    private Rect2i ae2lightuicompat$footerImage(Rect2i old) {
        return ClientTheme.has(ClientTheme.ADVANCED_LAYOUT) && old.getY() == 125 && old.getHeight() == 97
                ? new Rect2i(old.getX(), old.getY(), old.getWidth(), 99) : old;
    }
}
