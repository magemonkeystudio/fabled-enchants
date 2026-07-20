package studio.magemonkey.fabled.enchants.data;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import studio.magemonkey.codex.mccore.config.parse.DataSection;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Covers {@link Enchantability#populate}, which is responsible for keeping
 * enchantability.yml in sync with whichever materials actually exist on the
 * running server:
 * <ul>
 *     <li>on an older server missing newer materials (e.g. spears), it must not
 *     write bogus, unmatched entries into the config</li>
 *     <li>on a server that has since upgraded past a config generated on an older
 *     version, it must merge in the newly-available types on the next load
 *     without touching values the user already customized</li>
 * </ul>
 * <p>
 * The real classpath used to run these tests resolves an older paper-api (via
 * MockBukkit) that predates spear materials, so {@code Material.matchMaterial}
 * naturally returns null for e.g. {@code DIAMOND_SPEAR} here. That's useful for
 * proving the "old server" behavior for real, but it means the "spear exists"
 * branch would never actually execute if tests only relied on the ambient
 * classpath. The {@code *_whenSpearsExist} tests below mock {@link Material} to
 * force that branch to run deterministically, independent of whichever API jar
 * happens to be on the test classpath.
 */
class EnchantabilityTest {

    @Test
    void newSection_isCreatedWithAllValidMaterialTypes() {
        final DataSection data = new DataSection();

        final boolean changed = Enchantability.populate(data, Enchantability.WEAPON, "DIAMOND", "tool", 10);

        assertTrue(changed);
        assertTrue(data.has("diamond-tool"));
        final DataSection section = data.getSection("diamond-tool");
        assertEquals(10, section.getInt(Enchantability.ENCHANTABILITY));
        assertTrue(section.getList(Enchantability.TYPES).contains("DIAMOND_SWORD"));
    }

    @Test
    void newSection_omitsSpear_whenServerPredatesIt() {
        // Exercises the real (older) API jar on this test classpath, which has no
        // spear materials -- matchMaterial("DIAMOND_SPEAR") genuinely returns null.
        assertNull(Material.matchMaterial("DIAMOND_SPEAR"), "test assumption: this classpath predates spears");

        final DataSection data = new DataSection();
        Enchantability.populate(data, Enchantability.WEAPON, "DIAMOND", "tool", 10);

        final List<String> types = data.getSection("diamond-tool").getList(Enchantability.TYPES);
        assertFalse(types.contains("DIAMOND_SPEAR"), "no bogus entry should be written for a material that doesn't exist yet");
    }

    @Test
    void newSection_includesSpear_whenSpearsExist() {
        try (MockedStatic<Material> material = mockStatic(Material.class, CALLS_REAL_METHODS)) {
            stubSpearsAsSupported(material);

            final DataSection data = new DataSection();
            final boolean     changed = Enchantability.populate(data, Enchantability.WEAPON, "DIAMOND", "tool", 10);

            assertTrue(changed);
            final List<String> types = data.getSection("diamond-tool").getList(Enchantability.TYPES);
            assertTrue(types.contains("DIAMOND_SWORD"));
            assertTrue(types.contains("DIAMOND_SPEAR"), "spear should be included once it exists on the server");
        }
    }

    @Test
    void existingSection_mergesSpear_whenServerGainsSupportAfterUpgrade_withoutOverwritingCustomValue() {
        // Simulate an enchantability.yml generated before spears existed, where the
        // server admin has since customized the enchantability value. Then simulate
        // the server having since upgraded to a version that supports spears.
        final DataSection data    = new DataSection();
        final DataSection section = data.createSection("wooden-tool");
        section.set(Enchantability.TYPES, List.of("WOODEN_AXE", "WOODEN_HOE", "WOODEN_PICKAXE",
                "WOODEN_SHOVEL", "WOODEN_SWORD"));
        section.set(Enchantability.ENCHANTABILITY, 99);

        try (MockedStatic<Material> material = mockStatic(Material.class, CALLS_REAL_METHODS)) {
            stubSpearsAsSupported(material);

            final boolean changed = Enchantability.populate(data, Enchantability.WEAPON, "WOODEN", "tool", 15);

            assertTrue(changed, "newly-available WOODEN_SPEAR should trigger a merge");
            final DataSection updated = data.getSection("wooden-tool");
            assertEquals(99, updated.getInt(Enchantability.ENCHANTABILITY),
                    "the admin's customized value must not be clobbered by the default");

            final List<String> types = updated.getList(Enchantability.TYPES);
            assertTrue(types.contains("WOODEN_SPEAR"), "spear should be merged in on upgrade");
            assertTrue(types.contains("WOODEN_SWORD"), "pre-existing types must be preserved");
            assertEquals(6, types.size());
        }
    }

    @Test
    void zeroValue_writesNothing() {
        final DataSection data = new DataSection();

        final boolean changed = Enchantability.populate(data, Enchantability.WEAPON, "LEATHER", "tool", 0);

        assertFalse(changed);
        assertFalse(data.has("leather-tool"));
    }

    @Test
    void unknownMaterialClass_isSkippedEntirely_forOlderServerCompat() {
        // Simulates a material tier that doesn't exist on the running server version
        // (the same situation "SPEAR" was in before spears were added to the game).
        final DataSection data = new DataSection();

        final boolean changed = Enchantability.populate(data, Enchantability.WEAPON, "UNOBTAINIUM", "tool", 10);

        assertFalse(changed, "no section should be written when none of its material types exist on this server");
        assertFalse(data.has("unobtainium-tool"));
    }

    @Test
    void partiallyUnknownTypes_areFilteredOutWithoutFailingTheWholeEntry() {
        final DataSection data = new DataSection();

        final boolean changed = Enchantability.populate(
                data, List.of("SWORD", "NOT_A_REAL_TOOL_TYPE"), "DIAMOND", "tool", 10);

        assertTrue(changed);
        final List<String> types = data.getSection("diamond-tool").getList(Enchantability.TYPES);
        assertEquals(List.of("DIAMOND_SWORD"), types);
    }

    @Test
    void existingSection_alreadyUpToDate_reportsNoChange() {
        final DataSection data    = new DataSection();
        final DataSection section = data.createSection("diamond-tool");
        section.set(Enchantability.TYPES,
                Enchantability.WEAPON.stream().map(t -> "DIAMOND_" + t).toList());
        section.set(Enchantability.ENCHANTABILITY, 10);

        final boolean changed = Enchantability.populate(data, Enchantability.WEAPON, "DIAMOND", "tool", 10);

        assertFalse(changed, "no rewrite/save should be triggered when nothing is missing");
    }

    /**
     * Makes {@code Material.matchMaterial} report success for any {@code *_SPEAR} name while
     * delegating every other lookup to the real implementation, simulating a server version
     * where spear materials exist without depending on the test classpath's actual API jar.
     */
    private static void stubSpearsAsSupported(final MockedStatic<Material> material) {
        material.when(() -> Material.matchMaterial(argThat(type -> type != null && type.endsWith("_SPEAR"))))
                .thenReturn(Material.DIAMOND_SWORD);
    }
}
