package studio.magemonkey.fabled.enchants.api;

/**
 * FabledEnchants © 2026 VoidEdge
 * api.studio.magemonkey.fabled.enchants.EnchantPlugin
 * <p>
 * Interface to be implemented by plugins adding new enchantments to the server.
 * FabledEnchants will find any plugins implementing this class and call the
 * registration method on enable.
 */
public interface EnchantPlugin {

    /**
     * This is where you should register your enchantments.
     *
     * @param registry registry to register enchantments with
     */
    void registerEnchantments(EnchantmentRegistry registry);
}
