package com.darkbladedev.fastmenu.api;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents a GUI menu that can be displayed to players.
 * This is the main interface for all menu implementations.
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