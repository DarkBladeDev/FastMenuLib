package com.darkbladedev.fastmenu.core;

import com.darkbladedev.fastmenu.api.Menu;
import com.darkbladedev.fastmenu.listeners.InventoryClickListener;
import com.darkbladedev.fastmenu.listeners.InventoryCloseListener;
import com.darkbladedev.fastmenu.listeners.InventoryDragListener;
import com.darkbladedev.fastmenu.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager for all FastMenu operations.
 * Handles menu registration, player tracking, and event listener management.
 */
public final class MenuManager {

    private static MenuManager instance;
    
    private final Plugin plugin;
    private final Map<UUID, Menu> openMenus;
    private final Map<String, Menu> registeredMenus;
    private final Set<UUID> playersInMenu;
    
    // Event listeners
    private InventoryClickListener clickListener;
    private InventoryCloseListener closeListener;
    private InventoryDragListener dragListener;
    
    private boolean initialized = false;

    /**
     * Private constructor for singleton pattern.
     *
     * @param plugin the plugin instance
     */
    private MenuManager(@NotNull Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        this.openMenus = new ConcurrentHashMap<>();
        this.registeredMenus = new ConcurrentHashMap<>();
        this.playersInMenu = ConcurrentHashMap.newKeySet();
    }

    /**
     * Initializes the MenuManager with the specified plugin.
     * This must be called before using any menu functionality.
     *
     * @param plugin the plugin instance
     * @return the MenuManager instance
     * @throws IllegalStateException if already initialized
     */
    @NotNull
    public static MenuManager initialize(@NotNull Plugin plugin) {
        if (instance != null) {
            throw new IllegalStateException("MenuManager is already initialized");
        }
        
        instance = new MenuManager(plugin);
        instance.setup();
        return instance;
    }

    /**
     * Gets the MenuManager instance.
     *
     * @return the MenuManager instance
     * @throws IllegalStateException if not initialized
     */
    @NotNull
    public static MenuManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MenuManager not initialized. Call initialize() first.");
        }
        return instance;
    }

    /**
     * Sets up the MenuManager by initializing utilities and registering listeners.
     */
    private void setup() {
        if (initialized) {
            return;
        }
        
        // Initialize scheduler utility
        SchedulerUtil.initialize(plugin);
        
        // Register event listeners
        registerListeners();
        
        initialized = true;
        plugin.getLogger().info("FastMenu MenuManager initialized successfully");
    }

    /**
     * Registers all event listeners for menu functionality.
     */
    private void registerListeners() {
        clickListener = new InventoryClickListener(this);
        closeListener = new InventoryCloseListener(this);
        dragListener = new InventoryDragListener(this);
        
        Bukkit.getPluginManager().registerEvents(clickListener, plugin);
        Bukkit.getPluginManager().registerEvents(closeListener, plugin);
        Bukkit.getPluginManager().registerEvents(dragListener, plugin);
    }

    /**
     * Unregisters all event listeners.
     */
    private void unregisterListeners() {
        if (clickListener != null) {
            HandlerList.unregisterAll(clickListener);
        }
        if (closeListener != null) {
            HandlerList.unregisterAll(closeListener);
        }
        if (dragListener != null) {
            HandlerList.unregisterAll(dragListener);
        }
    }

    /**
     * Registers a menu with the manager.
     *
     * @param menu the menu to register
     * @throws IllegalArgumentException if a menu with the same ID is already registered
     */
    public void registerMenu(@NotNull Menu menu) {
        Objects.requireNonNull(menu, "Menu cannot be null");
        
        String menuId = menu.getId();
        if (registeredMenus.containsKey(menuId)) {
            throw new IllegalArgumentException("Menu with ID '" + menuId + "' is already registered");
        }
        
        registeredMenus.put(menuId, menu);
    }

    /**
     * Unregisters a menu from the manager.
     *
     * @param menuId the ID of the menu to unregister
     * @return the unregistered menu, or null if not found
     */
    @Nullable
    public Menu unregisterMenu(@NotNull String menuId) {
        Objects.requireNonNull(menuId, "Menu ID cannot be null");
        
        Menu menu = registeredMenus.remove(menuId);
        if (menu != null) {
            // Close the menu for all players who have it open
            closeMenuForAll(menu);
        }
        
        return menu;
    }

    /**
     * Gets a registered menu by its ID.
     *
     * @param menuId the menu ID
     * @return the menu, or null if not found
     */
    @Nullable
    public Menu getRegisteredMenu(@NotNull String menuId) {
        return registeredMenus.get(menuId);
    }

    /**
     * Gets all registered menus.
     *
     * @return an unmodifiable map of registered menus
     */
    @NotNull
    public Map<String, Menu> getRegisteredMenus() {
        return Collections.unmodifiableMap(registeredMenus);
    }

    /**
     * Registers a menu as open for a player.
     *
     * @param player the player
     * @param menu   the menu
     */
    public void registerOpenMenu(@NotNull Player player, @NotNull Menu menu) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(menu, "Menu cannot be null");
        
        UUID playerId = player.getUniqueId();
        
        // Close any existing menu for this player
        Menu existingMenu = openMenus.get(playerId);
        if (existingMenu != null && !existingMenu.equals(menu)) {
            existingMenu.close(player);
        }
        
        openMenus.put(playerId, menu);
        playersInMenu.add(playerId);
    }

    /**
     * Unregisters a player's open menu.
     *
     * @param player the player
     */
    public void unregisterOpenMenu(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        
        UUID playerId = player.getUniqueId();
        openMenus.remove(playerId);
        playersInMenu.remove(playerId);
    }

    /**
     * Removes tracking for a player's menu without calling close actions.
     * Used internally by listeners when a menu is closed.
     *
     * @param player the player
     */
    public void untrackMenu(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        
        UUID playerId = player.getUniqueId();
        openMenus.remove(playerId);
        playersInMenu.remove(playerId);
    }

    /**
     * Gets the menu that a player currently has open.
     *
     * @param player the player
     * @return the open menu, or null if none
     */
    @Nullable
    public Menu getOpenMenu(@NotNull Player player) {
        return openMenus.get(player.getUniqueId());
    }

    /**
     * Checks if a player has a menu open.
     *
     * @param player the player
     * @return true if the player has a menu open
     */
    public boolean hasMenuOpen(@NotNull Player player) {
        return playersInMenu.contains(player.getUniqueId());
    }

    /**
     * Closes any open menu for a player.
     *
     * @param player the player
     */
    public void closeMenu(@NotNull Player player) {
        Menu menu = getOpenMenu(player);
        if (menu != null) {
            menu.close(player);
        }
    }

    /**
     * Closes a specific menu for all players who have it open.
     *
     * @param menu the menu to close
     */
    public void closeMenuForAll(@NotNull Menu menu) {
        Objects.requireNonNull(menu, "Menu cannot be null");
        
        List<Player> playersToClose = new ArrayList<>();
        
        for (Map.Entry<UUID, Menu> entry : openMenus.entrySet()) {
            if (menu.equals(entry.getValue())) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    playersToClose.add(player);
                }
            }
        }
        
        for (Player player : playersToClose) {
            menu.close(player);
        }
    }

    /**
     * Closes all open menus.
     */
    public void closeAllMenus() {
        List<Player> playersToClose = new ArrayList<>();
        
        for (UUID playerId : playersInMenu) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                playersToClose.add(player);
            }
        }
        
        for (Player player : playersToClose) {
            closeMenu(player);
        }
    }

    /**
     * Refreshes all open menus.
     */
    public void refreshAllMenus() {
        for (Map.Entry<UUID, Menu> entry : openMenus.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                entry.getValue().refresh(player);
            }
        }
    }

    /**
     * Gets all players who currently have menus open.
     *
     * @return a set of players with open menus
     */
    @NotNull
    public Set<Player> getPlayersWithOpenMenus() {
        Set<Player> players = new HashSet<>();
        for (UUID playerId : playersInMenu) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    /**
     * Gets statistics about the menu manager.
     *
     * @return a map containing various statistics
     */
    @NotNull
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("registeredMenus", registeredMenus.size());
        stats.put("openMenus", openMenus.size());
        stats.put("playersInMenu", playersInMenu.size());
        stats.put("initialized", initialized);
        return stats;
    }

    /**
     * Cleans up resources for a player (called when they disconnect).
     *
     * @param player the player to clean up for
     */
    public void cleanupPlayer(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        
        UUID playerId = player.getUniqueId();
        Menu menu = openMenus.remove(playerId);
        playersInMenu.remove(playerId);
        
        // Clean up the menu if it's an AbstractMenu
        if (menu instanceof AbstractMenu) {
            ((AbstractMenu) menu).cleanup(player);
        }
    }

    /**
     * Shuts down the MenuManager and cleans up all resources.
     */
    public void shutdown() {
        if (!initialized) {
            return;
        }
        
        // Close all open menus
        closeAllMenus();
        
        // Clear all data
        openMenus.clear();
        registeredMenus.clear();
        playersInMenu.clear();
        
        // Unregister listeners
        unregisterListeners();
        
        initialized = false;
        instance = null;
        
        plugin.getLogger().info("FastMenu MenuManager shut down successfully");
    }

    /**
     * Gets the plugin instance.
     *
     * @return the plugin instance
     */
    @NotNull
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Checks if the MenuManager is initialized.
     *
     * @return true if initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
}