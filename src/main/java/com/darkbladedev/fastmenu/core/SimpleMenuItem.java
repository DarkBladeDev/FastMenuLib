package com.darkbladedev.fastmenu.core;

import com.darkbladedev.fastmenu.api.MenuItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Simple implementation of MenuItem for basic use cases.
 * Supports both static and dynamic items with optional click actions.
 */
public class SimpleMenuItem implements MenuItem {

    private final Function<Player, ItemStack> itemProvider;
    private final BiConsumer<Player, ClickType> clickAction;
    private final boolean clickable;
    private final boolean dynamic;
    private final int slot;

    /**
     * Creates a new SimpleMenuItem.
     *
     * @param itemProvider the function that provides the ItemStack
     * @param clickAction  the click action (can be null)
     * @param clickable    whether the item is clickable
     * @param dynamic      whether the item is dynamic
     * @param slot         the slot position (-1 if not set)
     */
    private SimpleMenuItem(@NotNull Function<Player, ItemStack> itemProvider,
                          @Nullable BiConsumer<Player, ClickType> clickAction,
                          boolean clickable,
                          boolean dynamic,
                          int slot) {
        this.itemProvider = Objects.requireNonNull(itemProvider, "Item provider cannot be null");
        this.clickAction = clickAction;
        this.clickable = clickable;
        this.dynamic = dynamic;
        this.slot = slot;
    }

    /**
     * Creates a static MenuItem with the specified ItemStack.
     *
     * @param itemStack the ItemStack
     * @return a new SimpleMenuItem
     */
    @NotNull
    public static SimpleMenuItem of(@NotNull ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "ItemStack cannot be null");
        return new SimpleMenuItem(player -> itemStack.clone(), null, false, false, -1);
    }

    /**
     * Creates a static MenuItem with the specified ItemStack and click action.
     *
     * @param itemStack the ItemStack
     * @param action    the click action
     * @return a new SimpleMenuItem
     */
    @NotNull
    public static SimpleMenuItem of(@NotNull ItemStack itemStack, @NotNull Consumer<Player> action) {
        Objects.requireNonNull(itemStack, "ItemStack cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        return new SimpleMenuItem(
            player -> itemStack.clone(),
            (player, clickType) -> action.accept(player),
            true,
            false,
            -1
        );
    }

    /**
     * Creates a static MenuItem with the specified ItemStack and click action with click type.
     *
     * @param itemStack the ItemStack
     * @param action    the click action with click type
     * @return a new SimpleMenuItem
     */
    @NotNull
    public static SimpleMenuItem of(@NotNull ItemStack itemStack, @NotNull BiConsumer<Player, ClickType> action) {
        Objects.requireNonNull(itemStack, "ItemStack cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        return new SimpleMenuItem(player -> itemStack.clone(), action, true, false, -1);
    }

    /**
     * Creates a dynamic MenuItem with the specified item provider.
     *
     * @param itemProvider the function that provides the ItemStack based on the player
     * @return a new SimpleMenuItem
     */
    @NotNull
    public static SimpleMenuItem dynamic(@NotNull Function<Player, ItemStack> itemProvider) {
        Objects.requireNonNull(itemProvider, "Item provider cannot be null");
        return new SimpleMenuItem(itemProvider, null, false, true, -1);
    }

    /**
     * Creates a dynamic MenuItem with the specified item provider and click action.
     *
     * @param itemProvider the function that provides the ItemStack based on the player
     * @param action       the click action
     * @return a new SimpleMenuItem
     */
    @NotNull
    public static SimpleMenuItem dynamic(@NotNull Function<Player, ItemStack> itemProvider,
                                        @NotNull Consumer<Player> action) {
        Objects.requireNonNull(itemProvider, "Item provider cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        return new SimpleMenuItem(
            itemProvider,
            (player, clickType) -> action.accept(player),
            true,
            true,
            -1
        );
    }

    /**
     * Creates a dynamic MenuItem with the specified item provider and click action with click type.
     *
     * @param itemProvider the function that provides the ItemStack based on the player
     * @param action       the click action with click type
     * @return a new SimpleMenuItem
     */
    @NotNull
    public static SimpleMenuItem dynamic(@NotNull Function<Player, ItemStack> itemProvider,
                                        @NotNull BiConsumer<Player, ClickType> action) {
        Objects.requireNonNull(itemProvider, "Item provider cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        return new SimpleMenuItem(itemProvider, action, true, true, -1);
    }

    /**
     * Creates a non-clickable MenuItem (decoration only).
     *
     * @param itemStack the ItemStack
     * @return a new SimpleMenuItem
     */
    @NotNull
    public static SimpleMenuItem decoration(@NotNull ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "ItemStack cannot be null");
        return new SimpleMenuItem(player -> itemStack.clone(), null, false, false, -1);
    }

    /**
     * Creates a dynamic non-clickable MenuItem (decoration only).
     *
     * @param itemProvider the function that provides the ItemStack based on the player
     * @return a new SimpleMenuItem
     */
    @NotNull
    public static SimpleMenuItem dynamicDecoration(@NotNull Function<Player, ItemStack> itemProvider) {
        Objects.requireNonNull(itemProvider, "Item provider cannot be null");
        return new SimpleMenuItem(itemProvider, null, false, true, -1);
    }

    @Override
    @NotNull
    public ItemStack getItemStack() {
        return itemProvider.apply(null);
    }

    @Override
    @NotNull
    public ItemStack getItemStack(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        ItemStack item = itemProvider.apply(player);
        return item != null ? item : new ItemStack(org.bukkit.Material.AIR);
    }

    @Override
    @Nullable
    public BiConsumer<Player, ClickType> getClickAction() {
        return clickAction;
    }

    @Nullable
    public Consumer<Player> getSimpleClickAction() {
        if (clickAction == null) {
            return null;
        }
        return player -> clickAction.accept(player, ClickType.LEFT);
    }

    public boolean handleClick(@NotNull Player player, @NotNull ClickType clickType) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(clickType, "Click type cannot be null");
        
        if (!clickable || clickAction == null) {
            return false;
        }
        
        try {
            clickAction.accept(player, clickType);
            return true;
        } catch (Exception e) {
            // Log the error but don't crash the menu system
            player.sendMessage("§cAn error occurred while processing your click.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setClickAction(@Nullable BiConsumer<Player, ClickType> action) {
        // SimpleMenuItem is immutable, so this method is not supported
        throw new UnsupportedOperationException("SimpleMenuItem is immutable. Use withAction() to create a new instance with a different action.");
    }

    @Override
    public boolean isClickable() {
        return clickable;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public void update(@NotNull Player player) {
        // For SimpleMenuItem, updating is handled by the menu itself
        // when it calls getItemStack() again
    }

    @Override
    @NotNull
    public MenuItem copy() {
        return new SimpleMenuItem(itemProvider, clickAction, clickable, dynamic, slot);
    }

    /**
     * Creates a copy of this MenuItem with a different slot.
     *
     * @param newSlot the new slot position
     * @return a new SimpleMenuItem with the specified slot
     */
    @NotNull
    public SimpleMenuItem withSlot(int newSlot) {
        return new SimpleMenuItem(itemProvider, clickAction, clickable, dynamic, newSlot);
    }

    /**
     * Creates a copy of this MenuItem with a different click action.
     *
     * @param newAction the new click action
     * @return a new SimpleMenuItem with the specified action
     */
    @NotNull
    public SimpleMenuItem withAction(@NotNull Consumer<Player> newAction) {
        Objects.requireNonNull(newAction, "Action cannot be null");
        return new SimpleMenuItem(
            itemProvider,
            (player, clickType) -> newAction.accept(player),
            true,
            dynamic,
            slot
        );
    }

    /**
     * Creates a copy of this MenuItem with a different click action that receives click type.
     *
     * @param newAction the new click action with click type
     * @return a new SimpleMenuItem with the specified action
     */
    @NotNull
    public SimpleMenuItem withAction(@NotNull BiConsumer<Player, ClickType> newAction) {
        Objects.requireNonNull(newAction, "Action cannot be null");
        return new SimpleMenuItem(itemProvider, newAction, true, dynamic, slot);
    }

    /**
     * Creates a copy of this MenuItem with clickable state changed.
     *
     * @param clickable whether the item should be clickable
     * @return a new SimpleMenuItem with the specified clickable state
     */
    @NotNull
    public SimpleMenuItem withClickable(boolean clickable) {
        return new SimpleMenuItem(itemProvider, clickAction, clickable, dynamic, slot);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SimpleMenuItem that = (SimpleMenuItem) obj;
        return clickable == that.clickable &&
               dynamic == that.dynamic &&
               slot == that.slot &&
               Objects.equals(itemProvider, that.itemProvider) &&
               Objects.equals(clickAction, that.clickAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemProvider, clickAction, clickable, dynamic, slot);
    }

    @Override
    public String toString() {
        return "SimpleMenuItem{" +
                "clickable=" + clickable +
                ", dynamic=" + dynamic +
                ", slot=" + slot +
                ", hasAction=" + (clickAction != null) +
                '}';
    }
}