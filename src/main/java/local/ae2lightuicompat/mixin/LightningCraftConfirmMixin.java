package local.ae2lightuicompat.mixin;

import local.ae2lightuicompat.ClientTheme;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** AE2LT beta3 footer slices, paired with its original 238x260 texture. */
@Pseudo
@Mixin(targets = "com.moakiee.ae2lt.client.crafting.AE2LtCraftConfirmScreen", remap = false)
public abstract class LightningCraftConfirmMixin {
    @ModifyConstant(method = {"init()V", "m_7856_()V"},
            constant = @Constant(intValue = 68), require = 2, allow = 2)
    private int ae2lightuicompat$footerHeight(int original) {
        return ClientTheme.has(ClientTheme.LIGHTNING_CRAFT_REPORT) ? 64 : original;
    }

    @ModifyConstant(method = "drawBG", constant = @Constant(intValue = 60), require = 1, allow = 1)
    private int ae2lightuicompat$footerSliceHeight(int original) {
        return ClientTheme.has(ClientTheme.LIGHTNING_CRAFT_REPORT) ? 56 : original;
    }

    @ModifyConstant(method = "drawBG", constant = @Constant(intValue = 248), require = 1, allow = 1)
    private int ae2lightuicompat$bottomSliceY(int original) {
        return ClientTheme.has(ClientTheme.LIGHTNING_CRAFT_REPORT) ? 252 : original;
    }
}
