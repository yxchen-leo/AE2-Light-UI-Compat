package local.ae2lightuicompat;

import net.minecraftforge.fml.common.Mod;

/** GUI mixins are client-only; optional factory loot compatibility also runs on servers. */
@Mod(AE2LightUICompat.MOD_ID)
public final class AE2LightUICompat {
    // Shared ID for the mod and its dedicated resource-pack texture namespace.
    public static final String MOD_ID = "ae2lightuicompat";

    public AE2LightUICompat() {}
}
