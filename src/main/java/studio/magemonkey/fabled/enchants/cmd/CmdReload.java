package studio.magemonkey.fabled.enchants.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.fabled.enchants.FabledEnchants;

/**
 * FabledEnchants © 2026 VoidEdge
 * cmd.studio.magemonkey.fabled.enchants.CmdReload
 */
public class CmdReload implements IFunction {

    @Override
    public void execute(
            final ConfigurableCommand configurableCommand,
            final Plugin plugin,
            final CommandSender commandSender,
            final String[] strings,
            final boolean silent) {

        final FabledEnchants fabledEnchants = JavaPlugin.getPlugin(FabledEnchants.class);
        fabledEnchants.onDisable();
        fabledEnchants.onEnable();
        if (!silent) {
            commandSender.sendMessage(ChatColor.GREEN + "FabledEnchants has been reloaded!");
        }
    }
}
