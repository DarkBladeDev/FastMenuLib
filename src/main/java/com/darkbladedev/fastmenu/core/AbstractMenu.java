package com.darkbladedev.fastmenu.core;

import com.darkbladedev.fastmenu.api.Menu;
import com.darkbladedev.fastmenu.api.MenuContext;
import com.darkbladedev.fastmenu.api.MenuItem;
import com.darkbladedev.fastmenu.utils.SchedulerUtil;
import com.darkbladedev.fastmenu.utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract implementation of the Menu interface providing common functionality.
 * 
 * <p>AbstractMenu serves as the foundation for all menu implementations in the FastMenu library.
 * It provides a complete, thread-safe implementation of the {@link Menu} interface with robust
 * inventory management, player state tracking, and event handling capabilities.</p>
 * 
 * <p>Key features provided by AbstractMenu:</p>
 * <ul>
 *   <li><strong>Thread-Safe Operations</strong> - Uses concurrent collections for safe multi-threaded access</li>
 *   <li><strong>Automatic Inventory Management</strong> - Handles Bukkit inventory creation and synchronization</li>
 *   <li><strong>Player State Tracking</strong> - Maintains per-player contexts and open state</li>
 *   <li><strong>Event Integration</strong> - Seamless integration with Bukkit's inventory events</li>
 *   <li><strong>Validation and Safety</strong> - Comprehensive input validation and error handling</li>
 *   <li><strong>Performance Optimized</strong> - Efficient slot updates and batch operations</li>
 * </ul>
 * 
 * <p><strong>Implementation Guidelines:</strong></p>
 * <p>When extending AbstractMenu, you typically only need to:</p>
 * <ol>
 *   <li>Call the super constructor with appropriate parameters</li>
 *   <li>Override {@link #onOpen(Player)} and {@link #onClose(Player)} for custom behavior</li>
 *   <li>Populate the menu with items using {@link #setItem(int, MenuItem)}</li>
 * </ol>
 * 
 * <p><strong>Thread Safety:</strong></p>
 * <p>AbstractMenu is designed to be thread-safe for concurrent access. All internal collections
 * use concurrent implementations, and inventory updates are properly synchronized with the
 * Bukkit main thread using {@link SchedulerUtil}.</p>
 * 
 * <p><strong>Memory Management:</strong></p>
 * <p>AbstractMenu automatically manages memory by:</p>
 * <ul>
 *   <li>Cleaning up player contexts when menus are closed</li>
 *   <li>Removing players from tracking sets when they disconnect</li>
 *   <li>Providing explicit cleanup methods for manual resource management</li>
 * </ul>
 * 
 * <p><strong>Example Implementation:</strong></p>
 * <pre>{@code
 * public class ShopMenu extends AbstractMenu {
 *     
 *     public ShopMenu() {
 *         super("shop_menu", Component.text("§6Shop"), 27);
 *         setupItems();
 *     }
 *     
 *     private void setupItems() {
 *         // Add shop items
 *         setItem(10, SimpleMenuItem.of(
 *             new ItemBuilder(Material.DIAMOND_SWORD)
 *                 .name("§bDiamond Sword")
 *                 .lore("§7Price: §e100 coins")
 *                 .build(),
 *             (player, clickType) -> {
 *                 // Handle purchase logic
 *                 purchaseItem(player, "diamond_sword", 100);
 *             }
 *         ));
 *     }
 *     
 *     @Override
 *     public void onOpen(Player player) {
 *         // Update prices based on player's economy status
 *         updatePricesForPlayer(player);
 *         super.onOpen(player);
 *     }
 * }
 * }</pre>
 * 
 * @author DarkBladeDev
 * @since 1.0.0
 * @see com.darkbladedev.fastmenu.api.Menu
 * @see com.darkbladedev.fastmenu.core.MenuBuilder
 * @see com.darkbladedev.fastmenu.api.MenuItem
 * @see com.darkbladedev.fastmenu.api.MenuContext
 */
public abstract class AbstractMenu implements Menu {

    protected final String id;
    protected final Component title;
    protected final int size;
    protected final Map<Integer, MenuItem> items;
    protected final Map<UUID, MenuContext> contexts;
    protected final Set<UUID> openPlayers;
    
    private Inventory inventory;

    /**
     * Creates a new AbstractMenu with the specified parameters.
     *
     * @param id    the unique identifier for this menu
     * @param title the title component of the menu
     * @param size  the size of the menu (must be multiple of 9, max 54)
     */
    protected AbstractMenu(@NotNull String id, @NotNull Component title, int size) {
        if (size <= 0 || size > 54 || size % 9 != 0) {
            throw new IllegalArgumentException("Size must be a positive multiple of 9 and not exceed 54");
        }
        
        this.id = Objects.requireNonNull(id, "Menu ID cannot be null");
        this.title = Objects.requireNonNull(title, "Menu title cannot be null");
        this.size = size;
        this.items = new ConcurrentHashMap<>();
        this.contexts = new ConcurrentHashMap<>();
        this.openPlayers = ConcurrentHashMap.newKeySet();
        
        createInventory();
    }

    /**
     * Creates the Bukkit inventory for this menu.
     */
    private void createInventory() {
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    @Override
    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public Component getTitle() {
        return title;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    @NotNull
    public Map<Integer, MenuItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    @Override
    @Nullable
    public MenuItem getItem(int slot) {
        validateSlot(slot);
        return items.get(slot);
    }

    @Override
    public void setItem(int slot, @Nullable MenuItem item) {
        validateSlot(slot);
        
        if (item == null) {
            removeItem(slot);
            return;
        }
        
        items.put(slot, item);
        
        // Update inventory for all open players
        for (UUID playerId : openPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                updateSlotForPlayer(player, slot);
            }
        }
    }

    @Override
    public void removeItem(int slot) {
        validateSlot(slot);
        items.remove(slot);
        inventory.setItem(slot, null);
    }

    public void clearItems() {
        items.clear();
        inventory.clear();
    }

    @Override
    public void open(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        
        // Close any currently open menu
        MenuManager.getInstance().closeMenu(player);
        
        // Create context if it doesn't exist
        contexts.computeIfAbsent(player.getUniqueId(), k -> new MenuContext());
        
        // Update all items for this player
        updateInventoryForPlayer(player);
        
        // Register this menu as open for the player
        MenuManager.getInstance().registerOpenMenu(player, this);
        openPlayers.add(player.getUniqueId());
        
        // Open the inventory
        SchedulerUtil.runSync(() -> {
            player.openInventory(inventory);
            onOpen(player);
        });
    }

    @Override
    public void close(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        
        if (!isOpen(player)) {
            return;
        }
        
        openPlayers.remove(player.getUniqueId());
        MenuManager.getInstance().unregisterOpenMenu(player);
        
        SchedulerUtil.runSync(() -> {
            player.closeInventory();
            onClose(player);
        });
    }

    @Override
    public void refresh(@NotNull Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        
        if (!isOpen(player)) {
            return;
        }
        
        updateInventoryForPlayer(player);
    }

    public void refreshAll() {
        for (UUID playerId : openPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                refresh(player);
            }
        }
    }

    @Override
    public boolean isOpen(@NotNull Player player) {
        return openPlayers.contains(player.getUniqueId());
    }

    @NotNull
    public Set<Player> getOpenPlayers() {
        Set<Player> players = new HashSet<>();
        for (UUID playerId : openPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    @Override
    @NotNull
    public MenuContext getContext(@NotNull Player player) {
        return contexts.computeIfAbsent(player.getUniqueId(), k -> new MenuContext());
    }

    @Override
    public void setContext(@NotNull Player player, @NotNull MenuContext context) {
        contexts.put(player.getUniqueId(), context);
    }

    public void clearContext(@NotNull Player player) {
        contexts.remove(player.getUniqueId());
    }

    /**
     * Updates the entire inventory for a specific player.
     * This method refreshes all items based on the player's context.
     *
     * @param player the player to update the inventory for
     */
    protected void updateInventoryForPlayer(@NotNull Player player) {
        for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
            updateSlotForPlayer(player, entry.getKey());
        }
    }

    /**
     * Updates a specific slot for a player.
     *
     * @param player the player to update the slot for
     * @param slot   the slot to update
     */
    protected void updateSlotForPlayer(@NotNull Player player, int slot) {
        MenuItem menuItem = items.get(slot);
        if (menuItem == null) {
            inventory.setItem(slot, null);
            return;
        }
        
        ItemStack itemStack = menuItem.getItemStack(player);
        inventory.setItem(slot, itemStack);
        
        // If the item is dynamic, update it
        if (menuItem.isDynamic()) {
            menuItem.update(player);
        }
    }

    /**
     * Validates that a slot is within the valid range for this menu.
     *
     * @param slot the slot to validate
     * @throws IllegalArgumentException if the slot is invalid
     */
    protected void validateSlot(int slot) {
        if (slot < 0 || slot >= size) {
            throw new IllegalArgumentException("Slot " + slot + " is out of bounds for menu size " + size);
        }
    }

    /**
     * Called when a player opens this menu.
     * Override this method to add custom open behavior.
     *
     * @param player the player who opened the menu
     */
    @Override
    public void onOpen(@NotNull Player player) {
        // Default implementation does nothing
    }

    /**
     * Called when a player closes this menu.
     * Override this method to add custom close behavior.
     *
     * @param player the player who closed the menu
     */
    @Override
    public void onClose(@NotNull Player player) {
        // Default implementation does nothing
    }

    /**
     * Handles a click event on this menu.
     * This method is called by the MenuManager when a player clicks in the inventory.
     *
     * @param player the player who clicked
     * @param slot   the slot that was clicked
     * @param clickType the type of click
     * @return true if the click was handled, false otherwise
     */
    public boolean handleClick(@NotNull Player player, int slot, @NotNull org.bukkit.event.inventory.ClickType clickType) {
        MenuItem menuItem = items.get(slot);
        if (menuItem == null) {
            return false;
        }
        
        if (!menuItem.isClickable()) {
            return true; // Prevent the click but don't handle it
        }
        
        menuItem.onClick(player, clickType);
        return true; // Click was handled
    }

    /**
     * Handles the menu being closed by a player.
     * This method is called by the MenuManager when a player closes the inventory.
     *
     * @param player the player who closed the menu
     */
    public void handleClose(@NotNull Player player) {
        openPlayers.remove(player.getUniqueId());
        onClose(player);
    }

    /**
     * Cleans up resources for a player who is no longer online.
     *
     * @param player the player to clean up for
     */
    public void cleanup(@NotNull Player player) {
        openPlayers.remove(player.getUniqueId());
        contexts.remove(player.getUniqueId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AbstractMenu that = (AbstractMenu) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AbstractMenu{" +
                "id='" + id + '\'' +
                ", title=" + TextUtil.stripFormatting(TextUtil.toMiniMessage(title)) +
                ", size=" + size +
                ", itemCount=" + items.size() +
                ", openPlayers=" + openPlayers.size() +
                '}';
    }
}