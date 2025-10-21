# FastMenu Library

Una librería moderna y fluida para crear menús (GUIs) en plugins de Minecraft Paper/Spigot.

## Características

- **API Fluida**: Sintaxis clara y legible usando builders
- **Manejo Automático de Eventos**: Sin necesidad de crear listeners manualmente
- **Soporte para Contenido Dinámico**: Items que se actualizan automáticamente
- **Sistema de Contexto**: Datos específicos por jugador
- **Adventure API**: Soporte completo para formateo de texto moderno
- **Extensible**: Diseño modular para futuras características

## Instalación

[![](https://jitpack.io/v/DarkBladeDev/FastMenuLib.svg)](https://jitpack.io/#DarkBladeDev/FastMenuLib)


### Como Dependencia (Recomendado)

Agrega FastMenu como dependencia en tu `build.gradle`:

```gradle
repositories {
    mavenCentral()
    maven { url = 'https://jitpack.io' }
}

dependencies {
    implementation 'com.darkbladedev:fastmenu:VERSION'
}
```

### Como Plugin Independiente

1. Compila el proyecto: `./gradlew build`
2. Coloca el JAR en tu carpeta `plugins/`
3. Reinicia el servidor

## Uso Básico

### Inicialización

```java
public class MiPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Inicializar FastMenu
        MenuManager.initialize(this);
        SchedulerUtil.initialize(this);
    }
}
```

### Crear un Menú Simple

```java
Menu menu = MenuBuilder.create("mi_menu", "§6Mi Menú", 3)
    .setItem(13, new ItemBuilder(Material.DIAMOND)
        .name("§bDiamante")
        .lore("§7¡Haz clic para obtener un diamante!")
        .onClick((player, click) -> {
            player.getInventory().addItem(new ItemStack(Material.DIAMOND));
            player.sendMessage("§a¡Recibiste un diamante!");
        })
        .build())
    
    .setItem(22, new ItemBuilder(Material.BARRIER)
        .name("§cCerrar")
        .onClick(MenuAction.close())
        .build())
    
    .build();

// Abrir el menú
menu.open(player);
```

### Contenido Dinámico

```java
Menu menu = MenuBuilder.create("tienda", "§2Tienda", 4)
    .setItem(13, SimpleMenuItem.dynamic("balance", (player, context) -> {
        int balance = getPlayerBalance(player); // Tu método para obtener balance
        
        return new ItemBuilder(Material.GOLD_INGOT)
            .name("§6Tu Balance")
            .lore("§7Balance actual: §6" + balance + " monedas")
            .build();
    }))
    .build();
```

### Sistema de Contexto

```java
Menu menu = MenuBuilder.create("configuracion", "§cConfiguración", 3)
    .setItem(11, SimpleMenuItem.dynamic("sonidos", (player, context) -> {
        boolean soundEnabled = context.getData("sound_enabled", Boolean.class, true);
        
        return new ItemBuilder(soundEnabled ? Material.LIME_DYE : Material.GRAY_DYE)
            .name(soundEnabled ? "§aSonidos: Activados" : "§cSonidos: Desactivados")
            .onClick((p, click) -> {
                boolean newState = !soundEnabled;
                context.setData("sound_enabled", newState);
                menu.refresh(p); // Actualizar el menú
            })
            .build();
    }))
    .build();
```

## Ejemplos Avanzados

Consulta la clase `ExampleMenu` en el paquete `examples` para ver implementaciones completas de:

- Menú principal con navegación
- Tienda con sistema de compras
- Perfil de jugador con estadísticas dinámicas
- Configuraciones con toggles persistentes

## API Reference

### MenuBuilder

- `create(id, title, rows)` - Crear un nuevo builder
- `setItem(slot, item)` - Establecer un item en un slot específico
- `fillRow(row, item)` - Llenar una fila completa
- `fillBorder(item)` - Llenar el borde del menú
- `onOpen(action)` - Acción al abrir el menú
- `onClose(action)` - Acción al cerrar el menú
- `build()` - Construir el menú final

### ItemBuilder

- `name(text)` - Establecer nombre (soporta MiniMessage)
- `lore(lines...)` - Agregar líneas de lore
- `onClick(action)` - Definir acción de clic
- `amount(count)` - Establecer cantidad
- `enchant(enchantment, level)` - Agregar encantamiento
- `glow()` - Agregar efecto de brillo

### MenuAction

- `MenuAction.close()` - Cerrar el menú
- `MenuAction.refresh()` - Refrescar el menú
- `MenuAction.openMenu(menu)` - Abrir otro menú
- `MenuAction.runCommand(command)` - Ejecutar comando
- `MenuAction.sendMessage(message)` - Enviar mensaje

## Dependencias

- Paper API 1.21+
- Adventure API (incluida en Paper)
- Lombok (para desarrollo)
- Caffeine (para caché interno)
- PlaceholderAPI (opcional)

## Licencia

MIT License - Ver archivo LICENSE para más detalles.

## Contribuir

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/nueva-caracteristica`)
3. Commit tus cambios (`git commit -am 'Agregar nueva característica'`)
4. Push a la rama (`git push origin feature/nueva-caracteristica`)
5. Crea un Pull Request

## Soporte

- **Issues**: [GitHub Issues](https://github.com/DarkBladeDev/FastMenuLib/issues)
- **Discord**: [DarkBladeDev](https://discord.com/users/835986372594630706)
