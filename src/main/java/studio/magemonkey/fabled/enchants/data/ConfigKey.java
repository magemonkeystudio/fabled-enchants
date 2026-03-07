package studio.magemonkey.fabled.enchants.data;

/**
 * FabledEnchants © 2026 VoidEdge
 * data.studio.magemonkey.fabled.enchants.ConfigKey
 */
public enum ConfigKey {
    COLORED_NAMES_IN_ANVILS,
    CUSTOM_FISHING,
    FISHING_ENCHANTING_LEVEL,
    GLOBAL_ANVIL_LEVEL,
    MAX_ENCHANTMENTS,
    MAX_MERGED_ENCHANTMENTS,
    NON_ENCHANTABLES,
    SKILL_COOLDOWN,
    SKILL_MANA;

    private final String key = name().toLowerCase().replace('_', '-');

    public String getKey() {
        return key;
    }
}
