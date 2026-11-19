package local.ae2lightuicompat.test;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.HashMap;
import java.util.Map;

public final class TestProperties implements IGlobalPropertyService {
    private final Map<IPropertyKey, Object> properties = new HashMap<>();

    private record Key(String name) implements IPropertyKey {}

    @Override
    public IPropertyKey resolveKey(String name) {
        return new Key(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IPropertyKey key) {
        return (T) properties.get(key);
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        properties.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IPropertyKey key, T fallback) {
        return (T) properties.getOrDefault(key, fallback);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String fallback) {
        Object value = properties.get(key);
        return value == null ? fallback : value.toString();
    }
}
