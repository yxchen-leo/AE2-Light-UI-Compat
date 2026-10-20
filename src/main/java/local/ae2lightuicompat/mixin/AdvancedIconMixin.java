package local.ae2lightuicompat.mixin;

import local.ae2lightuicompat.ClientTheme;
import local.ae2lightuicompat.AE2LightUICompat;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Remap semantic icons, rather than replacing an atlas whose old coordinates differ. */
@Pseudo
@Mixin(targets = "net.pedroksl.advanced_ae.client.gui.widgets.AAEIcon", remap = false)
public abstract class AdvancedIconMixin {
    @Unique private boolean ae2lightuicompat$modernIcon() {
        String name = ((Enum<?>) (Object) this).name();
        return ClientTheme.has(ClientTheme.ADVANCED_LAYOUT) && !name.equals("CLEAR_SMALL")
                && !name.startsWith("TOOLBAR_BUTTON_");
    }

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true, require = 1)
    private void ae2lightuicompat$iconAtlas(CallbackInfoReturnable<ResourceLocation> cir) {
        if (ae2lightuicompat$modernIcon()) cir.setReturnValue(new ResourceLocation(
                AE2LightUICompat.MOD_ID, "textures/gui/advanced_ae_states.png"));
    }

    @Inject(method = "getRect", at = @At("RETURN"), cancellable = true, require = 1)
    private void ae2lightuicompat$iconCoordinates(CallbackInfoReturnable<Rect2i> cir) {
        if (!ae2lightuicompat$modernIcon()) return;
        Rect2i rect = switch (((Enum<?>) (Object) this).name()) {
            case "CRAFTER_TERMINAL_VISIBLE" -> new Rect2i(0, 16, 16, 16);
            case "CRAFTER_TERMINAL_ALL" -> new Rect2i(16, 16, 16, 16);
            case "CRAFTER_TERMINAL_NOT_FULL" -> new Rect2i(32, 16, 16, 16);
            case "SHOW_ON_CRAFTER_TERMINAL" -> new Rect2i(0, 32, 16, 16);
            case "HIDE_ON_CRAFTER_TERMINAL" -> new Rect2i(16, 32, 16, 16);
            case "REGULATE_ON" -> new Rect2i(32, 32, 16, 16);
            case "REGULATE_OFF" -> new Rect2i(48, 32, 16, 16);
            default -> cir.getReturnValue();
        };
        cir.setReturnValue(rect);
    }
}
