package com.darkbladedev.fastmenu.examples;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Complete example plugin demonstrating the FastMenu library integration and usage.
 * 
 * <p>This plugin serves as a comprehensive working example of how to properly integrate
 * and utilize the FastMenu library within a Minecraft plugin. It demonstrates best
 * practices for plugin initialization, menu system setup, command registration,
 * and proper resource management throughout the plugin lifecycle.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><strong>Plugin Integration:</strong> Proper initialization and shutdown procedures</li>
 *   <li><strong>Menu System Setup:</strong> Complete menu system initialization</li>
 *   <li><strong>Command Registration:</strong> Automatic command registration and handling</li>
 *   <li><strong>Error Handling:</strong> Comprehensive error handling and logging</li>
 *   <li><strong>Resource Management:</strong> Proper cleanup and resource disposal</li>
 *   <li><strong>Example Menus:</strong> Multiple menu examples showcasing different features</li>
 * </ul>
 * 
 * <h3>Plugin Setup:</h3>
 * <p>To use this example plugin in your server:</p>
 * <ol>
 *   <li>Build the plugin using Gradle: <code>./gradlew build</code></li>
 *   <li>Copy the generated JAR file to your server's plugins folder</li>
 *   <li>Start or restart your server</li>
 *   <li>Use <code>/fastmenu</code> command to access the example menus</li>
 * </ol>
 * 
 * <h3>plugin.yml Configuration:</h3>
 * <p>Ensure your plugin.yml includes the following command definition:</p>
 * <pre>{@code
 * commands:
 *   fastmenu:
 *     description: Opens the FastMenu example menus
 *     usage: /fastmenu [main|shop|profile|settings]
 *     permission: fastmenu.use
 * }</pre>
 * 
 * <h3>Dependencies:</h3>
 * <p>This plugin requires the following dependencies:</p>
 * <ul>
 *   <li><strong>Spigot/Paper API:</strong> 1.20+ (for modern Minecraft features)</li>
 *   <li><strong>Java:</strong> 21+ (for optimal performance and modern language features)</li>
 *   <li><strong>FastMenu Library:</strong> All core components included</li>
 * </ul>
 * 
 * <h3>Usage Examples:</h3>
 * <p>Players can interact with the plugin using these commands:</p>
 * <ul>
 *   <li><code>/fastmenu</code> - Opens the main menu hub</li>
 *   <li><code>/fastmenu main</code> - Directly opens the main menu</li>
 *   <li><code>/fastmenu shop</code> - Opens the shop menu example</li>
 *   <li><code>/fastmenu profile</code> - Opens the player profile menu</li>
 *   <li><code>/fastmenu settings</code> - Opens the settings configuration menu</li>
 * </ul>
 * 
 * <h3>Development Notes:</h3>
 * <p>This example demonstrates several important development practices:</p>
 * <ul>
 *   <li><strong>Initialization Order:</strong> Proper sequence of component initialization</li>
 *   <li><strong>Exception Handling:</strong> Graceful error handling with detailed logging</li>
 *   <li><strong>Plugin Lifecycle:</strong> Proper onEnable() and onDisable() implementation</li>
 *   <li><strong>Resource Cleanup:</strong> Automatic cleanup of menu resources</li>
 *   <li><strong>Logging:</strong> Comprehensive logging for debugging and monitoring</li>
 * </ul>
 * 
 * <h3>Customization:</h3>
 * <p>Developers can extend this example by:</p>
 * <ul>
 *   <li>Adding new menu types and configurations</li>
 *   <li>Implementing database integration for persistent data</li>
 *   <li>Adding permission-based menu access</li>
 *   <li>Integrating with economy plugins</li>
 *   <li>Creating custom menu animations and effects</li>
 * </ul>
 * 
 * <h3>Performance Considerations:</h3>
 * <p>The plugin is optimized for production use with:</p>
 * <ul>
 *   <li>Efficient menu caching and reuse</li>
 *   <li>Minimal memory footprint</li>
 *   <li>Asynchronous task handling where appropriate</li>
 *   <li>Proper event listener management</li>
 * </ul>
 * 
 * @author DarkBladeDev
 * @since 1.0.0
 * @version 1.0.0
 * @see ExampleMenu
 * @see MenuManager
 * @see MenuBuilder
 * @see JavaPlugin
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