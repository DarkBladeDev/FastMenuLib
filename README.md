# FastMenu Library

A modern and fluid library for creating menus (GUIs) in Minecraft Paper/Spigot plugins.

## Features

- **Fluent API**: Clear and readable syntax using builders
- **Automatic Event Handling**: No need to manually create listeners
- **Dynamic Content Support**: Items that automatically update
- **Context System**: Player-specific data handling
- **Adventure API**: Full support for modern text formatting
- **Extensible**: Modular design ready for future features

## Installation

[![](https://jitpack.io/v/DarkBladeDev/FastMenuLib.svg)](https://jitpack.io/#DarkBladeDev/FastMenuLib) [![CodeFactor](https://www.codefactor.io/repository/github/darkbladedev/fastmenulib/badge)](https://www.codefactor.io/repository/github/darkbladedev/fastmenulib)

### As a Dependency (Recommended)

Add FastMenu as a dependency in your `build.gradle`:

```gradle
repositories {
    mavenCentral()
    maven { url = 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.DarkBladeDev:FastMenuLib:VERSION'
}
```

### As a Standalone Plugin

1. Build the project: `./gradlew build`
2. Place the JAR inside your `plugins/` folder
3. Restart the server

## Basic Usage

### Initialization

```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Initialize FastMenu
        MenuManager.initialize(this);
        SchedulerUtil.initialize(this);
    }
}
```

### Creating a Simple Menu

```java
Menu menu = MenuBuilder.create("my_menu", "§6My Menu", 3)
    .setItem(13, new ItemBuilder(Material.DIAMOND)
        .name("§bDiamond")
        .lore("§7Click to receive a diamond!")
        .onClick((player, click) -> {
            player.getInventory().addItem(new ItemStack(Material.DIAMOND));
            player.sendMessage("§aYou received a diamond!");
        })
        .build())
    
    .setItem(22, new ItemBuilder(Material.BARRIER)
        .name("§cClose")
        .onClick(MenuAction.close())
        .build())
    
    .build();

// Open the menu
menu.open(player);
```

### Dynamic Content

```java
Menu menu = MenuBuilder.create("shop", "§2Shop", 4)
    .setItem(13, SimpleMenuItem.dynamic("balance", (player, context) -> {
        int balance = getPlayerBalance(player); // Your method to get balance
        
        return new ItemBuilder(Material.GOLD_INGOT)
            .name("§6Your Balance")
            .lore("§7Current balance: §6" + balance + " coins")
            .build();
    }))
    .build();
```

### Context System

```java
Menu menu = MenuBuilder.create("settings", "§cSettings", 3)
    .setItem(11, SimpleMenuItem.dynamic("sounds", (player, context) -> {
        boolean soundEnabled = context.getData("sound_enabled", Boolean.class, true);
        
        return new ItemBuilder(soundEnabled ? Material.LIME_DYE : Material.GRAY_DYE)
            .name(soundEnabled ? "§aSounds: Enabled" : "§cSounds: Disabled")
            .onClick((p, click) -> {
                boolean newState = !soundEnabled;
                context.setData("sound_enabled", newState);
                menu.refresh(p); // Refresh the menu
            })
            .build();
    }))
    .build();
```

## Advanced Examples

Check the `ExampleMenu` class in the [`examples`](https://github.com/DarkBladeDev/FastMenuLib/tree/c7ad3d9d7badce4945cc6e29df8b2c0badffea4e/src/main/java/com/darkbladedev/fastmenu/examples) package for complete implementations of:

- Main menu with navigation
- Shop with purchase system
- Player profile with dynamic statistics
- Settings menu with persistent toggles

## API Reference

### MenuBuilder

- `create(id, title, rows)` – Create a new builder
- `setItem(slot, item)` – Set an item in a specific slot
- `fillRow(row, item)` – Fill a whole row
- `fillBorder(item)` – Fill the menu border
- `onOpen(action)` – Action triggered when opening the menu
- `onClose(action)` – Action triggered when closing the menu
- `build()` – Build the final menu

### ItemBuilder

- `name(text)` – Set item name (supports MiniMessage)
- `lore(lines...)` – Add lore lines
- `onClick(action)` – Define click action
- `amount(count)` – Set item amount
- `enchant(enchantment, level)` – Add enchantment
- `glow()` – Add glowing effect

### MenuAction

- `MenuAction.close()` – Close the menu
- `MenuAction.refresh()` – Refresh the menu
- `MenuAction.openMenu(menu)` – Open another menu
- `MenuAction.runCommand(command)` – Run a command
- `MenuAction.sendMessage(message)` – Send a message

## Dependencies

- Paper API 1.21+
- Adventure API (included in Paper)
- Lombok (for development)
- Caffeine (for internal caching)
- PlaceholderAPI (optional)

## License

MIT License – See the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a new branch for your feature (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push your branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## Support

- **Issues**: [GitHub Issues](https://github.com/DarkBladeDev/FastMenuLib/issues)
- **Discord**: [DarkBladeDev](https://discord.com/users/835986372594630706)
