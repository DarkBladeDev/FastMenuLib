package com.darkbladedev.fastmenu.examples;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Example plugin demonstrating FastMenu library usage.
 * This plugin serves as a complete working example of how to integrate
 * and use the FastMenu library in a Minecraft plugin.
 */
public class ExamplePlugin extends JavaPlugin {

    private ExampleMenu exampleMenu;

    @Override
    public void onEnable() {
        getLogger().info("FastMenu Example Plugin is starting...");
        
        try {
            // Initialize the example menu system
            exampleMenu = new ExampleMenu(this);
            exampleMenu.initialize();
            
            getLogger().info("FastMenu Example Plugin has been enabled successfully!");
            getLogger().info("Use /fastmenu to open the example menus.");
            
        } catch (Exception e) {
            getLogger().severe("Failed to enable FastMenu Example Plugin: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("FastMenu Example Plugin is shutting down...");
        
        try {
            // Clean up resources if needed
            // The MenuManager will handle its own cleanup
            
            getLogger().info("FastMenu Example Plugin has been disabled successfully!");
            
        } catch (Exception e) {
            getLogger().warning("Error during plugin shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }
}