package studio.magemonkey.fabled.enchants.fabled;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.fabled.enchants.FabledEnchants;
import studio.magemonkey.fabled.enchants.api.CustomEnchantment;

import java.io.File;
import java.util.List;

/**
 * FabledEnchants © 2026 VoidEdge
 * fabled.studio.magemonkey.fabled.enchants.FabledHook
 */
public class FabledHook {

    public static List<CustomEnchantment> getEnchantments(final FabledEnchants plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("Fabled")) {
            return loadAll(plugin);
        }
        plugin.getLogger().info("Fabled not enabled, skipping skill-based enchantments");
        return ImmutableList.of();
    }

    private static List<CustomEnchantment> loadAll(final FabledEnchants plugin) {
        final String path      = plugin.getDataFolder().getAbsolutePath() + "/" + SkillEnchantment.SAVE_FOLDER;
        final File   directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                plugin.getLogger().severe("Unable to create folder at: " + path);
            }
        }

        final File[] files = directory.listFiles();
        if (files == null) {
            plugin.getLogger().warning("Folder \"" + path + "\" does not exist");
            return ImmutableList.of();
        }

        final ImmutableList.Builder<CustomEnchantment> result = ImmutableList.builder();
        for (final File file : files) {
            if (!file.getName().endsWith(".yml")) {
                plugin.getLogger()
                        .warning(file.getName() + " is not a .yml file but is in the skill enchantments folder");
                continue;
            }

            try {
                final String          fileName = file.getName().replace(".yml", "");
                final CommentedConfig config   = new CommentedConfig(plugin, SkillEnchantment.SAVE_FOLDER + fileName);
                result.add(new SkillEnchantment(fileName, config.getConfig()));
            } catch (final Exception ex) {
                // Do nothing
                plugin.getLogger()
                        .warning("Failed to load " + file.getName() + ", make sure it points to a valid skill");
                ex.printStackTrace();
            }
        }
        return result.build();
    }
}
