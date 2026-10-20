package local.ae2lightuicompat.mixin;

import java.util.HashMap;
import appeng.api.stacks.AEKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;
import appeng.menu.AEBaseMenu;
import appeng.menu.slot.FakeSlot;
import local.ae2lightuicompat.ClientTheme;
import local.ae2lightuicompat.ModernDirectionButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Visual layout only. Original buttons retain their AEKey, direction index and packet callbacks. */
@Pseudo
@Mixin(targets = "net.pedroksl.advanced_ae.client.gui.AdvPatternEncoderScreen", remap = false)
public abstract class AdvancedEncoderScreenMixin extends AEBaseScreen<AEBaseMenu> {
    @Shadow private HashMap<AEKey, Direction> inputList;
    @Shadow @Final private HashMap<AEKey, Button[]> directionButtons;
    @Shadow @Final private Scrollbar scrollbar;

    protected AdvancedEncoderScreenMixin(AEBaseMenu menu, Inventory inventory, Component title, ScreenStyle style) {
        super(menu, inventory, title, style);
    }

    @Inject(method = "drawFG", at = @At("HEAD"), cancellable = true, require = 1)
    private void ae2lightuicompat$encoderForeground(GuiGraphics graphics, int offsetX, int offsetY,
                                                    int mouseX, int mouseY, CallbackInfo ci) {
        if (!ClientTheme.has(ClientTheme.ADVANCED_LAYOUT)) {
            directionButtons.values().forEach(buttons -> {
                for (var button : buttons) { button.setWidth(13); button.setHeight(15); }
            });
            scrollbar.setHeight(72);
            scrollbar.setRange(0, Math.max(0, inputList.size() - 4), 2);
            return;
        }
        scrollbar.setHeight(56);
        scrollbar.setRange(0, Math.max(0, inputList.size() - 3), 2);
        menu.slots.removeIf(slot -> slot instanceof FakeSlot);
        directionButtons.values().forEach(buttons -> {
            for (var button : buttons) { button.visible = false; button.setWidth(12); button.setHeight(14); }
        });
        int skip = scrollbar.getCurrentScroll(), row = 0, index = 0;
        for (var entry : inputList.entrySet()) {
            if (index++ < skip) continue;
            if (row >= 3) break;
            graphics.renderItem(entry.getKey().wrapForDisplayOrFilter(), 19, 23 + row * 20);
            var buttons = directionButtons.get(entry.getKey());
            if (buttons == null) continue;
            int selected = entry.getValue() == null ? 0 : switch (entry.getValue()) {
                case NORTH -> 1; case EAST -> 2; case SOUTH -> 3;
                case WEST -> 4; case UP -> 5; case DOWN -> 6;
            };
            for (int col = 0; col < buttons.length; col++) {
                var button = buttons[col];
                button.setPosition(leftPos + 39 + col * 13, topPos + 23 + row * 20);
                ((ModernDirectionButton) button).ae2lightuicompat$select(col == selected);
                button.visible = true;
            }
            row++;
        }
        ci.cancel();
    }

    @Inject(method = "drawBG", at = @At("HEAD"), cancellable = true, require = 1)
    private void ae2lightuicompat$encoderBackground(GuiGraphics graphics, int offsetX, int offsetY,
                                                    int mouseX, int mouseY, float partial, CallbackInfo ci) {
        if (!ClientTheme.has(ClientTheme.ADVANCED_LAYOUT)) return;
        super.drawBG(graphics, offsetX, offsetY, mouseX, mouseY, partial);
        var texture = new ResourceLocation("ae2", "textures/guis/adv_pattern_encoder.png");
        for (int row = 0; row < Math.min(3, inputList.size()); row++) {
            graphics.blit(texture, offsetX + 18, offsetY + 22 + row * 20, 146, 16, 18, 18);
        }
        ci.cancel();
    }

    @Inject(method = "resetScrollbar", at = @At("TAIL"), require = 1)
    private void ae2lightuicompat$encoderScroll(CallbackInfo ci) {
        if (!ClientTheme.has(ClientTheme.ADVANCED_LAYOUT)) return;
        scrollbar.setHeight(56);
        scrollbar.setRange(0, Math.max(0, inputList.size() - 3), 2);
    }
}
