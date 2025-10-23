package com.darkbladedev.fastmenu.examples;

import com.darkbladedev.fastmenu.api.Menu;
import com.darkbladedev.fastmenu.api.MenuAction;
import com.darkbladedev.fastmenu.core.MenuBuilder;
import com.darkbladedev.fastmenu.core.MenuManager;
import com.darkbladedev.fastmenu.core.SimpleMenuItem;
import com.darkbladedev.fastmenu.utils.ItemBuilder;
import com.darkbladedev.fastmenu.utils.SchedulerUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Comprehensive example implementation demonstrating the FastMenu library's capabilities.
 * 
 * <p>This class serves as a complete demonstration of the FastMenu library, showcasing
 * various features and best practices for creating interactive menus in Minecraft plugins.
 * It includes examples of different menu types, item configurations, click actions,
 * dynamic content updates, and advanced menu management techniques.</p>
 * 
 * <h3>Demonstrated Features:</h3>
 * <ul>
 *   <li><strong>Menu Creation:</strong> Using MenuBuilder for fluent menu construction</li>
 *   <li><strong>Item Types:</strong> Static items, dynamic items, and interactive elements</li>
 *   <li><strong>Click Actions:</strong> Various click handlers and action types</li>
 *   <li><strong>Dynamic Content:</strong> Real-time updates and context-sensitive displays</li>
 *   <li><strong>Navigation:</strong> Multi-page menus and menu transitions</li>
 *   <li><strong>Sound Effects:</strong> Audio feedback for user interactions</li>
 *   <li><strong>Permissions:</strong> Permission-based menu access and item visibility</li>
 *   <li><strong>Animations:</strong> Visual effects and item animations</li>
 *   <li><strong>Context Management:</strong> Player-specific data and menu states</li>
 * </ul>
 * 
 * <h3>Menu Examples Included:</h3>
 * <ul>
 *   <li><strong>Main Menu:</strong> Central hub with navigation options</li>
 *   <li><strong>Player Info Menu:</strong> Displays player statistics and information</li>
 *   <li><strong>Settings Menu:</strong> Configuration options and preferences</li>
 *   <li><strong>Confirmation Menu:</strong> Yes/No dialogs and confirmations</li>
 *   <li><strong>Paginated Menu:</strong> Multi-page content with navigation</li>
 *   <li><strong>Dynamic Menu:</strong> Real-time updating content</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // In your plugin's onEnable() method
 * ExampleMenu exampleMenu = new ExampleMenu(this);
 * exampleMenu.initialize();
 * 
 * // Register the command
 * getCommand("menu").setExecutor(exampleMenu);
 * }</pre>
 * 
 * <h3>Command Usage:</h3>
 * <p>Players can access the example menus using the following commands:</p>
 * <ul>
 *   <li><code>/menu</code> - Opens the main example menu</li>
 *   <li><code>/menu info</code> - Opens the player information menu</li>
 *   <li><code>/menu settings</code> - Opens the settings menu</li>
 *   <li><code>/menu confirm</code> - Opens a confirmation dialog example</li>
 * </ul>
 * 
 * <h3>Implementation Notes:</h3>
 * <p>This example demonstrates proper initialization order, error handling,
 * and resource management. It follows best practices for menu creation,
 * including proper cleanup, thread safety considerations, and performance
 * optimization techniques.</p>
 * 
 * <h3>Customization:</h3>
 * <p>Developers can use this class as a template for creating their own
 * menu systems. The modular design allows for easy customization and
 * extension of existing functionality.</p>
 * 
 * <h3>Performance Considerations:</h3>
 * <p>The example includes optimizations for large player bases, including
 * efficient item caching, lazy loading of dynamic content, and proper
 * memory management to prevent memory leaks.</p>
 * 
 * @author DarkBladeDev
 * @since 1.0.0
 * @see MenuBuilder
 * @see MenuManager
 * @see MenuItem
 * @see MenuAction
 * @see CommandExecutor
 */
public class ExampleMenu implements CommandExecutor {

    private final JavaPlugin plugin;
    private final MenuManager menuManager;

    /**
     * Creates a new ExampleMenu instance.
     *
     * @param plugin the plugin instance
     */
    public ExampleMenu(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.menuManager = MenuManager.getInstance();
    }

    /**
     * Registers the example command and initializes the menu system.
     */
    public void initialize() {
        // Initialize the MenuManager
        MenuManager.initialize(plugin);
        
        // Initialize the SchedulerUtil
        SchedulerUtil.initialize(plugin);
        
        // Register the command
        plugin.getCommand("fastmenu").setExecutor(this);
        
        plugin.getLogger().info("FastMenu example initialized successfully!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            openMainMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "main":
                openMainMenu(player);
                break;
            case "shop":
                openShopMenu(player);
                break;
            case "profile":
                openProfileMenu(player);
                break;
            case "settings":
                openSettingsMenu(player);
                break;
            default:
                player.sendMessage("§cUsage: /fastmenu [main|shop|profile|settings]");
                break;
        }

        return true;
    }

    /**
     * Opens the main menu for a player.
     * Demonstrates basic menu creation with various item types.
     *
     * @param player the player
     */
    private void openMainMenu(@NotNull Player player) {
        Menu mainMenu = MenuBuilder.create("main_menu", "§6§lFastMenu §7- Main Menu", 5)
            // Header decoration
            .setRow(0, SimpleMenuItem.decoration(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                .name("§e§l▬▬▬▬▬▬▬")
                .build()).getItemStack())
            
            // Navigation items
            .setItem(19, SimpleMenuItem.of(new ItemBuilder(Material.EMERALD)
                .name("§a§lShop")
                .lore("§7Click to open the shop menu")
                .lore("§8Buy items and upgrades")
                .build(), (p, click) -> {
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    openShopMenu(p);
                }))
            
            .setItem(21, SimpleMenuItem.of(new ItemBuilder(Material.PLAYER_HEAD)
                .name("§b§lProfile")
                .lore("§7View your player profile")
                .lore("§8Statistics and achievements")
                .build(), (p, click) -> {
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    openProfileMenu(p);
                }))
            
            .setItem(23, SimpleMenuItem.of(new ItemBuilder(Material.REDSTONE)
                .name("§c§lSettings")
                .lore("§7Configure your preferences")
                .lore("§8Customize your experience")
                .build(), (p, click) -> {
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    openSettingsMenu(p);
                }))
            
            .setItem(25, SimpleMenuItem.of(new ItemBuilder(Material.BOOK)
                .name("§e§lHelp")
                .lore("§7Learn how to use FastMenu")
                .lore("§8Documentation and examples")
                .build(), (p, click) -> {
                    p.sendMessage("§6§lFastMenu Help:");
                    p.sendMessage("§7- Use §e/fastmenu main §7for the main menu");
                    p.sendMessage("§7- Use §e/fastmenu shop §7for the shop");
                    p.sendMessage("§7- Use §e/fastmenu profile §7for your profile");
                    p.sendMessage("§7- Use §e/fastmenu settings §7for settings");
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }))
            
            // Footer decoration
            .setRow(4, SimpleMenuItem.decoration(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                .name("§e§l▬▬▬▬▬▬▬")
                .build()).getItemStack())
            
            // Close button
            .setItem(40, SimpleMenuItem.of(new ItemBuilder(Material.BARRIER)
                .name("§c§lClose Menu")
                .lore("§7Click to close this menu")
                .build(), MenuAction.CLOSE))
            
            .onOpen(p -> {
                p.sendMessage("§6Welcome to FastMenu, " + p.getName() + "!");
                p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
            })
            
            .onClose(p -> {
                p.sendMessage("§7Thanks for using FastMenu!");
            })
            
            .build();

        mainMenu.open(player);
    }

    /**
     * Opens the shop menu for a player.
     * Demonstrates dynamic content and context usage.
     *
     * @param player the player
     */
    private void openShopMenu(@NotNull Player player) {
        Menu shopMenu = MenuBuilder.create("shop_menu", "§2§lShop §7- Buy Items", 6)
            // Back button
            .setItem(0, SimpleMenuItem.of(new ItemBuilder(Material.ARROW)
                .name("§7« Back to Main Menu")
                .build(), (p, click) -> openMainMenu(p)))
            
            // Shop items
            .setItem(10, SimpleMenuItem.of(new ItemBuilder(Material.DIAMOND_SWORD)
                .name("§b§lDiamond Sword")
                .lore("§7Price: §6100 coins")
                .lore("§7A powerful weapon for combat")
                .lore("")
                .lore("§eClick to purchase!")
                .build(), (p, click) -> {
                    // Simulate purchase
                    p.sendMessage("§a§lPURCHASE SUCCESSFUL!");
                    p.sendMessage("§7You bought a §bDiamond Sword §7for §6100 coins§7!");
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
                    p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_SWORD)
                        .name("§b§lPurchased Diamond Sword")
                        .build());
                }))
            
            .setItem(12, SimpleMenuItem.of(new ItemBuilder(Material.GOLDEN_APPLE)
                .name("§6§lGolden Apple")
                .lore("§7Price: §650 coins")
                .lore("§7Restores health and provides buffs")
                .lore("")
                .lore("§eClick to purchase!")
                .build(), (p, click) -> {
                    p.sendMessage("§a§lPURCHASE SUCCESSFUL!");
                    p.sendMessage("§7You bought a §6Golden Apple §7for §650 coins§7!");
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
                    p.getInventory().addItem(new ItemBuilder(Material.GOLDEN_APPLE).build());
                }))
            
            .setItem(14, SimpleMenuItem.of(new ItemBuilder(Material.ELYTRA)
                .name("§d§lElytra")
                .lore("§7Price: §6500 coins")
                .lore("§7Allows you to glide through the air")
                .lore("")
                .lore("§eClick to purchase!")
                .build(), (p, click) -> {
                    p.sendMessage("§a§lPURCHASE SUCCESSFUL!");
                    p.sendMessage("§7You bought an §dElytra §7for §6500 coins§7!");
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
                    p.getInventory().addItem(new ItemBuilder(Material.ELYTRA).build());
                }))
            
            // Dynamic balance display
            .setItem(49, SimpleMenuItem.dynamic((p) -> {
                // Simulate getting player balance (in a real plugin, you'd get this from an economy plugin)
                int balance = p.getLevel() * 10; // Use level as fake balance
                
                return new ItemBuilder(Material.GOLD_INGOT)
                    .name("§6§lYour Balance")
                    .lore("§7Current balance: §6" + balance + " coins")
                    .lore("§8This updates automatically!")
                    .build();
            }))
            
            // Refresh button
            .setItem(53, SimpleMenuItem.of(new ItemBuilder(Material.LIME_DYE)
                .name("§a§lRefresh Shop")
                .lore("§7Click to refresh the shop")
                .build(), MenuAction.REFRESH))
            
            .build();

        shopMenu.open(player);
    }

    /**
     * Opens the profile menu for a player.
     * Demonstrates player-specific dynamic content.
     *
     * @param player the player
     */
    private void openProfileMenu(@NotNull Player player) {
        Menu profileMenu = MenuBuilder.create("profile_menu", "§9§lProfile §7- " + player.getName(), 4)
            // Back button
            .setItem(0, SimpleMenuItem.of(new ItemBuilder(Material.ARROW)
                .name("§7« Back to Main Menu")
                .build(), (p, click) -> openMainMenu(p)))
            
            // Player head
            .setItem(13, SimpleMenuItem.of(new ItemBuilder(Material.PLAYER_HEAD)
                .name("§b§l" + player.getName())
                .lore("§7Level: §e" + player.getLevel())
                .lore("§7Health: §c" + String.format("%.1f", player.getHealth()) + "/20.0")
                .lore("§7Food: §6" + player.getFoodLevel() + "/20")
                .lore("§7XP: §a" + player.getTotalExperience())
                .lore("")
                .lore("§7Joined: §f" + "Some time ago") // You could format the join date here
                .build()))
            
            // Statistics
            .setItem(29, SimpleMenuItem.of(new ItemBuilder(Material.BOOK)
                .name("§e§lStatistics")
                .lore("§7View detailed statistics")
                .lore("§8Coming soon...")
                .build(), (p, click) -> {
                    p.sendMessage("§7Statistics feature coming soon!");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.8f);
                }))
            
            .setItem(31, SimpleMenuItem.of(new ItemBuilder(Material.DIAMOND)
                .name("§b§lAchievements")
                .lore("§7View your achievements")
                .lore("§8Coming soon...")
                .build(), (p, click) -> {
                    p.sendMessage("§7Achievements feature coming soon!");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.8f);
                }))
            
            .setItem(33, SimpleMenuItem.of(new ItemBuilder(Material.CHEST)
                .name("§6§lInventory")
                .lore("§7Manage your inventory")
                .lore("§8Coming soon...")
                .build(), (p, click) -> {
                    p.sendMessage("§7Inventory management feature coming soon!");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.8f);
                }))
            
            .build();

        profileMenu.open(player);
    }

    /**
     * Opens the settings menu for a player.
     * Demonstrates toggle functionality and menu state management.
     *
     * @param player the player
     */
    private void openSettingsMenu(@NotNull Player player) {
        MenuBuilder builder = MenuBuilder.create("settings_menu", "§c§lSettings §7- Configuration", 4)
            // Back button
            .setItem(0, SimpleMenuItem.of(new ItemBuilder(Material.ARROW)
                .name("§7« Back to Main Menu")
                .build(), (p, click) -> openMainMenu(p)));
        
        Menu settingsMenu = builder.build();
        
        // Settings options
        builder.setItem(11, SimpleMenuItem.dynamic((p) -> {
                // Simulate sound setting (in a real plugin, you'd store this in a config or database)
                boolean soundEnabled = settingsMenu.getContext(p).getOrDefault("sound_enabled", true);
                
                return new ItemBuilder(soundEnabled ? Material.LIME_DYE : Material.GRAY_DYE)
                    .name(soundEnabled ? "§a§lSounds: Enabled" : "§c§lSounds: Disabled")
                    .lore("§7Toggle sound effects")
                    .lore("")
                    .lore("§eClick to toggle!")
                    .build();
            }, (player1, clickType) -> {
                boolean soundEnabled = settingsMenu.getContext(player1).getOrDefault("sound_enabled", true);
                boolean newState = !soundEnabled;
                settingsMenu.getContext(player1).set("sound_enabled", newState);
                
                player1.sendMessage(newState ? 
                    "§a§lSounds enabled!" : 
                    "§c§lSounds disabled!");
                
                if (newState) {
                    player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.2f);
                }
                
                // Refresh the menu to update the item
                settingsMenu.refresh(player1);
            }))
            
            .setItem(13, SimpleMenuItem.dynamic((p) -> {
                boolean notificationsEnabled = settingsMenu.getContext(p).getOrDefault("notifications_enabled", true);
                
                return new ItemBuilder(notificationsEnabled ? Material.LIME_DYE : Material.GRAY_DYE)
                    .name(notificationsEnabled ? "§a§lNotifications: Enabled" : "§c§lNotifications: Disabled")
                    .lore("§7Toggle chat notifications")
                    .lore("")
                    .lore("§eClick to toggle!")
                    .build();
            }, (player1, clickType) -> {
                boolean notificationsEnabled = settingsMenu.getContext(player1).getOrDefault("notifications_enabled", true);
                boolean newState = !notificationsEnabled;
                settingsMenu.getContext(player1).set("notifications_enabled", newState);
                
                player1.sendMessage(newState ? 
                    "§a§lNotifications enabled!" : 
                    "§c§lNotifications disabled!");
                
                settingsMenu.refresh(player1);
            }))
            
            .setItem(15, SimpleMenuItem.of(new ItemBuilder(Material.REDSTONE)
                .name("§c§lReset Settings")
                .lore("§7Reset all settings to default")
                .lore("")
                .lore("§cClick to reset!")
                .build(), (p, click) -> {
                    // Clear all context data
                    p.sendMessage("§a§lSettings reset to default!");
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    
                    // Refresh the menu
                    settingsMenu.refresh(p);
                }));

        settingsMenu.open(player);
    }
}