package local.ae2lightuicompat.mixin;

import com.google.gson.JsonElement;
import java.util.Optional;
import local.ae2lightuicompat.OptionalFactoryLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Common-side: runs before Forge deserializes item references, on every data reload. */
@Mixin(LootDataType.class)
public abstract class OptionalFactoryLootMixin {
    @Shadow @Final private String directory;

    // Forge-added overload: its name is not present in vanilla's obfuscation mappings.
    @Inject(method = "deserialize(Lnet/minecraft/resources/ResourceLocation;Lcom/google/gson/JsonElement;Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Optional;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void ae2lightuicompat$skipAbsentOptionalFactory(ResourceLocation id, JsonElement json,
            ResourceManager manager, CallbackInfoReturnable<Optional<?>> callback) {
        if (OptionalFactoryLoot.shouldSkip(directory, id, json,
                ForgeRegistries.ITEMS::containsKey, ForgeRegistries.BLOCKS::containsKey)) {
            callback.setReturnValue(Optional.empty());
        }
    }
}
