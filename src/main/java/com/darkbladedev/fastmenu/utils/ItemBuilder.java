package com.darkbladedev.fastmenu.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A fluent builder for creating ItemStacks with Adventure Component support.
 * Supports MiniMessage formatting for modern text styling.
 */
public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Creates a new ItemBuilder with the specified material.
     *
     * @param material the material for the item
     */
    public ItemBuilder(@NotNull Material material) {
        this(material, 1);
    }

    /**
     * Creates a new ItemBuilder with the specified material and amount.
     *
     * @param material the material for the item
     * @param amount   the amount of items
     */
    public ItemBuilder(@NotNull Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = itemStack.getItemMeta();
    }

    /**
     * Creates a new ItemBuilder from an existing ItemStack.
     *
     * @param itemStack the existing ItemStack to copy
     */
    public ItemBuilder(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    /**
     * Sets the display name of the item using MiniMessage formatting.
     *
     * @param name the display name (supports MiniMessage format)
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder name(@NotNull String name) {
        if (itemMeta != null) {
            itemMeta.displayName(MINI_MESSAGE.deserialize(name));
        }
        return this;
    }

    /**
     * Sets the display name of the item using a Component.
     *
     * @param name the display name component
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder name(@NotNull Component name) {
        if (itemMeta != null) {
            itemMeta.displayName(name);
        }
        return this;
    }

    /**
     * Sets the lore of the item using MiniMessage formatting.
     *
     * @param lore the lore lines (supports MiniMessage format)
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder lore(@NotNull String... lore) {
        return lore(Arrays.asList(lore));
    }

    /**
     * Sets the lore of the item using MiniMessage formatting.
     *
     * @param lore the lore lines (supports MiniMessage format)
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder lore(@NotNull List<String> lore) {
        if (itemMeta != null) {
            List<Component> components = new ArrayList<>();
            for (String line : lore) {
                components.add(MINI_MESSAGE.deserialize(line));
            }
            itemMeta.lore(components);
        }
        return this;
    }

    /**
     * Sets the lore of the item using Components.
     *
     * @param lore the lore components
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder loreComponents(@NotNull Component... lore) {
        return loreComponents(Arrays.asList(lore));
    }

    /**
     * Sets the lore of the item using Components.
     *
     * @param lore the lore components
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder loreComponents(@NotNull List<Component> lore) {
        if (itemMeta != null) {
            itemMeta.lore(lore);
        }
        return this;
    }

    /**
     * Adds a line to the existing lore using MiniMessage formatting.
     *
     * @param line the lore line to add
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder addLore(@NotNull String line) {
        if (itemMeta != null) {
            List<Component> lore = itemMeta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(MINI_MESSAGE.deserialize(line));
            itemMeta.lore(lore);
        }
        return this;
    }

    /**
     * Adds a Component to the existing lore.
     *
     * @param component the lore component to add
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder addLore(@NotNull Component component) {
        if (itemMeta != null) {
            List<Component> lore = itemMeta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(component);
            itemMeta.lore(lore);
        }
        return this;
    }

    /**
     * Sets the amount of items in the stack.
     *
     * @param amount the amount
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder amount(int amount) {
        itemStack.setAmount(Math.max(1, Math.min(64, amount)));
        return this;
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment the enchantment
     * @param level       the enchantment level
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder enchant(@NotNull Enchantment enchantment, int level) {
        if (itemMeta != null) {
            itemMeta.addEnchant(enchantment, level, true);
        }
        return this;
    }

    /**
     * Adds multiple enchantments to the item.
     *
     * @param enchantments the enchantments map
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder enchant(@NotNull Map<Enchantment, Integer> enchantments) {
        if (itemMeta != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }
        return this;
    }

    /**
     * Removes an enchantment from the item.
     *
     * @param enchantment the enchantment to remove
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder removeEnchant(@NotNull Enchantment enchantment) {
        if (itemMeta != null) {
            itemMeta.removeEnchant(enchantment);
        }
        return this;
    }

    /**
     * Adds item flags to hide certain attributes.
     *
     * @param flags the item flags to add
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder flags(@NotNull ItemFlag... flags) {
        if (itemMeta != null) {
            itemMeta.addItemFlags(flags);
        }
        return this;
    }

    /**
     * Removes item flags.
     *
     * @param flags the item flags to remove
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder removeFlags(@NotNull ItemFlag... flags) {
        if (itemMeta != null) {
            itemMeta.removeItemFlags(flags);
        }
        return this;
    }

    /**
     * Makes the item glow by adding an enchantment and hiding it.
     *
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder glow() {
        return glow(true);
    }

    /**
     * Sets whether the item should glow.
     *
     * @param glow true to make the item glow
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder glow(boolean glow) {
        if (glow) {
            enchant(Enchantment.UNBREAKING, 1);
            flags(ItemFlag.HIDE_ENCHANTS);
        } else {
            removeEnchant(Enchantment.UNBREAKING);
            removeFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    /**
     * Sets whether the item is unbreakable.
     *
     * @param unbreakable true to make the item unbreakable
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder unbreakable(boolean unbreakable) {
        if (itemMeta != null) {
            itemMeta.setUnbreakable(unbreakable);
        }
        return this;
    }

    /**
     * Sets the custom model data for the item.
     *
     * @param data the custom model data
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder customModelData(int data) {
        if (itemMeta != null) {
            itemMeta.setCustomModelData(data);
        }
        return this;
    }

    /**
     * Applies a custom modifier to the ItemMeta.
     *
     * @param modifier the modifier function
     * @return this builder for chaining
     */
    @NotNull
    public ItemBuilder modify(@NotNull java.util.function.Consumer<ItemMeta> modifier) {
        if (itemMeta != null) {
            modifier.accept(itemMeta);
        }
        return this;
    }

    /**
     * Builds and returns the final ItemStack.
     *
     * @return the built ItemStack
     */
    @NotNull
    public ItemStack build() {
        if (itemMeta != null) {
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack.clone();
    }

    /**
     * Creates a new ItemBuilder with the specified material.
     *
     * @param material the material
     * @return a new ItemBuilder
     */
    @NotNull
    public static ItemBuilder of(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    /**
     * Creates a new ItemBuilder with the specified material and amount.
     *
     * @param material the material
     * @param amount   the amount
     * @return a new ItemBuilder
     */
    @NotNull
    public static ItemBuilder of(@NotNull Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    /**
     * Creates a new ItemBuilder from an existing ItemStack.
     *
     * @param itemStack the existing ItemStack
     * @return a new ItemBuilder
     */
    @NotNull
    public static ItemBuilder from(@NotNull ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }
}