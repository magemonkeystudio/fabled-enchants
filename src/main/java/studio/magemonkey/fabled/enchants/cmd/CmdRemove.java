package studio.magemonkey.fabled.enchants.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.fabled.enchants.api.CustomEnchantment;
import studio.magemonkey.fabled.enchants.api.Enchantments;

import java.util.Map;

import static studio.magemonkey.fabled.enchants.util.Utils.isPresent;

/**
 * FabledEnchants © 2026 VoidEdge
 * cmd.studio.magemonkey.fabled.enchants.CmdRemove
 */
public class CmdRemove implements IFunction {

    private static final String NOT_PLAYER  = "not-player";
    private static final String NO_ITEM     = "no-item";
    private static final String NO_ENCHANTS = "no-enchants";
    private static final String REMOVED     = "removed";

    @Override
    public void execute(
            final ConfigurableCommand command,
            final Plugin plugin,
            final CommandSender sender,
            final String[] strings,
            final boolean silent) {

        if (!(sender instanceof Player)) {
            if (!silent) {
                command.sendMessage(sender, NOT_PLAYER, "&4Only players can use this command");
            }
            return;
        }

        final Player    player = (Player) sender;
        final ItemStack item   = player.getEquipment().getItemInMainHand();
        if (!isPresent(item)) {
            if (!silent) {
                command.sendMessage(sender, NO_ITEM, "&4You don't have an item in your hand");
            }
            return;
        }

        final Map<CustomEnchantment, Integer> enchantments = Enchantments.getAllEnchantments(item);
        if (enchantments.isEmpty()) {
            if (!silent) {
                command.sendMessage(sender, NO_ENCHANTS, "&4That item doesn't have any enchantments");
            }
            return;
        }

        Enchantments.removeAllEnchantments(item);
        if (!silent) {
            command.sendMessage(sender, REMOVED, "&2 Removed all enchantments from your held item");
        }
    }
}
