package studio.magemonkey.fabled.enchants.data;

import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.enchants.FabledEnchants;

/**
 * FabledEnchants © 2026 VoidEdge
 * data.studio.magemonkey.fabled.enchants.Configuration
 */
public class Configuration {

    private static DataSection data;

    public static void reload(final FabledEnchants plugin) {
        final CommentedConfig config = new CommentedConfig(plugin, "config");
        config.saveDefaultConfig();
        config.checkDefaults();
        config.trim();
        config.save();
        data = config.getConfig();
    }

    public static boolean using(final ConfigKey configKey) {
        return data.getBoolean(configKey.getKey());
    }

    public static int amount(final ConfigKey configKey) {
        return data.getInt(configKey.getKey());
    }
}
