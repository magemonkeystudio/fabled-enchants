package studio.magemonkey.fabled.enchants;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import studio.magemonkey.codex.mccore.commands.CommandManager;
import studio.magemonkey.fabled.enchants.api.CustomEnchantment;
import studio.magemonkey.fabled.enchants.api.EnchantPlugin;
import studio.magemonkey.fabled.enchants.api.EnchantmentRegistry;
import studio.magemonkey.fabled.enchants.api.Enchantments;
import studio.magemonkey.fabled.enchants.cmd.Commands;
import studio.magemonkey.fabled.enchants.data.ConfigKey;
import studio.magemonkey.fabled.enchants.data.Configuration;
import studio.magemonkey.fabled.enchants.data.Enchantability;
import studio.magemonkey.fabled.enchants.fabled.FabledHook;
import studio.magemonkey.fabled.enchants.listener.*;
import studio.magemonkey.fabled.enchants.vanilla.VanillaData;

import java.util.*;

/**
 * FabledEnchants © 2024 MageMonkeyStudio
 * studio.magemonkey.fabled.enchants.FabledEnchants
 */
public class FabledEnchants extends JavaPlugin implements EnchantmentRegistry {

    private static final Map<String, CustomEnchantment> ENCHANTMENTS = new HashMap<>();

    private final List<BaseListener> listeners = new ArrayList<>();

    @Getter
    @Accessors(fluent = true)
    private static FabledEnchants inst;

    @Override
    public void onEnable() {
        if (inst != null) throw new IllegalStateException("Cannot enable multiple times!");
        inst = this;

        Configuration.reload(this);
        Enchantability.init(this);

        registerEnchantments();
        register(new ItemListener(), true);
        register(new EnchantListener(), true);
        register(new AnvilListener(), true);
        register(new FishingListener(), Configuration.using(ConfigKey.CUSTOM_FISHING));
        Commands.init(this);
    }

    @Override
    public void onDisable() {
        if (inst == null) throw new IllegalStateException("Plugin not enabled!");
        inst = null;

        CommandManager.unregisterCommands(this);
        ENCHANTMENTS.clear();
        listeners.forEach(listener -> listener.cleanUp(this));
        listeners.clear();
        HandlerList.unregisterAll(this);
        Enchantments.clearAllEquipmentData();
    }

    private void register(final BaseListener listener, final boolean condition) {
        if (condition) {
            listeners.add(listener);
            listener.init(this);
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * @param name enchantment name
     * @return true if the enchantment is registered successfully, false otherwise
     */
    public static boolean isRegistered(final String name) {
        return name != null && ENCHANTMENTS.containsKey(name.toLowerCase());
    }

    /**
     * @param name name of the enchantment (not case-sensitive)
     * @return enchantment with the provided name
     */
    public static CustomEnchantment getEnchantment(final String name) {
        return name == null ? null : ENCHANTMENTS.get(name.toLowerCase());
    }

    /**
     * @return collection of all registered enchantments including vanilla enchantments
     */
    public static Collection<CustomEnchantment> getRegisteredEnchantments() {
        return ENCHANTMENTS.values();
    }

    /**
     * Registers enchantments with the API
     *
     * @param enchantments enchantments to register
     */
    @Override
    public void register(final CustomEnchantment... enchantments) {
        for (final CustomEnchantment enchantment : enchantments) {
            final String key = enchantment.getName().toLowerCase();
            if (ENCHANTMENTS.containsKey(key)) {
                getLogger().warning("Duplicate enchantment name \"" + enchantment.getName() + "\" was found");
                continue;
            }

            Objects.requireNonNull(enchantment, "Cannot register a null enchantment");
            enchantment.load(this);
            enchantment.save(this);
            ENCHANTMENTS.put(key, enchantment);
        }
    }

    private void registerEnchantments() {
        for (final VanillaData vanillaData : VanillaData.values()) {
            if (vanillaData.doesExist()) {
                register(vanillaData.getEnchantment());
            }
        }

        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin instanceof EnchantPlugin) {
                try {
                    ((EnchantPlugin) plugin).registerEnchantments(this);
                } catch (final Exception ex) {
                    getLogger().warning(
                            plugin.getName() + " failed to register enchantments. Send the error to the author");
                    ex.printStackTrace();
                }
            }
        }

        FabledHook.getEnchantments(this).forEach(this::register);
    }
}
