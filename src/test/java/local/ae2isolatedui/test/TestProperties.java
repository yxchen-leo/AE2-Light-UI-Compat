package local.ae2isolatedui.test;

import java.util.*;
import org.spongepowered.asm.service.*;

public final class TestProperties implements IGlobalPropertyService {
    private final Map<IPropertyKey, Object> properties = new HashMap<>();
    private record Key(String name) implements IPropertyKey {}
    public IPropertyKey resolveKey(String name) { return new Key(name); }
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IPropertyKey key) { return (T) properties.get(key); }
    public void setProperty(IPropertyKey key, Object value) { properties.put(key, value); }
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IPropertyKey key, T fallback) { return (T) properties.getOrDefault(key, fallback); }
    public String getPropertyString(IPropertyKey key, String fallback) {
        Object value = properties.get(key);
        return value == null ? fallback : value.toString();
    }
}
