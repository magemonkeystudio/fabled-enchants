package studio.magemonkey.fabled.enchants.vanilla;

import org.bukkit.enchantments.Enchantment;
import studio.magemonkey.fabled.enchants.api.CustomEnchantment;
import studio.magemonkey.fabled.enchants.api.ItemSet;

/**
 * FabledEnchants © 2026 VoidEdge
 * vanilla.studio.magemonkey.fabled.enchants.VanillaData
 */
public enum VanillaData {

    // ---- Armor ---- //
    BINDING_CURSE(ItemSet.ALL, 1, 1, 25, 25, 50, 8, false),
    DEPTH_STRIDER(ItemSet.BOOTS, ItemSet.NONE, 3, 2, 10, 10, 5, 4, true, "stride"),
    FROST_WALKER(ItemSet.BOOTS, ItemSet.NONE, 2, 2, 10, 10, 5, 4, false, "stride"),
    OXYGEN(ItemSet.HELMETS, 3, 2, 10, 10, 20, 4, true),
    PROTECTION_ENVIRONMENTAL(ItemSet.ARMOR, ItemSet.NONE, 4, 10, 1, 11, 0, 1, true, "protection"),
    PROTECTION_EXPLOSIONS(ItemSet.ARMOR, ItemSet.NONE, 4, 2, 5, 8, 0, 4, true, "protection"),
    PROTECTION_FALL(ItemSet.BOOTS, ItemSet.NONE, 4, 5, 5, 6, 0, 2, true, CustomEnchantment.DEFAULT_GROUP),
    PROTECTION_FIRE(ItemSet.ARMOR, ItemSet.NONE, 4, 5, 10, 8, 0, 2, true, "protection"),
    PROTECTION_PROJECTILE(ItemSet.ARMOR, ItemSet.NONE, 4, 5, 3, 6, 0, 2, true, "protection"),
    THORNS(ItemSet.CHESTPLATES, ItemSet.ARMOR, 3, 1, -10, 20, 30, 8, true, CustomEnchantment.DEFAULT_GROUP),
    WATER_WORKER(ItemSet.HELMETS, 1, 2, 1, 25, 40, 4, true),
    SOUL_SPEED(ItemSet.BOOTS, 3, 1, 0, 10, 30, 8, false),

    // ---- Swords ---- //
    DAMAGE_ALL(ItemSet.WEAPONS, ItemSet.AXES, 5, 10, 1, 11, 9, 1, true, "damage"),
    DAMAGE_ARTHROPODS(ItemSet.WEAPONS, ItemSet.AXES, 5, 5, 5, 8, 12, 2, true, "damage"),
    DAMAGE_UNDEAD(ItemSet.WEAPONS, ItemSet.AXES, 5, 5, 5, 8, 12, 2, true, "damage"),
    FIRE_ASPECT(ItemSet.MELEE, 2, 2, 10, 20, 30, 4, true),
    KNOCKBACK(ItemSet.MELEE, 2, 5, 5, 20, 30, 2, true),
    LOOT_BONUS_MOBS(ItemSet.MELEE, 3, 2, 15, 9, 41, 4, true),
    SWEEPING_EDGE(ItemSet.MELEE, 3, 1, 5, 9, 6, 4, true),

    // --- Tools --- //
    DIG_SPEED(ItemSet.TOOLS, ItemSet.SHEARS, 5, 10, 1, 10, 40, 1, true, CustomEnchantment.DEFAULT_GROUP),
    LOOT_BONUS_BLOCKS(ItemSet.TOOLS, ItemSet.NONE, 3, 2, 15, 9, 41, 4, true, "blocks"),
    SILK_TOUCH(ItemSet.TOOLS, ItemSet.NONE, 1, 1, 15, 25, 50, 8, true, "blocks"),

    // ---- Bows ---- //
    ARROW_DAMAGE(ItemSet.BOWS, 5, 10, 1, 10, 5, 1, true),
    ARROW_FIRE(ItemSet.BOWS, 1, 2, 20, 25, 50, 4, true),
    ARROW_INFINITE(ItemSet.BOWS, ItemSet.NONE, 1, 1, 20, 25, 50, 8, true, "infinite"),
    ARROW_KNOCKBACK(ItemSet.BOWS, 2, 2, 12, 20, 5, 4, true),

    // ---- Crossbow ---- //
    MULTISHOT(ItemSet.CROSSBOW, 1, 2, 17, 25, 30, 4, true),
    QUICK_CHARGE(ItemSet.CROSSBOW, 3, 5, 10, 6, 30, 2, true),
    PIERCING(ItemSet.CROSSBOW, 4, 10, 1, 20, 30, 1, true),

    // ---- Fishing ---- //
    LUCK(ItemSet.FISHING, 3, 2, 15, 9, 41, 4, true),
    LURE(ItemSet.FISHING, 3, 2, 15, 9, 41, 4, true),

    // ---- Trident ---- //
    LOYALTY(ItemSet.TRIDENT, 3, 5, 5, 7, 43, 2, true),
    IMPALING(ItemSet.TRIDENT, 5, 2, 1, 8, 12, 4, true),
    RIPTIDE(ItemSet.TRIDENT, 3, 2, 10, 7, 43, 4, true),
    CHANNELING(ItemSet.TRIDENT, 1, 1, 25, 25, 50, 8, true),

    // ---- All ---- //
    DURABILITY(ItemSet.DURABILITY,
            ItemSet.DURABILITY_SECONDARY,
            3,
            5,
            5,
            8,
            42,
            2,
            true,
            CustomEnchantment.DEFAULT_GROUP),
    MENDING(ItemSet.DURABILITY_ALL, ItemSet.NONE, 1, 2, 0, 25, 25, 4, false, "infinite"),
    VANISHING_CURSE(ItemSet.ALL, 1, 1, 25, 0, 50, 8, false);

    private VanillaEnchantment enchantment;

    public VanillaEnchantment getEnchantment() {
        return enchantment;
    }

    public boolean doesExist() {
        return enchantment != null;
    }

    VanillaData(
            final ItemSet itemSet,
            final int maxLevel,
            final int weight,
            final int minEnchantLevel,
            final int enchantLevelScale,
            final int maxBuffer,
            final int costPerLevel,
            final boolean tableEnabled) {
        this(itemSet,
                ItemSet.NONE,
                maxLevel,
                weight,
                minEnchantLevel,
                enchantLevelScale,
                maxBuffer,
                costPerLevel,
                tableEnabled,
                CustomEnchantment.DEFAULT_GROUP);
    }

    VanillaData(
            final ItemSet itemSet,
            final ItemSet secondary,
            final int maxLevel,
            final int weight,
            final int minEnchantLevel,
            final int enchantLevelScale,
            final int maxBuffer,
            final int costPerLevel,
            final boolean tableEnabled,
            final String group) {

        final Enchantment enchant = Enchantment.getByName(name());
        if (enchant == null) return;

        enchantment = new VanillaEnchantment(enchant);
        enchantment.addNaturalItems(itemSet.getItems());
        enchantment.addAnvilItems(secondary.getItems());
        enchantment.setMaxLevel(maxLevel);
        enchantment.setWeight(weight);
        enchantment.setMinEnchantingLevel(minEnchantLevel);
        enchantment.setEnchantLevelScaleFactor(enchantLevelScale);
        enchantment.setEnchantLevelBuffer(maxBuffer);
        enchantment.setCombineCostPerLevel(costPerLevel);
        enchantment.setTableEnabled(tableEnabled);
        enchantment.setGroup(group);
    }
}
