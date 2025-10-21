package com.darkbladedev.fastmenu.listeners;

import com.darkbladedev.fastmenu.api.Menu;
import com.darkbladedev.fastmenu.core.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * Handles inventory drag events for FastMenu menus.
 * Prevents item dragging in menu inventories to maintain menu integrity.
 */
public class InventoryDragListener implements Listener {

    private final MenuManager menuManager;

    /**
     * Creates a new InventoryDragListener.
     *
     * @param menuManager the MenuManager instance
     */
    public InventoryDragListener(@NotNull MenuManager menuManager) {
        this.menuManager = Objects.requireNonNull(menuManager, "MenuManager cannot be null");
    }

    /**
     * Handles inventory drag events.
     * 
     * @param event the InventoryDragEvent
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        // Only handle player drags
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        
        // Check if the player has a menu open
        Menu openMenu = menuManager.getOpenMenu(player);
        if (openMenu == null) {
            return;
        }

        Inventory menuInventory = openMenu.getInventory();
        Inventory draggedInventory = event.getInventory();
        
        // Check if the drag involves the menu inventory
        boolean dragInvolveMenu = false;
        
        // Check if the main inventory is the menu
        if (draggedInventory.equals(menuInventory)) {
            dragInvolveMenu = true;
        } else {
            // Check if any of the dragged slots are in the menu inventory
            Set<Integer> rawSlots = event.getRawSlots();
            int menuSize = menuInventory.getSize();
            
            for (Integer rawSlot : rawSlots) {
                if (rawSlot < menuSize) {
                    dragInvolveMenu = true;
                    break;
                }
            }
        }

        // Cancel the drag if it involves the menu inventory
        if (dragInvolveMenu) {
            event.setCancelled(true);
            
            try {
                // Optionally, you could play a sound or send a message to indicate the action was blocked
                // For now, we'll silently cancel to maintain a clean user experience
                
            } catch (Exception e) {
                // Log any errors that might occur
                menuManager.getPlugin().getLogger().warning(
                    "Error handling drag event for menu " + openMenu.getId() + " and player " + player.getName() + ": " + e.getMessage()
                );
            }
        }
    }

    /**
     * Handles inventory drag events with monitor priority for additional processing.
     * This can be used for logging or analytics.
     * 
     * @param event the InventoryDragEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDragMonitor(@NotNull InventoryDragEvent event) {
        // Only handle player drags
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Menu openMenu = menuManager.getOpenMenu(player);
        
        if (openMenu == null) {
            return;
        }
    }
}