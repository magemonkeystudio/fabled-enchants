package studio.magemonkey.fabled.enchants.cmd;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import studio.magemonkey.codex.mccore.commands.ConfigurableCommand;
import studio.magemonkey.codex.mccore.commands.IFunction;
import studio.magemonkey.codex.mccore.items.ItemManager;
import studio.magemonkey.fabled.enchants.FabledEnchants;
import studio.magemonkey.fabled.enchants.api.CustomEnchantment;
import studio.magemonkey.fabled.enchants.api.ItemSet;
import studio.magemonkey.fabled.enchants.vanilla.VanillaEnchantment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * FabledEnchants © 2024 MageMonkeyStudio
 * cmd.studio.magemonkey.fabled.enchants.CmdBook
 */
public class CmdBook implements IFunction {

    private static final String NOT_PLAYER = "not-player";
    private static final String SUCCESS    = "success";

    @Override
    public void execute(
            final ConfigurableCommand command,
            final Plugin plugin,
            final CommandSender sender,
            final String[] args,
            final boolean silent) {

        if (!(sender instanceof Player)) {
            if (!silent) {
                command.sendMessage(sender, NOT_PLAYER, "&4You must be a player to use this command");
            }
            return;
        }

        final ItemStack book = new ItemStack(ItemSet.BOOK_AND_QUILL.getItems()[0]);
        final BookMeta  meta = (BookMeta) book.getItemMeta();
        meta.addPage("FabledEnchants\nBy MageMonkeyStudio\n\n Enchantment details");
        meta.setAuthor("MageMonkeyStudio");
        meta.setTitle("FabledEnchants");

        final ArrayList<CustomEnchantment> enchants = new ArrayList<>(FabledEnchants.getRegisteredEnchantments());
        Collections.sort(enchants);

        for (final CustomEnchantment enchantment : enchants) {
            if (enchantment instanceof VanillaEnchantment) continue;
            if (enchantment.getDescription() == null) continue;
            StringBuilder page = new StringBuilder();
            page.append(enchantment.getName()).append(" - ").append(enchantment.getDescription()).append("\n\nItems: ");

            boolean first = true;
            for (Material item : enchantment.getNaturalItems()) {
                if (first) first = false;
                else page.append(", ");
                page.append(ItemManager.getVanillaName(item));
            }
            if (first) page.append("None");
            meta.addPage(page.toString());
        }
        book.setItemMeta(meta);
        ((Player) sender).getInventory().addItem(book);
        if (!silent) {
            command.sendMessage(sender, SUCCESS, "&2You have received a book with all enchantment details");
        }
    }
}
