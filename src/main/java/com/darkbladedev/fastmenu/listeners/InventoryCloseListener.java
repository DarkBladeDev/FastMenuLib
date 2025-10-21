package com.darkbladedev.fastmenu.listeners;

import com.darkbladedev.fastmenu.api.Menu;
import com.darkbladedev.fastmenu.core.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Handles inventory close events for FastMenu menus.
 * Manages menu cleanup and executes onClose actions.
 */
public class InventoryCloseListener implements Listener {

    private final MenuManager menuManager;

    /**
     * Creates a new InventoryCloseListener.
     *
     * @param menuManager the MenuManager instance
     */
    public InventoryCloseListener(@NotNull MenuManager menuManager) {
        this.menuManager = Objects.requireNonNull(menuManager, "MenuManager cannot be null");
    }

    /**
     * Handles inventory close events.
     * 
     * @param event the InventoryCloseEvent
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        // Only handle player closes
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory();
        
        // Check if the player has a menu open
        Menu openMenu = menuManager.getOpenMenu(player);
        if (openMenu == null) {
            return;
        }

        // Check if the closed inventory matches the open menu
        if (!closedInventory.equals(openMenu.getInventory())) {
            return;
        }

        try {
            // Execute the menu's onClose action
            openMenu.onClose(player);
            
            // Remove the menu from the manager's tracking
            menuManager.untrackMenu(player);
            
        } catch (Exception e) {
            // Log the error
            menuManager.getPlugin().getLogger().severe(
                "Error executing onClose for menu " + openMenu.getId() + " and player " + player.getName() + ": " + e.getMessage()
            );
            e.printStackTrace();
            
            // Still remove from tracking even if onClose failed
            menuManager.untrackMenu(player);
        }
    }

    /**
     * Handles inventory close events with monitor priority for additional processing.
     * This can be used for logging or analytics after the menu has been processed.
     * 
     * @param event the InventoryCloseEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryCloseMonitor(@NotNull InventoryCloseEvent event) {
        // Only handle player closes
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
    }
}