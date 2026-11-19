package local.ae2lightuicompat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/** AE-owned button styling; ForgeGradle remaps Minecraft calls for production. */
public final class ClientTheme {
    public static final ResourceLocation LIGHTNING_CRAFT_REPORT =
            resource("textures/gui/lightning_crafting_report.png");
    public static final ResourceLocation CHECKBOXES = resource("textures/gui/checkbox.png");
    public static final ResourceLocation LIGHTNING_PROVIDER =
            resource("textures/gui/lightning_pattern_provider.png");
    public static final ResourceLocation OUTPUT_DIRECTIONS =
            resource("textures/gui/output_direction_states.png");
    public static final ResourceLocation BUTTONS = resource("textures/gui/widgets.png");
    public static final ResourceLocation TOOLBAR_BACKGROUND =
            resource("textures/gui/vertical_buttons_bg.png");
    public static final ResourceLocation TOOLBAR_BUTTONS =
            resource("textures/gui/toolbar_buttons.png");
    public static final ResourceLocation TABS = resource("textures/gui/tab_buttons.png");
    public static final ResourceLocation ADVANCED_LAYOUT =
            resource("compat/advanced_ae_layout.json");
    public static final ResourceLocation ADVANCED_ICONS =
            resource("textures/gui/advanced_ae_states.png");
    public static final ResourceLocation ADVANCED_ENCODER =
            new ResourceLocation("ae2", "textures/guis/adv_pattern_encoder.png");
    public static final ResourceLocation CHEMICAL_PALETTE =
            resource("compat/portable_chemical_cells.json");

    private ClientTheme() {}

    private static ResourceLocation resource(String path) {
        return new ResourceLocation(AE2LightUICompat.MOD_ID, path);
    }

    public static boolean has(ResourceLocation marker) {
        return Minecraft.getInstance().getResourceManager().getResource(marker).isPresent();
    }

    public static Consumer<AbstractWidget> markOwned(Consumer<AbstractWidget> original) {
        return widget -> {
            if (widget instanceof ScopedButton button) {
                button.ae2lightuicompat$markOwned();
            }
            original.accept(widget);
        };
    }

    public static boolean isPackEnabled() {
        // Check current resource state, so disabling the pack or F3+T immediately
        // restores vanilla rendering; no persistent global texture/state changes.
        return has(BUTTONS);
    }

    public static int textColor(int original, boolean active) {
        // Keep vanilla alpha/fade semantics, with dark text on the light button.
        return (original & 0xFF000000) | (active ? 0x413F54 : 0xD2D3DF);
    }

    public static boolean isToolbarEnabled() {
        var resources = Minecraft.getInstance().getResourceManager();
        return resources.getResource(TOOLBAR_BACKGROUND).isPresent()
                && resources.getResource(TOOLBAR_BUTTONS).isPresent();
    }

    public static boolean isTabEnabled() {
        return has(TABS);
    }
}
