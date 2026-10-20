package local.ae2lightuicompat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;

/**
 * Ignore only byte-identified mekmm 1.1.2 optional factory tables whose block
 * AND item are absent. Existing factories and modified datapack tables pass through.
 */
public final class OptionalFactoryLoot {
    private static final Map<String, String> ORIGINAL_TABLES = loadFingerprints();

    private OptionalFactoryLoot() {}

    public static boolean shouldSkip(String directory, ResourceLocation table, JsonElement json,
                                     Predicate<ResourceLocation> itemExists,
                                     Predicate<ResourceLocation> blockExists) {
        if (!"loot_tables".equals(directory)) return false;
        String expected = ORIGINAL_TABLES.get(table.toString());
        if (expected == null) return false;
        ResourceLocation machine = new ResourceLocation("mekmm", table.getPath().substring("blocks/".length()));
        if (itemExists.test(machine) || blockExists.test(machine)) return false;
        // Do not discard a user replacement, including one that drops a different item.
        return expected.equals(fingerprint(json));
    }

    private static String fingerprint(JsonElement json) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(json.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is required by Java", e);
        }
    }

    private static Map<String, String> loadFingerprints() {
        var stream = OptionalFactoryLoot.class.getResourceAsStream("/compat/mekmm-1.1.2-optional-loot.json");
        if (stream == null) throw new IllegalStateException("Missing optional factory loot fingerprints");
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            Map<String, String> result = new HashMap<>();
            JsonParser.parseReader(reader).getAsJsonObject().entrySet()
                    .forEach(entry -> result.put(entry.getKey(), entry.getValue().getAsString()));
            return Map.copyOf(result);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot read optional factory loot fingerprints", e);
        }
    }
}
