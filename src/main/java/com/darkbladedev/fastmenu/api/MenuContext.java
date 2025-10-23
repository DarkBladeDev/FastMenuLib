package com.darkbladedev.fastmenu.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a context that stores player-specific data for menus.
 * 
 * <p>MenuContext provides a flexible key-value storage system that allows menus
 * to maintain state and dynamic content on a per-player basis. This is essential
 * for creating personalized and interactive menu experiences.</p>
 * 
 * <p>Key features of MenuContext:</p>
 * <ul>
 *   <li><strong>Type-Safe Storage</strong> - Generic methods for type-safe data retrieval</li>
 *   <li><strong>Null Safety</strong> - Proper handling of null values and optional returns</li>
 *   <li><strong>Fluent API</strong> - Method chaining support for convenient usage</li>
 *   <li><strong>Default Values</strong> - Built-in support for default values when keys don't exist</li>
 *   <li><strong>Data Management</strong> - Complete CRUD operations for context data</li>
 * </ul>
 * 
 * <p><strong>Common Use Cases:</strong></p>
 * <ul>
 *   <li>Storing player preferences and settings</li>
 *   <li>Maintaining pagination state for multi-page menus</li>
 *   <li>Tracking user selections and form data</li>
 *   <li>Caching expensive calculations or database queries</li>
 *   <li>Managing temporary state during menu interactions</li>
 * </ul>
 * 
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 * // Creating and using a menu context
 * MenuContext context = new MenuContext();
 * 
 * // Storing different types of data
 * context.set("current_page", 1)
 *        .set("selected_items", Arrays.asList("item1", "item2"))
 *        .set("last_action", "purchase")
 *        .set("total_cost", 150.75);
 * 
 * // Type-safe retrieval with Optional
 * Optional<Integer> page = context.get("current_page", Integer.class);
 * if (page.isPresent()) {
 *     // Handle pagination logic
 * }
 * 
 * // Using default values
 * int currentPage = context.getOrDefault("current_page", 1);
 * List<String> items = context.getOrDefault("selected_items", new ArrayList<>());
 * 
 * // Checking existence and removing data
 * if (context.contains("temporary_data")) {
 *     Object removed = context.remove("temporary_data");
 * }
 * }</pre>
 * 
 * <p><strong>Thread Safety:</strong></p>
 * <p>MenuContext is <strong>not thread-safe</strong>. If you need to access the same
 * context from multiple threads, you must provide external synchronization.</p>
 * 
 * @author DarkBladeDev
 * @since 1.0.0
 * @see com.darkbladedev.fastmenu.api.Menu#getContext(org.bukkit.entity.Player)
 * @see com.darkbladedev.fastmenu.api.Menu#setContext(org.bukkit.entity.Player, MenuContext)
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