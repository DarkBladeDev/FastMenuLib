package com.darkbladedev.fastmenu.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for text formatting and manipulation using Adventure API.
 * Provides methods for converting between different text formats and applying placeholders.
 */
public final class TextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private TextUtil() {
        // Utility class
    }

    /**
     * Converts a MiniMessage string to a Component.
     * This is an alias for toComponent method for backward compatibility.
     *
     * @param text the MiniMessage formatted text
     * @return the Component
     */
    @NotNull
    public static Component parseMiniMessage(@NotNull String text) {
        return toComponent(text);
    }

    /**
     * Converts a MiniMessage string to a Component.
     *
     * @param text the MiniMessage formatted text
     * @return the Component
     */
    @NotNull
    public static Component toComponent(@NotNull String text) {
        return MINI_MESSAGE.deserialize(text);
    }

    /**
     * Converts a list of MiniMessage strings to Components.
     *
     * @param texts the MiniMessage formatted texts
     * @return the list of Components
     */
    @NotNull
    public static List<Component> toComponents(@NotNull List<String> texts) {
        return texts.stream()
                .map(TextUtil::toComponent)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Component to a MiniMessage string.
     *
     * @param component the Component
     * @return the MiniMessage formatted string
     */
    @NotNull
    public static String toMiniMessage(@NotNull Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    /**
     * Converts a legacy formatted string (with & color codes) to a Component.
     *
     * @param text the legacy formatted text
     * @return the Component
     */
    @NotNull
    public static Component fromLegacy(@NotNull String text) {
        // Convert hex colors first
        String converted = HEX_PATTERN.matcher(text)
                .replaceAll("<color:#$1>");
        
        // Convert legacy codes to MiniMessage format
        converted = converted
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<underlined>")
                .replace("&o", "<italic>")
                .replace("&r", "<reset>");
        
        return MINI_MESSAGE.deserialize(converted);
    }

    /**
     * Converts a Component to legacy format with & color codes.
     *
     * @param component the Component
     * @return the legacy formatted string
     */
    @NotNull
    public static String toLegacy(@NotNull Component component) {
        return LEGACY_SERIALIZER.serialize(component);
    }

    /**
     * Applies placeholders to a text string.
     *
     * @param text         the text with placeholders
     * @param placeholders the placeholder values (key-value pairs)
     * @return the text with placeholders replaced
     */
    @NotNull
    public static String applyPlaceholders(@NotNull String text, @NotNull Object... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be provided in key-value pairs");
        }
        
        String result = text;
        for (int i = 0; i < placeholders.length; i += 2) {
            String key = String.valueOf(placeholders[i]);
            String value = String.valueOf(placeholders[i + 1]);
            result = result.replace("{" + key + "}", value);
        }
        return result;
    }

    /**
     * Applies placeholders to a list of text strings.
     *
     * @param texts        the texts with placeholders
     * @param placeholders the placeholder values (key-value pairs)
     * @return the texts with placeholders replaced
     */
    @NotNull
    public static List<String> applyPlaceholders(@NotNull List<String> texts, @NotNull Object... placeholders) {
        return texts.stream()
                .map(text -> applyPlaceholders(text, placeholders))
                .collect(Collectors.toList());
    }

    /**
     * Centers text within a specified width using a fill character.
     *
     * @param text       the text to center
     * @param width      the total width
     * @param fillChar   the character to use for filling
     * @return the centered text
     */
    @NotNull
    public static String center(@NotNull String text, int width, char fillChar) {
        if (text.length() >= width) {
            return text;
        }
        
        int padding = width - text.length();
        int leftPadding = padding / 2;
        int rightPadding = padding - leftPadding;
        
        return String.valueOf(fillChar).repeat(leftPadding) + text + String.valueOf(fillChar).repeat(rightPadding);
    }

    /**
     * Centers text within a specified width using spaces.
     *
     * @param text  the text to center
     * @param width the total width
     * @return the centered text
     */
    @NotNull
    public static String center(@NotNull String text, int width) {
        return center(text, width, ' ');
    }

    /**
     * Truncates text to a maximum length and adds an ellipsis if needed.
     *
     * @param text      the text to truncate
     * @param maxLength the maximum length
     * @return the truncated text
     */
    @NotNull
    public static String truncate(@NotNull String text, int maxLength) {
        return truncate(text, maxLength, "...");
    }

    /**
     * Truncates text to a maximum length and adds a suffix if needed.
     *
     * @param text      the text to truncate
     * @param maxLength the maximum length
     * @param suffix    the suffix to add if truncated
     * @return the truncated text
     */
    @NotNull
    public static String truncate(@NotNull String text, int maxLength, @NotNull String suffix) {
        if (text.length() <= maxLength) {
            return text;
        }
        
        int truncateLength = maxLength - suffix.length();
        if (truncateLength <= 0) {
            return suffix.substring(0, Math.min(suffix.length(), maxLength));
        }
        
        return text.substring(0, truncateLength) + suffix;
    }

    /**
     * Wraps text to fit within a specified line length.
     *
     * @param text       the text to wrap
     * @param lineLength the maximum line length
     * @return the wrapped text as a list of lines
     */
    @NotNull
    public static List<String> wrap(@NotNull String text, int lineLength) {
        String[] words = text.split("\\s+");
        List<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > lineLength) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
            }
            
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines;
    }

    /**
     * Strips all color codes and formatting from text.
     *
     * @param text the text to strip
     * @return the plain text without formatting
     */
    @NotNull
    public static String stripFormatting(@NotNull String text) {
        // Strip MiniMessage tags
        String stripped = text.replaceAll("<[^>]*>", "");
        
        // Strip legacy color codes
        stripped = stripped.replaceAll("&[0-9a-fk-or]", "");
        
        // Strip hex color codes
        stripped = HEX_PATTERN.matcher(stripped).replaceAll("");
        
        return stripped;
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param text the text to check
     * @return true if the text is null or empty
     */
    public static boolean isEmpty(@Nullable String text) {
        return text == null || text.isEmpty();
    }

    /**
     * Checks if a string is null, empty, or contains only whitespace.
     *
     * @param text the text to check
     * @return true if the text is null, empty, or blank
     */
    public static boolean isBlank(@Nullable String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Returns the first non-null, non-empty string from the provided options.
     *
     * @param texts the text options
     * @return the first valid text, or empty string if none found
     */
    @NotNull
    public static String firstNonEmpty(@Nullable String... texts) {
        for (String text : texts) {
            if (!isEmpty(text)) {
                return text;
            }
        }
        return "";
    }
}