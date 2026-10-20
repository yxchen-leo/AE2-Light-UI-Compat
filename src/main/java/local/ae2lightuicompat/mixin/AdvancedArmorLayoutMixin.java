package local.ae2lightuicompat.mixin;

import local.ae2lightuicompat.ClientTheme;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "net.pedroksl.advanced_ae.client.gui.QuantumArmorConfigScreen", remap = false)
public abstract class AdvancedArmorLayoutMixin {
    @ModifyConstant(method = {"drawFG", "drawBG", "refreshList(Lnet/minecraft/world/item/ItemStack;Z)V"},
            constant = @Constant(intValue = 31), require = 3, allow = 3)
    private int ae2lightuicompat$armorRows(int old) {
        return ClientTheme.has(ClientTheme.ADVANCED_LAYOUT) ? 32 : old;
    }
}
