package studio.magemonkey.fabled.enchants.cmd;

import studio.magemonkey.codex.mccore.commands.CommandManager;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.SenderType;
import studio.magemonkey.fabled.enchants.FabledEnchants;

/**
 * FabledEnchants © 2026 VoidEdge
 * cmd.studio.magemonkey.fabled.enchants.Commands
 */
public class Commands {

    public static void init(final FabledEnchants plugin) {
        final ConfigurableCommand root = new ConfigurableCommand(plugin, "enchants", SenderType.ANYONE);
        root.addSubCommands(
                new ConfigurableCommand(
                        plugin,
                        "add",
                        SenderType.PLAYER_ONLY,
                        new CmdAdd(),
                        "enchants held item",
                        "<enchants> <level>",
                        "fabledenchants.admin"),
                new ConfigurableCommand(
                        plugin,
                        "remove",
                        SenderType.PLAYER_ONLY,
                        new CmdRemove(),
                        "removes enchants",
                        "",
                        "fabledenchants.admin"),
                new ConfigurableCommand(
                        plugin,
                        "reload",
                        SenderType.ANYONE,
                        new CmdReload(),
                        "reloads the plugin",
                        "",
                        "fabledenchants.admin"),
                new ConfigurableCommand(
                        plugin,
                        "graph",
                        SenderType.ANYONE,
                        new CmdGraph(),
                        "graphs probabilities",
                        "<mat> <enchants>",
                        "fabledenchants.admin"),
                new ConfigurableCommand(
                        plugin,
                        "book",
                        SenderType.PLAYER_ONLY,
                        new CmdBook(),
                        "detailed book",
                        "",
                        "fabledenchants.admin")
        );
        CommandManager.registerCommand(root);
    }
}
