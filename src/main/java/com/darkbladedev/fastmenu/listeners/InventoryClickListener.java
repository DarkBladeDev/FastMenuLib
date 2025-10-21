package com.darkbladedev.fastmenu.listeners;

import com.darkbladedev.fastmenu.api.Menu;
import com.darkbladedev.fastmenu.core.AbstractMenu;
import com.darkbladedev.fastmenu.core.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Handles inventory click events for FastMenu menus.
 * Intercepts clicks and delegates them to the appropriate menu handlers.
 */
public class InventoryClickListener implements Listener {

    private final MenuManager menuManager;

    /**
     * Creates a new InventoryClickListener.
     *
     * @param menuManager the MenuManager instance
     */
    public InventoryClickListener(@NotNull MenuManager menuManager) {
        this.menuManager = Objects.requireNonNull(menuManager, "MenuManager cannot be null");
    }

    /**
     * Handles inventory click events.
     * 
     * @param event the InventoryClickEvent
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        // Only handle player clicks
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        
        // Check if the player has a menu open
        Menu openMenu = menuManager.getOpenMenu(player);
        if (openMenu == null) {
            return;
        }

        // Check if the click was in the menu inventory
        if (clickedInventory == null || !clickedInventory.equals(openMenu.getInventory())) {
            // Allow clicks in player inventory but prevent moving items to menu
            if (event.getAction().name().contains("MOVE_TO_OTHER_INVENTORY")) {
                event.setCancelled(true);
            }
            return;
        }

        // Cancel the event by default to prevent item manipulation
        event.setCancelled(true);

        // Get the clicked slot
        int slot = event.getSlot();
        
        // Validate slot bounds
        if (slot < 0 || slot >= openMenu.getSize()) {
            return;
        }

        // Handle the click if the menu is an AbstractMenu
        if (openMenu instanceof AbstractMenu) {
            AbstractMenu abstractMenu = (AbstractMenu) openMenu;
            
            try {
                boolean handled = abstractMenu.handleClick(player, slot, event.getClick());
                
                // If the click wasn't handled, we still keep it cancelled to prevent item manipulation
                if (!handled) {
                    // Ignored click
                }
            } catch (Exception e) {
                // Log the error and notify the player
                menuManager.getPlugin().getLogger().severe(
                    "Error handling click in menu " + openMenu.getId() + " for player " + player.getName() + ": " + e.getMessage()
                );
                e.printStackTrace();
                
                player.sendMessage("§cAn error occurred while processing your click. Please try again.");
            }
        }
    }

    /**
     * Handles inventory click events with lower priority for additional processing.
     * This can be used for logging or analytics.
     * 
     * @param event the InventoryClickEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClickMonitor(@NotNull InventoryClickEvent event) {
        // Only handle player clicks
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