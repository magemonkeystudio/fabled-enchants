package studio.magemonkey.fabled.enchants.api;

/**
 * FabledEnchants © 2024 Mage Monkey Studios
 * api.studio.magemonkey.fabled.enchants.EnchantmentRegistry
 */
public interface EnchantmentRegistry {

    /**
     * Registers enchantments with the API, allowing them to be available
     * in the enchanting tables, commands, and anvils.
     *
     * @param enchantments enchantments to register
     */
    void register(CustomEnchantment... enchantments);
}
