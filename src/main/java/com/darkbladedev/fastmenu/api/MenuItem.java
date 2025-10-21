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