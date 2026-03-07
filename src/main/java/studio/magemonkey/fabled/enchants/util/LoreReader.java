package studio.magemonkey.fabled.enchants.util;

import org.bukkit.ChatColor;
import studio.magemonkey.fabled.enchants.FabledEnchants;
import studio.magemonkey.fabled.enchants.api.CustomEnchantment;

import java.util.Objects;

/**
 * FabledEnchants © 2026 VoidEdge
 * util.studio.magemonkey.fabled.enchants.LoreReader
 */
public class LoreReader {

    /**
     * Parses an enchantment name from a bit of text, assuming the format
     * is "{color}{enchantment} {level}"
     *
     * @param text text to parse from
     * @return enchantment name
     */
    public static String parseEnchantmentName(final String text) {
        Objects.requireNonNull(text, "Text cannot be null");
        if (!text.startsWith(ChatColor.GRAY.toString())) return "";

        final String noColor = text.substring(2);
        final int    index   = noColor.lastIndexOf(' ');
        final int    level   = RomanNumerals.fromNumerals(noColor.substring(index + 1));
        return level > 0 ? noColor.substring(0, index)
                : noColor;
    }

    /**
     * Parses a level from a lore line. This expects valid roman numerals to be at the end of the line
     * with no spaces after it, matching the format for enchantments.
     *
     * @param text text to parse the level from
     * @return parsed level or 1 if not found
     */
    public static int parseEnchantmentLevel(final String text) {
        Objects.requireNonNull(text, "Text cannot be null");

        final int index = text.lastIndexOf(' ');
        final int level = RomanNumerals.fromNumerals(text.substring(index + 1));
        return level > 0 ? level : 1;
    }

    /**
     * Formats an enchantment name for appending to an item's lore.
     *
     * @param enchantment enchantment name
     * @param level       level of the enchantment
     * @return lore string for the enchantment
     */
    public static String formatEnchantment(final CustomEnchantment enchantment, final int level) {
        return ChatColor.GRAY + enchantment.getName() + (enchantment.getMaxLevel() > 1 ? " " + RomanNumerals.toNumerals(
                level) : "");
    }

    /**
     * Checks whether or not the lore line is the line for the given enchantment
     *
     * @param line line to check
     * @return true if the line matches, false otherwise
     */
    public static boolean isEnchantment(final String line) {
        return FabledEnchants.isRegistered(parseEnchantmentName(line));
    }
}
