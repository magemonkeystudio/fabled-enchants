package studio.magemonkey.fabled.enchants.vanilla;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.permissions.Permissible;
import studio.magemonkey.fabled.enchants.api.CustomEnchantment;
import studio.magemonkey.fabled.enchants.data.Permission;

import java.util.Map;

/**
 * FabledEnchants © 2026 VoidEdge
 * vanilla.studio.magemonkey.fabled.enchants.VanillaEnchantment
 */
public class VanillaEnchantment extends CustomEnchantment {

    private final Enchantment enchantment;

    VanillaEnchantment(final Enchantment vanilla) {
        super(vanilla.getName(), vanilla.getName() + " vanilla enchantment");
        this.enchantment = vanilla;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    @Override
    public ItemStack addToItem(final ItemStack item, final int level) {
        if (item.getType() == Material.BOOK) {
            item.setType(Material.ENCHANTED_BOOK);
        }
        if (item.getType() == Material.ENCHANTED_BOOK) {
            final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            meta.addStoredEnchant(enchantment, level, true);
            item.setItemMeta(meta);
            return item;
        }
        item.addUnsafeEnchantment(enchantment, level);
        return item;
    }

    @Override
    public void addToEnchantment(final Map<Enchantment, Integer> enchantments,
                                 final ItemStack result,
                                 final int level) {
        enchantments.put(enchantment, level);
    }

    @Override
    public ItemStack removeFromItem(final ItemStack item) {
        if (item.getType() == Material.ENCHANTED_BOOK) {
            final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            meta.removeStoredEnchant(enchantment);
            item.setItemMeta(meta);
        }
        item.removeEnchantment(enchantment);
        return item;
    }

    @Override
    public boolean hasPermission(final Permissible permissible) {
        return permissible == null
                || permissible.hasPermission(Permission.ENCHANT_VANILLA)
                || permissible.hasPermission(getPermission());
    }

    @Override
    public String getSaveFolder() {
        return "enchants/vanilla/";
    }
}
