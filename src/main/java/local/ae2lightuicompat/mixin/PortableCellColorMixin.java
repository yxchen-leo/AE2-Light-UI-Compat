package local.ae2lightuicompat.mixin;

import appeng.items.tools.powered.AbstractPortableCell;
import local.ae2lightuicompat.ClientTheme;
import local.ae2lightuicompat.PortableCellColors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractPortableCell.class, remap = false)
public abstract class PortableCellColorMixin {
    @Inject(method = "getColor(Lnet/minecraft/world/item/ItemStack;I)I",
            at = @At("RETURN"), cancellable = true, require = 1)
    private static void ae2lightuicompat$screenColor(ItemStack stack, int tint,
                                                    CallbackInfoReturnable<Integer> cir) {
        if (tint != 2 || !ClientTheme.has(ClientTheme.CHEMICAL_PALETTE)) return;
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        var display = stack.getTagElement("display");
        cir.setReturnValue(PortableCellColors.resolve(cir.getReturnValue(), id.getNamespace(), id.getPath(),
                tint, display != null && display.contains("color", 99), true));
    }
}
