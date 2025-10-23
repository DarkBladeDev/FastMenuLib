package com.darkbladedev.fastmenu.api;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents an item within a menu that can be clicked and interacted with.
 * 
 * <p>MenuItem is the core interface for all interactive elements within a FastMenu.
 * It provides functionality for displaying items, handling click events, and managing
 * dynamic content that can change based on player context or menu state.</p>
 * 
 * <p>Menu items can be:</p>
 * <ul>
 *   <li><strong>Static</strong> - Items that never change their appearance or behavior</li>
 *   <li><strong>Dynamic</strong> - Items that update their content based on player data or menu context</li>
 *   <li><strong>Clickable</strong> - Items that respond to player clicks with custom actions</li>
 *   <li><strong>Contextual</strong> - Items that display different content for different players</li>
 * </ul>
 * 
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 * // Creating a simple static menu item
 * MenuItem item = SimpleMenuItem.of(
 *     new ItemBuilder(Material.DIAMOND)
 *         .name("§bDiamond Item")
 *         .lore("§7Click to get diamonds!")
 *         .build(),
 *     (player, clickType) -> {
 *         player.getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
 *         player.sendMessage("§aYou received 5 diamonds!");
 *     }
 * );
 * 
 * // Creating a dynamic menu item that changes based on player data
 * MenuItem dynamicItem = SimpleMenuItem.dynamic(
 *     player -> {
 *         int level = player.getLevel();
 *         return new ItemBuilder(Material.EXPERIENCE_BOTTLE)
 *             .name("§eYour Level: " + level)
 *             .lore("§7Current XP: " + player.getExp())
 *             .build();
 *     },
 *     (player, clickType) -> {
 *         player.setLevel(player.getLevel() + 1);
 *         // Item will automatically update when menu refreshes
 *     }
 * );
 * }</pre>
 * 
 * @author DarkBladeDev
 * @since 1.0.0
 * @see com.darkbladedev.fastmenu.core.SimpleMenuItem
 * @see com.darkbladedev.fastmenu.api.Menu
 * @see com.darkbladedev.fastmenu.core.MenuBuilder
 */
public interface MenuItem {

    /**
     * Gets the ItemStack representation of this menu item.
     *
     * @return the ItemStack
     */
    @NotNull
    ItemStack getItemStack();

    /**
     * Gets the ItemStack representation of this menu item for a specific player.
     * This allows for player-specific customization using placeholders or context.
     *
     * @param player the player viewing the item
     * @return the customized ItemStack
     */
    @NotNull
    default ItemStack getItemStack(@NotNull Player player) {
        return getItemStack();
    }

    /**
     * Gets the click action for this menu item.
     *
     * @return the click action, or null if no action is set
     */
    @Nullable
    BiConsumer<Player, ClickType> getClickAction();

    /**
     * Sets the click action for this menu item.
     *
     * @param action the click action
     */
    void setClickAction(@Nullable BiConsumer<Player, ClickType> action);

    /**
     * Sets a simple click action that doesn't care about click type.
     *
     * @param action the simple click action
     */
    default void setClickAction(@Nullable Consumer<Player> action) {
        if (action == null) {
            setClickAction((BiConsumer<Player, ClickType>) null);
        } else {
            setClickAction((player, clickType) -> action.accept(player));
        }
    }

    /**
     * Handles a click on this menu item.
     *
     * @param player    the player who clicked
     * @param clickType the type of click
     */
    default void onClick(@NotNull Player player, @NotNull ClickType clickType) {
        BiConsumer<Player, ClickType> action = getClickAction();
        if (action != null) {
            action.accept(player, clickType);
        }
    }

    /**
     * Checks if this menu item is clickable.
     *
     * @return true if the item has a click action
     */
    default boolean isClickable() {
        return getClickAction() != null;
    }

    /**
     * Gets the slot position of this item in the menu.
     * This may return -1 if the item is not placed in a menu yet.
     *
     * @return the slot position, or -1 if not placed
     */
    default int getSlot() {
        return -1;
    }

    /**
     * Checks if this menu item should be updated dynamically.
     * Dynamic items will be refreshed when the menu is refreshed.
     *
     * @return true if the item is dynamic
     */
    default boolean isDynamic() {
        return false;
    }

    /**
     * Updates this menu item for a specific player.
     * This is called when the menu is refreshed for dynamic items.
     *
     * @param player the player to update the item for
     */
    default void update(@NotNull Player player) {
        // Default implementation does nothing
    }

    /**
     * Creates a copy of this menu item.
     *
     * @return a new MenuItem instance with the same properties
     */
    @NotNull
    MenuItem copy();
}