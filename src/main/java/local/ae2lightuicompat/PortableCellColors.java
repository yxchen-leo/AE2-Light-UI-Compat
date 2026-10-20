package local.ae2lightuicompat;

/** Applied Mekanistics 1.6.3 default screen color; explicitly dyed cells keep their color. */
public final class PortableCellColors {
    public static final int CHEMICAL_SCREEN = 8440575;
    private PortableCellColors() {}

    public static int resolve(int original, String namespace, String path, int tint,
                              boolean explicitlyDyed, boolean theme) {
        if (!theme || tint != 2 || explicitlyDyed || !namespace.equals("appmek")) return original;
        return switch (path) {
            case "portable_chemical_storage_cell_1k", "portable_chemical_storage_cell_4k",
                 "portable_chemical_storage_cell_16k", "portable_chemical_storage_cell_64k",
                 "portable_chemical_storage_cell_256k" -> CHEMICAL_SCREEN;
            default -> original;
        };
    }
}
