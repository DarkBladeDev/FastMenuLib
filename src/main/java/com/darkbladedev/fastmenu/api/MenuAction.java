package com.darkbladedev.fastmenu.api;

import com.darkbladedev.fastmenu.core.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Utility class containing common menu actions that can be used with menu items.
 * These actions provide standard functionality like closing menus, refreshing, etc.
 */
public final class MenuAction {

    private MenuAction() {
        // Utility class
    }

    /**
     * Action that closes the current menu for the player.
     */
    public static final BiConsumer<Player, ClickType> CLOSE = (player, clickType) -> {
        MenuManager.getInstance().closeMenu(player);
    };

    /**
     * Action that refreshes the current menu for the player.
     */
    public static final BiConsumer<Player, ClickType> REFRESH = (player, clickType) -> {
        Menu menu = MenuManager.getInstance().getOpenMenu(player);
        if (menu != null) {
            menu.refresh(player);
        }
    };

    /**
     * Action that does nothing (no-op).
     */
    public static final BiConsumer<Player, ClickType> NONE = (player, clickType) -> {
        // Do nothing
    };

    /**
     * Creates an action that opens another menu.
     *
     * @param menu the menu to open
     * @return the action
     */
    @NotNull
    public static BiConsumer<Player, ClickType> openMenu(@NotNull Menu menu) {
        return (player, clickType) -> menu.open(player);
    }

    /**
     * Creates an action that opens a menu provided by a supplier.
     * This is useful for dynamic menu creation.
     *
     * @param menuProvider the menu provider
     * @return the action
     */
    @NotNull
    public static BiConsumer<Player, ClickType> openMenu(@NotNull MenuProvider menuProvider) {
        return (player, clickType) -> {
            Menu menu = menuProvider.provide(player);
            if (menu != null) {
                menu.open(player);
            }
        };
    }

    /**
     * Creates an action that executes a command as the player.
     *
     * @param command the command to execute (without leading slash)
     * @return the action
     */
    @NotNull
    public static BiConsumer<Player, ClickType> executeCommand(@NotNull String command) {
        return (player, clickType) -> player.performCommand(command);
    }

    /**
     * Creates an action that sends a message to the player.
     *
     * @param message the message to send
     * @return the action
     */
    @NotNull
    public static BiConsumer<Player, ClickType> sendMessage(@NotNull String message) {
        return (player, clickType) -> player.sendMessage(message);
    }

    /**
     * Creates an action that combines multiple actions.
     * All actions will be executed in order.
     *
     * @param actions the actions to combine
     * @return the combined action
     */
    @NotNull
    @SafeVarargs
    public static BiConsumer<Player, ClickType> combine(@NotNull BiConsumer<Player, ClickType>... actions) {
        return (player, clickType) -> {
            for (BiConsumer<Player, ClickType> action : actions) {
                if (action != null) {
                    action.accept(player, clickType);
                }
            }
        };
    }

    /**
     * Creates an action that only executes for specific click types.
     *
     * @param action     the action to execute
     * @param clickTypes the allowed click types
     * @return the conditional action
     */
    @NotNull
    public static BiConsumer<Player, ClickType> onlyFor(@NotNull BiConsumer<Player, ClickType> action, 
                                                        @NotNull ClickType... clickTypes) {
        return (player, clickType) -> {
            for (ClickType allowedType : clickTypes) {
                if (clickType == allowedType) {
                    action.accept(player, clickType);
                    return;
                }
            }
        };
    }

    /**
     * Creates an action that only executes for left clicks.
     *
     * @param action the action to execute
     * @return the left-click-only action
     */
    @NotNull
    public static BiConsumer<Player, ClickType> leftClickOnly(@NotNull BiConsumer<Player, ClickType> action) {
        return onlyFor(action, ClickType.LEFT, ClickType.SHIFT_LEFT);
    }

    /**
     * Creates an action that only executes for right clicks.
     *
     * @param action the action to execute
     * @return the right-click-only action
     */
    @NotNull
    public static BiConsumer<Player, ClickType> rightClickOnly(@NotNull BiConsumer<Player, ClickType> action) {
        return onlyFor(action, ClickType.RIGHT, ClickType.SHIFT_RIGHT);
    }

    /**
     * Converts a simple Consumer action to a BiConsumer action.
     *
     * @param action the simple action
     * @return the BiConsumer action
     */
    @NotNull
    public static BiConsumer<Player, ClickType> from(@NotNull Consumer<Player> action) {
        return (player, clickType) -> action.accept(player);
    }

    /**
     * Creates a conditional action that only executes if a condition is met.
     *
     * @param condition the condition to check
     * @param action    the action to execute if condition is true
     * @return the conditional action
     */
    @NotNull
    public static BiConsumer<Player, ClickType> conditional(@NotNull java.util.function.Predicate<Player> condition,
                                                            @NotNull BiConsumer<Player, ClickType> action) {
        return (player, clickType) -> {
            if (condition.test(player)) {
                action.accept(player, clickType);
            }
        };
    }
}