package com.darkbladedev.fastmenu.api;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents a GUI menu that can be displayed to players.
 * 
 * <p>Menu is the core interface for all menu implementations in the FastMenu library.
 * It provides a comprehensive API for creating, managing, and displaying interactive
 * GUI menus to Minecraft players using Bukkit inventories.</p>
 * 
 * <p>Key features of Menu implementations:</p>
 * <ul>
 *   <li><strong>Inventory Management</strong> - Automatic creation and management of Bukkit inventories</li>
 *   <li><strong>Item Placement</strong> - Easy placement and removal of menu items in specific slots</li>
 *   <li><strong>Player Context</strong> - Per-player data storage for dynamic content</li>
 *   <li><strong>Event Handling</strong> - Built-in click and interaction event management</li>
 *   <li><strong>Lifecycle Management</strong> - Open/close callbacks and state tracking</li>
 *   <li><strong>Dynamic Updates</strong> - Real-time menu content updates and refreshing</li>
 * </ul>
 * 
 * <p><strong>Menu Lifecycle:</strong></p>
 * <ol>
 *   <li><strong>Creation</strong> - Menu is created with a unique ID, title, and size</li>
 *   <li><strong>Configuration</strong> - Items are added and configured using {@link #setItem(int, MenuItem)}</li>
 *   <li><strong>Opening</strong> - Menu is opened for a player using {@link #open(Player)}</li>
 *   <li><strong>Interaction</strong> - Player clicks are handled by menu items</li>
 *   <li><strong>Updates</strong> - Menu content can be refreshed using {@link #refresh()} or {@link #refresh(Player)}</li>
 *   <li><strong>Closing</strong> - Menu is closed automatically or manually using {@link #close(Player)}</li>
 * </ol>
 * 
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 * // Creating a simple menu using MenuBuilder
 * Menu menu = MenuBuilder.create("example_menu", "§6Example Menu", 3)
 *     .setItem(10, SimpleMenuItem.of(
 *         new ItemBuilder(Material.DIAMOND)
 *             .name("§bDiamond")
 *             .lore("§7Click to get diamonds!")
 *             .build(),
 *         (player, clickType) -> {
 *             player.getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
 *             player.sendMessage("§aYou received 5 diamonds!");
 *         }
 *     ))
 *     .setItem(16, SimpleMenuItem.of(
 *         new ItemBuilder(Material.BARRIER)
 *             .name("§cClose")
 *             .build(),
 *         MenuAction.CLOSE
 *     ))
 *     .build();
 * 
 * // Opening the menu for a player
 * menu.open(player);
 * 
 * // Using menu context for player-specific data
 * MenuContext context = menu.getContext(player);
 * context.set("last_opened", System.currentTimeMillis());
 * context.set("visit_count", context.getOrDefault("visit_count", 0) + 1);
 * }</pre>
 * 
 * @author DarkBladeDev
 * @since 1.0.0
 * @see com.darkbladedev.fastmenu.core.MenuBuilder
 * @see com.darkbladedev.fastmenu.core.AbstractMenu
 * @see com.darkbladedev.fastmenu.api.MenuItem
 * @see com.darkbladedev.fastmenu.api.MenuContext
 */
public interface Menu {

    /**
     * Gets the unique identifier for this menu.
     *
     * @return the menu's unique identifier
     */
    @NotNull
    String getId();

    /**
     * Gets the title of this menu.
     *
     * @return the menu title as a Component
     */
    @NotNull
    Component getTitle();

    /**
     * Gets the size of this menu (number of slots).
     *
     * @return the menu size
     */
    int getSize();

    /**
     * Gets the Bukkit inventory associated with this menu.
     *
     * @return the inventory instance
     */
    @NotNull
    Inventory getInventory();

    /**
     * Gets all menu items in this menu.
     *
     * @return a map of slot positions to menu items
     */
    @NotNull
    Map<Integer, MenuItem> getItems();

    /**
     * Gets a menu item at the specified slot.
     *
     * @param slot the slot position
     * @return the menu item, or null if no item exists at that slot
     */
    @Nullable
    MenuItem getItem(int slot);

    /**
     * Sets a menu item at the specified slot.
     *
     * @param slot the slot position
     * @param item the menu item to set
     */
    void setItem(int slot, @NotNull MenuItem item);

    /**
     * Removes a menu item from the specified slot.
     *
     * @param slot the slot position
     */
    void removeItem(int slot);

    /**
     * Opens this menu for the specified player.
     *
     * @param player the player to open the menu for
     */
    void open(@NotNull Player player);

    /**
     * Closes this menu for the specified player.
     *
     * @param player the player to close the menu for
     */
    void close(@NotNull Player player);

    /**
     * Refreshes the menu content for all viewers.
     * This will update all items and their visual representation.
     */
    void refresh();

    /**
     * Refreshes the menu content for a specific player.
     *
     * @param player the player to refresh the menu for
     */
    void refresh(@NotNull Player player);

    /**
     * Checks if this menu is currently open for the specified player.
     *
     * @param player the player to check
     * @return true if the menu is open for the player
     */
    boolean isOpen(@NotNull Player player);

    /**
     * Gets the menu context for a specific player.
     * Context can store player-specific data for dynamic menus.
     *
     * @param player the player
     * @return the menu context, or null if no context exists
     */
    @Nullable
    MenuContext getContext(@NotNull Player player);

    /**
     * Sets the menu context for a specific player.
     *
     * @param player the player
     * @param context the menu context
     */
    void setContext(@NotNull Player player, @NotNull MenuContext context);

    /**
     * Called when the menu is opened for a player.
     * Override this method to implement custom open behavior.
     *
     * @param player the player who opened the menu
     */
    default void onOpen(@NotNull Player player) {
        // Default implementation does nothing
    }

    /**
     * Called when the menu is closed for a player.
     * Override this method to implement custom close behavior.
     *
     * @param player the player who closed the menu
     */
    default void onClose(@NotNull Player player) {
        // Default implementation does nothing
    }
}