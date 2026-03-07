package studio.magemonkey.fabled.enchants.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import studio.magemonkey.codex.mccore.commands.CommandManager;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.fabled.enchants.FabledEnchants;
import studio.magemonkey.fabled.enchants.api.CustomEnchantment;
import studio.magemonkey.fabled.enchants.api.Enchantments;
import studio.magemonkey.fabled.enchants.api.GlowEffects;
import studio.magemonkey.fabled.enchants.api.Tasks;
import studio.magemonkey.fabled.enchants.data.PlayerEquips;

import static studio.magemonkey.fabled.enchants.util.Utils.isPresent;

/**
 * FabledEnchants © 2026 VoidEdge
 * cmd.studio.magemonkey.fabled.enchants.CmdAdd
 */
public class CmdAdd implements IFunction {

    private static final String PLAYER_ONLY     = "player-only";
    private static final String NOT_ENCHANTMENT = "not-enchantment";
    private static final String NOT_LEVEL       = "not-level";
    private static final String NO_ITEM         = "no-item";
    private static final String SUCCESS         = "success";

    @Override
    public void execute(
            final ConfigurableCommand command,
            final Plugin plugin,
            final CommandSender sender,
            final String[] args,
            final boolean silent) {

        if (!(sender instanceof Player)) {
            if (!silent) {
                command.sendMessage(sender, PLAYER_ONLY, ChatColor.DARK_RED + "Only players can use this command");
            }
            return;
        }

        if (args.length < 2) {
            if (!silent) {
                CommandManager.displayUsage(command, sender);
            }
            return;
        }

        final Player player = (Player) sender;
        if (!isPresent(player.getEquipment().getItemInMainHand())) {
            if (!silent) {
                command.sendMessage(sender, NO_ITEM, ChatColor.DARK_RED + "You are not holding an item to enchants");
            }
        }

        final StringBuilder builder = new StringBuilder(args[0]);
        for (int i = 1; i < args.length - 1; i++) {
            builder.append(' ');
            builder.append(args[i]);
        }

        final CustomEnchantment enchantment = FabledEnchants.getEnchantment(builder.toString());
        if (enchantment == null) {
            if (!silent) {
                command.sendMessage(sender,
                        NOT_ENCHANTMENT,
                        ChatColor.GOLD + builder.toString() + ChatColor.DARK_RED + " is not an enchantment");
            }
            return;
        }

        final int level;
        try {
            level = Integer.parseInt(args[args.length - 1]);
        } catch (final Exception ex) {
            if (!silent) {
                command.sendMessage(sender,
                        NOT_LEVEL,
                        ChatColor.GOLD + args[args.length - 1] + ChatColor.DARK_RED + " is not a number");
            }
            return;
        }

        enchantment.addToItem(player.getInventory().getItemInMainHand(), level);
        GlowEffects.finalize(player.getInventory().getItemInMainHand());
        final PlayerEquips equips = Enchantments.getEquipmentData(player);
        Tasks.schedule(() -> equips.updateWeapon((player).getInventory()));
        if (!silent) {
            command.sendMessage(sender, SUCCESS, ChatColor.DARK_GREEN + "Added the enchantment successfully");
        }
    }
}
