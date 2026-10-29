package local.ae2lightuicompat.mixin;

import com.google.gson.JsonObject;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Display metadata is read once during discovery; the pack ID remains its filename. */
@Mixin(Pack.class)
public abstract class ResourcePackTitleMixin {
    @Shadow @Final @Mutable private Component title;

    @Unique
    private static final MetadataSectionSerializer<String> ae2lightuicompat$metadata =
            new MetadataSectionSerializer<>() {
                @Override
                public String getMetadataSectionName() { return "ae2lightuicompat"; }

                @Override
                public String fromJson(JsonObject json) {
                    if (!"ae2_addons_texture_backport".equals(GsonHelper.getAsString(json, "pack_id", ""))) {
                        return null;
                    }
                    return GsonHelper.getAsString(json, "display_name", "");
                }
            };

    @Inject(method = "readMetaAndCreate", at = @At("RETURN"))
    private static void ae2lightuicompat$displayName(String id, Component originalTitle,
            boolean required, Pack.ResourcesSupplier supplier, PackType type,
            Pack.Position position, PackSource source, CallbackInfoReturnable<Pack> cir) {
        Pack pack = cir.getReturnValue();
        if (pack == null || type != PackType.CLIENT_RESOURCES || !id.startsWith("file/")) return;
        try (var resources = supplier.open(id)) {
            String name = resources.getMetadataSection(ae2lightuicompat$metadata);
            if (name != null && !name.isBlank()) {
                ((ResourcePackTitleMixin) (Object) pack).title = Component.literal(name);
            }
        } catch (IOException | RuntimeException ignored) {
            // Optional display metadata must never prevent a pack from being listed.
        }
    }
}
