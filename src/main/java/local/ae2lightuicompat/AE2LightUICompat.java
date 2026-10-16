package local.ae2lightuicompat;

import net.minecraftforge.fml.common.Mod;

/** Client-only mixins live in the client list; this entrypoint is server-safe. */
@Mod(AE2LightUICompat.MOD_ID)
public final class AE2LightUICompat {
    // Stable ID also identifies the existing resource pack's private button texture.
    public static final String MOD_ID = "ae2isolatedui";

    public AE2LightUICompat() {}
}
