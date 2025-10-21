package com.darkbladedev.fastmenu.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a context that stores player-specific data for menus.
 * This allows menus to maintain state and dynamic content per player.
 */
public class MenuContext {

    private final Map<String, Object> data;

    /**
     * Creates a new empty menu context.
     */
    public MenuContext() {
        this.data = new HashMap<>();
    }

    /**
     * Creates a new menu context with initial data.
     *
     * @param initialData the initial data map
     */
    public MenuContext(@NotNull Map<String, Object> initialData) {
        this.data = new HashMap<>(initialData);
    }

    /**
     * Sets a value in the context.
     *
     * @param key   the key
     * @param value the value
     * @return this context for chaining
     */
    @NotNull
    public MenuContext set(@NotNull String key, @Nullable Object value) {
        if (value == null) {
            data.remove(key);
        } else {
            data.put(key, value);
        }
        return this;
    }

    /**
     * Gets a value from the context.
     *
     * @param key the key
     * @return the value, or null if not found
     */
    @Nullable
    public Object get(@NotNull String key) {
        return data.get(key);
    }

    /**
     * Gets a value from the context with a specific type.
     *
     * @param key  the key
     * @param type the expected type
     * @param <T>  the type parameter
     * @return an Optional containing the value if found and of correct type
     */
    @NotNull
    public <T> Optional<T> get(@NotNull String key, @NotNull Class<T> type) {
        Object value = data.get(key);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        }
        return Optional.empty();
    }

    /**
     * Gets a value from the context with a default fallback.
     *
     * @param key          the key
     * @param defaultValue the default value if not found
     * @param <T>          the type parameter
     * @return the value or default value
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(@NotNull String key, @NotNull T defaultValue) {
        Object value = data.get(key);
        if (value != null && defaultValue.getClass().isInstance(value)) {
            return (T) value;
        }
        return defaultValue;
    }

    /**
     * Checks if the context contains a key.
     *
     * @param key the key to check
     * @return true if the key exists
     */
    public boolean contains(@NotNull String key) {
        return data.containsKey(key);
    }

    /**
     * Removes a value from the context.
     *
     * @param key the key to remove
     * @return the removed value, or null if not found
     */
    @Nullable
    public Object remove(@NotNull String key) {
        return data.remove(key);
    }

    /**
     * Clears all data from the context.
     */
    public void clear() {
        data.clear();
    }

    /**
     * Gets the size of the context.
     *
     * @return the number of key-value pairs
     */
    public int size() {
        return data.size();
    }

    /**
     * Checks if the context is empty.
     *
     * @return true if the context contains no data
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Gets a copy of all data in the context.
     *
     * @return a new map containing all context data
     */
    @NotNull
    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }
}