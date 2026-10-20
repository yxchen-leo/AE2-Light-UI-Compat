package local.ae2lightuicompat;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

/** AE-owned button styling; ForgeGradle remaps Minecraft calls for production. */
public final class ClientTheme {
    public static final ResourceLocation LIGHTNING_PROVIDER =
            new ResourceLocation(AE2LightUICompat.MOD_ID, "textures/gui/lightning_pattern_provider.png");
    public static final ResourceLocation OUTPUT_DIRECTIONS =
            new ResourceLocation(AE2LightUICompat.MOD_ID, "textures/gui/output_direction_states.png");
    public static final ResourceLocation BUTTONS =
            new ResourceLocation(AE2LightUICompat.MOD_ID, "textures/gui/widgets.png");
    public static final ResourceLocation TOOLBAR_BACKGROUND =
            new ResourceLocation(AE2LightUICompat.MOD_ID, "textures/gui/vertical_buttons_bg.png");
    public static final ResourceLocation TOOLBAR_BUTTONS =
            new ResourceLocation(AE2LightUICompat.MOD_ID, "textures/gui/toolbar_buttons.png");
    public static final ResourceLocation TABS =
            new ResourceLocation(AE2LightUICompat.MOD_ID, "textures/gui/tab_buttons.png");
    public static final ResourceLocation ADVANCED_LAYOUT =
            new ResourceLocation(AE2LightUICompat.MOD_ID, "compat/advanced_ae_layout.json");
    public static final ResourceLocation CHEMICAL_PALETTE =
            new ResourceLocation(AE2LightUICompat.MOD_ID, "compat/portable_chemical_cells.json");

    public static boolean has(ResourceLocation marker) {
        return Minecraft.getInstance().getResourceManager().getResource(marker).isPresent();
    }

    private ClientTheme() {}

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
        return Minecraft.getInstance().getResourceManager().getResource(BUTTONS).isPresent();
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
        return Minecraft.getInstance().getResourceManager().getResource(TABS).isPresent();
    }
}
