package com.darkbladedev.fastmenu.core;

import com.darkbladedev.fastmenu.api.Menu;
import com.darkbladedev.fastmenu.api.MenuItem;
import com.darkbladedev.fastmenu.utils.ItemBuilder;
import com.darkbladedev.fastmenu.utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Builder class for creating menus using a fluent API.
 * 
 * <p>MenuBuilder provides a convenient and flexible way to construct {@link Menu} instances
 * using the builder pattern. It offers a fluent API that makes menu creation intuitive
 * and readable, while providing comprehensive validation and error handling.</p>
 * 
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li><strong>Fluent API</strong> - Chain method calls for readable menu construction</li>
 *   <li><strong>Type Safety</strong> - Compile-time validation of menu parameters</li>
 *   <li><strong>Flexible Item Placement</strong> - Multiple ways to add and configure items</li>
 *   <li><strong>Event Handling</strong> - Custom open/close actions with lambda support</li>
 *   <li><strong>Validation</strong> - Comprehensive input validation and error messages</li>
 *   <li><strong>Performance</strong> - Efficient internal data structures and lazy evaluation</li>
 * </ul>
 * 
 * <p><strong>Usage Examples:</strong></p>
 * <pre>{@code
 * // Basic menu creation
 * Menu simpleMenu = MenuBuilder.create("shop", "Shop Menu", 27)
 *     .item(10, ItemBuilder.create(Material.DIAMOND)
 *         .name("Diamond")
 *         .lore("Click to buy!")
 *         .build())
 *     .onClick(10, (player, clickType) -> {
 *         player.sendMessage("You bought a diamond!");
 *     })
 *     .build();
 * 
 * // Advanced menu with custom actions
 * Menu advancedMenu = MenuBuilder.create("settings", "Settings", 54)
 *     .fillBorder(Material.GRAY_STAINED_GLASS_PANE)
 *     .item(22, Material.REDSTONE, "Toggle PvP", 
 *           "Click to toggle PvP mode")
 *     .onOpen(player -> player.sendMessage("Welcome to settings!"))
 *     .onClose(player -> player.sendMessage("Settings saved!"))
 *     .build();
 * 
 * // Dynamic content with context
 * Menu dynamicMenu = MenuBuilder.create("profile", "Player Profile", 36)
 *     .dynamicItem(13, player -> {
 *         return ItemBuilder.create(Material.PLAYER_HEAD)
 *             .name(player.getName())
 *             .lore("Level: " + player.getLevel())
 *             .build();
 *     })
 *     .build();
 * }</pre>
 * 
 * <p><strong>Thread Safety:</strong></p>
 * <p>MenuBuilder instances are <strong>not thread-safe</strong> and should not be shared
 * between threads during construction. However, the resulting {@link Menu} instances
 * are thread-safe and can be safely used across multiple threads.</p>
 * 
 * <p><strong>Memory Management:</strong></p>
 * <p>MenuBuilder uses efficient internal data structures and performs validation
 * during construction rather than at runtime, ensuring optimal performance of
 * the resulting menu instances.</p>
 * 
 * @author DarkBladeDev
 * @since 1.0.0
 * @see Menu
 * @see MenuItem
 * @see AbstractMenu
 */
public final class MenuBuilder {

    private final String id;
    private final Component title;
    private final int size;
    private final Map<Integer, MenuItem> items;
    
    private Consumer<Player> onOpenAction;
    private Consumer<Player> onCloseAction;

    /**
     * Private constructor for the builder.
     *
     * @param id    the menu ID
     * @param title the menu title
     * @param size  the menu size
     */
    private MenuBuilder(@NotNull String id, @NotNull Component title, int size) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.items = new HashMap<>();
    }

    /**
     * Creates a new MenuBuilder with the specified parameters.
     *
     * @param id    the unique identifier for the menu
     * @param title the title of the menu (supports MiniMessage format)
     * @param rows  the number of rows (1-6)
     * @return a new MenuBuilder instance
     */
    @NotNull
    public static MenuBuilder create(@NotNull String id, @NotNull String title, int rows) {
        Objects.requireNonNull(id, "Menu ID cannot be null");
        Objects.requireNonNull(title, "Menu title cannot be null");
        
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows must be between 1 and 6");
        }
        
        Component titleComponent = TextUtil.parseMiniMessage(title);
        return new MenuBuilder(id, titleComponent, rows * 9);
    }

    /**
     * Creates a new MenuBuilder with the specified parameters.
     *
     * @param id    the unique identifier for the menu
     * @param title the title component of the menu
     * @param rows  the number of rows (1-6)
     * @return a new MenuBuilder instance
     */
    @NotNull
    public static MenuBuilder create(@NotNull String id, @NotNull Component title, int rows) {
        Objects.requireNonNull(id, "Menu ID cannot be null");
        Objects.requireNonNull(title, "Menu title cannot be null");
        
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows must be between 1 and 6");
        }
        
        return new MenuBuilder(id, title, rows * 9);
    }

    /**
     * Creates a new MenuBuilder with exact size specification.
     *
     * @param id    the unique identifier for the menu
     * @param title the title of the menu (supports MiniMessage format)
     * @param size  the exact size of the menu (must be multiple of 9, max 54)
     * @return a new MenuBuilder instance
     */
    @NotNull
    public static MenuBuilder createWithSize(@NotNull String id, @NotNull String title, int size) {
        Objects.requireNonNull(id, "Menu ID cannot be null");
        Objects.requireNonNull(title, "Menu title cannot be null");
        
        Component titleComponent = TextUtil.parseMiniMessage(title);
        return new MenuBuilder(id, titleComponent, size);
    }

    /**
     * Sets an item at the specified slot.
     *
     * @param slot the slot position
     * @param item the MenuItem to set
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder setItem(int slot, @NotNull MenuItem item) {
        Objects.requireNonNull(item, "MenuItem cannot be null");
        validateSlot(slot);
        items.put(slot, item);
        return this;
    }

    /**
     * Sets an item at the specified slot using an ItemStack.
     *
     * @param slot      the slot position
     * @param itemStack the ItemStack to set
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder setItem(int slot, @NotNull ItemStack itemStack) {
        return setItem(slot, SimpleMenuItem.of(itemStack));
    }

    /**
     * Sets an item at the specified slot using an ItemBuilder.
     *
     * @param slot        the slot position
     * @param itemBuilder the ItemBuilder to use
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder setItem(int slot, @NotNull ItemBuilder itemBuilder) {
        return setItem(slot, itemBuilder.build());
    }

    /**
     * Sets an item with a click action.
     *
     * @param slot      the slot position
     * @param itemStack the ItemStack to set
     * @param action    the click action
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder setItem(int slot, @NotNull ItemStack itemStack, @NotNull Consumer<Player> action) {
        return setItem(slot, SimpleMenuItem.of(itemStack, action));
    }

    /**
     * Sets an item with a click action that receives click type.
     *
     * @param slot      the slot position
     * @param itemStack the ItemStack to set
     * @param action    the click action with click type
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder setItem(int slot, @NotNull ItemStack itemStack, @NotNull BiConsumer<Player, ClickType> action) {
        return setItem(slot, SimpleMenuItem.of(itemStack, action));
    }

    /**
     * Sets an item with a dynamic ItemStack provider.
     *
     * @param slot     the slot position
     * @param provider the function that provides the ItemStack based on the player
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder setDynamicItem(int slot, @NotNull Function<Player, ItemStack> provider) {
        return setItem(slot, SimpleMenuItem.dynamic(provider));
    }

    /**
     * Sets an item with a dynamic ItemStack provider and click action.
     *
     * @param slot     the slot position
     * @param provider the function that provides the ItemStack based on the player
     * @param action   the click action
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder setDynamicItem(int slot, @NotNull Function<Player, ItemStack> provider, 
                                     @NotNull Consumer<Player> action) {
        return setItem(slot, SimpleMenuItem.dynamic(provider, action));
    }

    /**
     * Sets an item with a dynamic ItemStack provider and click action with click type.
     *
     * @param slot     the slot position
     * @param provider the function that provides the ItemStack based on the player
     * @param action   the click action with click type
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder setDynamicItem(int slot, @NotNull Function<Player, ItemStack> provider, 
                                     @NotNull BiConsumer<Player, ClickType> action) {
        return setItem(slot, SimpleMenuItem.dynamic(provider, action));
    }

    /**
     * Fills empty slots with the specified item.
     *
     * @param itemStack the ItemStack to fill with
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder fillEmpty(@NotNull ItemStack itemStack) {
        for (int i = 0; i < size; i++) {
            if (!items.containsKey(i)) {
                setItem(i, itemStack);
            }
        }
        return this;
    }

    /**
     * Fills empty slots with the specified material.
     *
     * @param material the Material to fill with
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder fillEmpty(@NotNull Material material) {
        return fillEmpty(new ItemStack(material));
    }

    /**
     * Fills the border of the menu with the specified item.
     *
     * @param itemStack the ItemStack to use for the border
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder fillBorder(@NotNull ItemStack itemStack) {
        int rows = size / 9;
        
        // Top and bottom rows
        for (int i = 0; i < 9; i++) {
            setItem(i, itemStack); // Top row
            if (rows > 1) {
                setItem((rows - 1) * 9 + i, itemStack); // Bottom row
            }
        }
        
        // Left and right columns (excluding corners already set)
        for (int row = 1; row < rows - 1; row++) {
            setItem(row * 9, itemStack); // Left column
            setItem(row * 9 + 8, itemStack); // Right column
        }
        
        return this;
    }

    /**
     * Fills the border of the menu with the specified material.
     *
     * @param material the Material to use for the border
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder fillBorder(@NotNull Material material) {
        return fillBorder(new ItemStack(material));
    }

    /**
     * Sets a row of items.
     *
     * @param row   the row number (0-based)
     * @param items the items to set in the row
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder setRow(int row, @NotNull ItemStack... items) {
        if (row < 0 || row >= size / 9) {
            throw new IllegalArgumentException("Row " + row + " is out of bounds");
        }
        
        for (int i = 0; i < Math.min(items.length, 9); i++) {
            if (items[i] != null) {
                setItem(row * 9 + i, items[i]);
            }
        }
        
        return this;
    }

    /**
     * Sets a column of items.
     *
     * @param column the column number (0-8)
     * @param items  the items to set in the column
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder setColumn(int column, @NotNull ItemStack... items) {
        if (column < 0 || column >= 9) {
            throw new IllegalArgumentException("Column " + column + " is out of bounds");
        }
        
        int rows = size / 9;
        for (int i = 0; i < Math.min(items.length, rows); i++) {
            if (items[i] != null) {
                setItem(i * 9 + column, items[i]);
            }
        }
        
        return this;
    }

    /**
     * Sets an action to be executed when the menu is opened.
     *
     * @param action the action to execute
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder onOpen(@NotNull Consumer<Player> action) {
        this.onOpenAction = Objects.requireNonNull(action, "Open action cannot be null");
        return this;
    }

    /**
     * Sets an action to be executed when the menu is closed.
     *
     * @param action the action to execute
     * @return this builder for chaining
     */
    @NotNull
    public MenuBuilder onClose(@NotNull Consumer<Player> action) {
        this.onCloseAction = Objects.requireNonNull(action, "Close action cannot be null");
        return this;
    }

    /**
     * Builds the menu.
     *
     * @return the constructed Menu
     */
    @NotNull
    public Menu build() {
        return new BuiltMenu(id, title, size, items, onOpenAction, onCloseAction);
    }

    /**
     * Validates that a slot is within the valid range.
     *
     * @param slot the slot to validate
     */
    private void validateSlot(int slot) {
        if (slot < 0 || slot >= size) {
            throw new IllegalArgumentException("Slot " + slot + " is out of bounds for menu size " + size);
        }
    }

    /**
     * Internal implementation of Menu created by MenuBuilder.
     */
    private static class BuiltMenu extends AbstractMenu {
        
        private final Consumer<Player> onOpenAction;
        private final Consumer<Player> onCloseAction;

        public BuiltMenu(@NotNull String id, @NotNull Component title, int size, 
                        @NotNull Map<Integer, MenuItem> items,
                        @Nullable Consumer<Player> onOpenAction,
                        @Nullable Consumer<Player> onCloseAction) {
            super(id, title, size);
            
            // Set all items
            for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
                super.setItem(entry.getKey(), entry.getValue());
            }
            
            this.onOpenAction = onOpenAction;
            this.onCloseAction = onCloseAction;
        }

        @Override
        public void onOpen(@NotNull Player player) {
            if (onOpenAction != null) {
                onOpenAction.accept(player);
            }
        }

        @Override
        public void onClose(@NotNull Player player) {
            if (onCloseAction != null) {
                onCloseAction.accept(player);
            }
        }

        @Override
        public void refresh() {
            refreshAll();
        }
    }
}