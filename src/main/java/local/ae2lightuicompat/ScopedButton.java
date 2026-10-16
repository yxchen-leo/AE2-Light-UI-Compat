package local.ae2lightuicompat;

/** Marker attached only when AE2's own WidgetContainer registers a button. */
public interface ScopedButton {
    void ae2lightuicompat$markOwned();
}
