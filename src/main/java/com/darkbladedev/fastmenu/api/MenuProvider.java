package com.darkbladedev.fastmenu.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Functional interface for providing menus dynamically.
 * This allows for creating menus based on player context or other dynamic factors.
 */
@FunctionalInterface
public interface MenuProvider {

    /**
     * Provides a menu for the specified player.
     *
     * @param player the player requesting the menu
     * @return the menu instance, or null if no menu should be provided
     */
    @Nullable
    Menu provide(@NotNull Player player);

    /**
     * Creates a MenuProvider that always returns the same menu.
     *
     * @param menu the menu to provide
     * @return the menu provider
     */
    @NotNull
    static MenuProvider of(@NotNull Menu menu) {
        return player -> menu;
    }

    /**
     * Creates a MenuProvider that creates a new menu instance each time.
     *
     * @param menuFactory the factory function to create menus
     * @return the menu provider
     */
    @NotNull
    static MenuProvider factory(@NotNull java.util.function.Function<Player, Menu> menuFactory) {
        return menuFactory::apply;
    }

    /**
     * Creates a conditional MenuProvider that only provides a menu if a condition is met.
     *
     * @param condition the condition to check
     * @param provider  the provider to use if condition is true
     * @return the conditional menu provider
     */
    @NotNull
    static MenuProvider conditional(@NotNull java.util.function.Predicate<Player> condition,
                                   @NotNull MenuProvider provider) {
        return player -> condition.test(player) ? provider.provide(player) : null;
    }

    /**
     * Creates a MenuProvider that tries multiple providers in order until one returns a menu.
     *
     * @param providers the providers to try
     * @return the fallback menu provider
     */
    @NotNull
    @SafeVarargs
    static MenuProvider fallback(@NotNull MenuProvider... providers) {
        return player -> {
            for (MenuProvider provider : providers) {
                Menu menu = provider.provide(player);
                if (menu != null) {
                    return menu;
                }
            }
            return null;
        };
    }
}