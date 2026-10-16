package local.ae2isolatedui;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

/** Uses Forge 1.20.1 runtime (SRG) names; build_mod.py needs no remapping pass. */
public final class ClientTheme {
    public static final ResourceLocation BUTTONS =
            new ResourceLocation("ae2isolatedui", "textures/gui/widgets.png");

    private ClientTheme() {}

    public static Consumer<AbstractWidget> markOwned(Consumer<AbstractWidget> original) {
        return widget -> {
            if (widget instanceof ScopedButton button) {
                button.ae2isolatedui$markOwned();
            }
            original.accept(widget);
        };
    }

    public static boolean isPackEnabled() {
        // Check current resource state, so disabling the pack or F3+T immediately
        // restores vanilla rendering; no persistent global texture/state changes.
        return Minecraft.m_91087_().m_91098_().m_213713_(BUTTONS).isPresent();
    }

    public static int textColor(int original, boolean active) {
        // Keep vanilla alpha/fade semantics, with dark text on the light button.
        return (original & 0xFF000000) | (active ? 0x413F54 : 0xD2D3DF);
    }
}
