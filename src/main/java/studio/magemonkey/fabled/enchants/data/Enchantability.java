package studio.magemonkey.fabled.enchants.data;

import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.enchants.FabledEnchants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FabledEnchants © 2026 VoidEdge
 * data.studio.magemonkey.fabled.enchants.Enchantability
 */
public class Enchantability {

    static final String         TYPES          = "types";
    static final String         ENCHANTABILITY = "enchantability";
    private static final String DEFAULT        = "default";

    private static final Map<Material, Integer> VALUES = new HashMap<>();

    private static int defaultValue = 10;

    public static int determine(final Material material) {
        return VALUES.getOrDefault(material, defaultValue);
    }

    public static void init(final FabledEnchants fabledEnchants) {
        final CommentedConfig config = new CommentedConfig(fabledEnchants, "enchantability");
        checkDefaults(config);

        final DataSection data = config.getConfig();
        defaultValue = data.getInt(DEFAULT, 10);
        for (final String key : data.keys()) {
            if (data.isSection(key)) {
                final DataSection section = data.getSection(key);
                if (!section.has(TYPES) || !section.has(ENCHANTABILITY)) {
                    fabledEnchants.getLogger().warning(key + " in enchantability.yml is missing required values");
                } else {
                    final int value = section.getInt(ENCHANTABILITY, 0);
                    section.getList(TYPES).forEach(type -> {
                        final Material mat = Material.matchMaterial(type);
                        if (mat == null) {
                            fabledEnchants.getLogger()
                                    .warning(type + " is not a valid material (under " + key
                                            + " in enchantability.yml)");
                        } else {
                            VALUES.put(mat, value);
                        }
                    });
                }
            } else if (!key.equals(DEFAULT)) {
                fabledEnchants.getLogger().warning(key + " in enchantability.yml is not formatted properly");
            }
        }
    }

    private static void checkDefaults(final CommentedConfig config) {
        final DataSection data    = config.getConfig();
        boolean           changed = false;

        for (final MaterialClass materialClass : MaterialClass.values()) {
            changed |= populate(data, ARMOR, materialClass.name, "armor", materialClass.armor);
            changed |= populate(data, WEAPON, materialClass.name, "tool", materialClass.weapon);
        }

        if (!data.has(DEFAULT)) {
            data.set(DEFAULT, 1);
            changed = true;
        }

        if (changed) {
            config.save();
        }
    }

    /**
     * Ensures the config has an entry with all currently-valid material types for the given
     * material class/category, without touching an existing entry's enchantability value or
     * overwriting types that were removed by the user. Only material names that actually exist
     * on the running server are written, so older servers won't get bogus entries (e.g. spears
     * before they existed), and newer servers will pick up new types on the next load without
     * needing the config regenerated.
     */
    static boolean populate(
            final DataSection data,
            final List<String> names,
            final String material,
            final String category,
            final int value) {

        if (value == 0) {
            return false;
        }

        final List<String> types = names.stream()
                .map(type -> material + "_" + type)
                .filter(type -> Material.matchMaterial(type) != null)
                .collect(Collectors.toList());
        if (types.isEmpty()) {
            return false;
        }

        final String sectionKey = material.toLowerCase() + "-" + category;

        if (!data.has(sectionKey)) {
            final DataSection section = data.createSection(sectionKey);
            section.set(TYPES, types);
            section.set(ENCHANTABILITY, value);
            return true;
        }

        final DataSection  section = data.getSection(sectionKey);
        final List<String> existing = section.getList(TYPES);
        final List<String> merged  = new ArrayList<>(existing);
        boolean            added   = false;
        for (final String type : types) {
            if (!merged.contains(type)) {
                merged.add(type);
                added = true;
            }
        }
        if (added) {
            section.set(TYPES, merged);
        }
        return added;
    }

    static final List<String> ARMOR = ImmutableList.<String>builder()
            .add("BOOTS")
            .add("CHESTPLATE")
            .add("HELMET")
            .add("LEGGINGS")
            .build();

    static final List<String> WEAPON = ImmutableList.<String>builder()
            .add("AXE")
            .add("HOE")
            .add("PICKAXE")
            .add("SHOVEL")
            .add("SPEAR")
            .add("SWORD")
            .build();

    private enum MaterialClass {
        WOOD(0, 15, "WOODEN"),
        STONE(0, 5),
        IRON(9, 14),
        GOLD(25, 22, "GOLDEN"),
        DIAMOND(10, 10),
        NETHERITE(15, 15),
        LEATHER(15, 0),
        CHAINMAIL(12, 0);

        private final int    armor;
        private final int    weapon;
        private final String name;

        MaterialClass(final int armor, final int weapon) {
            this.armor = armor;
            this.weapon = weapon;
            this.name = this.name();
        }

        MaterialClass(final int armor, final int weapon, final String name) {
            this.armor = armor;
            this.weapon = weapon;
            this.name = name;
        }
    }
}
