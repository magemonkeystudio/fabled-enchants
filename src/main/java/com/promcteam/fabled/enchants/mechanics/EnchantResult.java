package com.promcteam.fabled.enchants.mechanics;

import com.promcteam.fabled.enchants.api.CustomEnchantment;
import com.promcteam.fabled.enchants.vanilla.VanillaEnchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * FabledEnchants © 2024 ProMCTeam
 * mechanics.com.promcteam.fabled.enchants.EnchantResult
 */
public class EnchantResult {
    private final Map<CustomEnchantment, Integer> enchantments = new HashMap<>();

    private VanillaEnchantment firstVanilla;
    private int                firstVanillaLevel;
    private int                enchantLevel;
    private int                enchantabilityModifier;

    public EnchantResult(final int enchantLevel, final int modifier) {
        this.enchantLevel = enchantLevel;
        this.enchantabilityModifier = modifier;
    }

    void addEnchantment(final CustomEnchantment enchant, final Integer level) {
        if (enchant instanceof VanillaEnchantment) {
            firstVanilla = (VanillaEnchantment) enchant;
            firstVanillaLevel = level;
        }
        enchantments.put(enchant, level);
    }

    public int getEnchantabilityModifier() {
        return enchantabilityModifier;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public Optional<VanillaEnchantment> getFirstVanillaEnchantment() {
        return Optional.ofNullable(firstVanilla);
    }

    public int getFirstVanillaLevel() {
        return firstVanillaLevel;
    }

    public Map<CustomEnchantment, Integer> getEnchantments() {
        return enchantments;
    }
}
