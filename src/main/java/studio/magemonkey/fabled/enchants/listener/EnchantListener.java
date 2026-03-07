package studio.magemonkey.fabled.enchants.listener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import studio.magemonkey.codex.mccore.config.CommentedConfig;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.codex.mccore.items.ItemManager;
import studio.magemonkey.fabled.enchants.FabledEnchants;
import studio.magemonkey.fabled.enchants.api.Enchantments;
import studio.magemonkey.fabled.enchants.api.GlowEffects;
import studio.magemonkey.fabled.enchants.api.ItemSet;
import studio.magemonkey.fabled.enchants.api.Tasks;
import studio.magemonkey.fabled.enchants.data.ConfigKey;
import studio.magemonkey.fabled.enchants.data.Configuration;
import studio.magemonkey.fabled.enchants.data.Path;
import studio.magemonkey.fabled.enchants.mechanics.EnchantResult;
import studio.magemonkey.fabled.enchants.mechanics.EnchantingMechanics;

import java.util.*;

import static studio.magemonkey.fabled.enchants.util.Utils.isPresent;

/**
 * FabledEnchants © 2026 VoidEdge
 * listener.studio.magemonkey.fabled.enchants.EnchantListener
 */
public class EnchantListener extends BaseListener {

    private static final String DATA_FILE = "seeds";

    private final Map<UUID, Long>      enchantSeeds = new HashMap<>();
    private final Map<UUID, ItemStack> placeholders = new HashMap<>();
    private final Map<UUID, int[]>     offers       = new HashMap<>();

    private final EnchantingMechanics mechanics = new EnchantingMechanics();

    private final Random random = new Random();

    private final boolean nonEnchantables = Configuration.using(ConfigKey.NON_ENCHANTABLES);

    @Override
    public void init(final FabledEnchants plugin) {
        final DataSection data = new CommentedConfig(plugin, Path.DATA_FOLDER + DATA_FILE).getConfig();
        data.keys().forEach(key -> enchantSeeds.put(UUID.fromString(key), Long.parseLong(data.getString(key))));
    }

    @Override
    public void cleanUp(final FabledEnchants plugin) {
        final CommentedConfig config = new CommentedConfig(plugin, Path.DATA_FOLDER + DATA_FILE);
        final DataSection     data   = config.getConfig();
        data.clear();

        enchantSeeds.forEach((uuid, seed) -> data.set(uuid.toString(), Long.toString(seed)));
        config.save();
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        enchantSeeds.computeIfAbsent(event.getPlayer().getUniqueId(), uuid -> random.nextLong());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareEnchant(final PrepareItemEnchantEvent event) {
        final ItemStack item = getItem(event.getItem(), event.getEnchanter());
        final long      seed = enchantSeeds.get(event.getEnchanter().getUniqueId());
        offers.put(event.getEnchanter().getUniqueId(), transformOffers(event.getOffers()));
        for (final EnchantmentOffer offer : event.getOffers()) {
            if (offer == null) continue;
            final EnchantResult result = mechanics.generateEnchantments(
                    event.getEnchanter(), item, offer.getCost(), true, seed);
            result.getFirstVanillaEnchantment().ifPresent(enchant -> {
                offer.setEnchantment(enchant.getEnchantment());
                offer.setEnchantmentLevel(result.getFirstVanillaLevel());
            });
        }
    }

    private int[] transformOffers(final EnchantmentOffer[] offers) {
        final List<Integer> result = new ArrayList<>(6);
        for (int i = 0; i < offers.length; i++) {
            if (offers[i] == null) continue;

            result.add(offers[i].getCost());
            result.add(i + 1);
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    private ItemStack getItem(final ItemStack fromEvent, final Player enchanter) {
        return placeholders.getOrDefault(enchanter.getUniqueId(), fromEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnchant(final EnchantItemEvent event) {
        final ItemStack item = getItem(event.getItem(), event.getEnchanter());
        final long      seed = enchantSeeds.get(event.getEnchanter().getUniqueId());
        final EnchantResult result = mechanics.generateEnchantments(
                event.getEnchanter(), item, event.getExpLevelCost(), true, seed);

        event.setCancelled(true);
        if (result.getEnchantments().isEmpty()) {
            return;
        }
        placeholders.remove(event.getEnchanter().getUniqueId());
        event.getEnchantsToAdd().clear();
        result.getEnchantments().forEach((enchant, level) -> enchant.addToItem(item, level));
        GlowEffects.finalize(item);
        enchantSeeds.put(event.getEnchanter().getUniqueId(), random.nextLong());
        event.getInventory().setItem(0, item);

        if (event.getEnchanter().getGameMode() != GameMode.CREATIVE) {
            int         cost  = 0;
            final int[] tiers = offers.get(event.getEnchanter().getUniqueId());
            for (int i = 0; i < tiers.length; i += 2) {
                if (tiers[i] == event.getExpLevelCost()) cost = tiers[i + 1];
            }
            event.getEnchanter().setLevel(event.getEnchanter().getLevel() - cost);
            EnchantingInventory inventory = (EnchantingInventory) event.getInventory();
            ItemStack           lapis     = inventory.getSecondary();
            lapis.setAmount(lapis.getAmount() - cost);
            inventory.setSecondary(lapis);
        }
    }

    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (!nonEnchantables) {
            return;
        }

        if (event.getInventory().getType() == InventoryType.ENCHANTING) {
            if (event.getRawSlot() == 0 && placeholders.containsKey(event.getWhoClicked().getUniqueId())) {
                event.getInventory().setItem(0, placeholders.remove(event.getWhoClicked().getUniqueId()));
            }

            Tasks.schedule(() -> {
                final ItemStack item = event.getInventory().getItem(0);
                if (isPresent(item) && !ENCHANTABLES.contains(item.getType())
                        && Enchantments.getAllEnchantments(item).isEmpty()
                        && mechanics.hasValidEnchantments(item)
                        && !placeholders.containsKey(event.getWhoClicked().getUniqueId())) {
                    placeholders.put(event.getWhoClicked().getUniqueId(), item);
                    event.getInventory().setItem(0, wrap(item));
                }
            });
        }
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (placeholders.containsKey(event.getPlayer().getUniqueId())) {
            event.getInventory().setItem(0, placeholders.remove(event.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        offers.remove(event.getPlayer().getUniqueId());
        if (placeholders.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().closeInventory();
        }
    }

    private ItemStack wrap(final ItemStack item) {
        final ItemStack wrapper = new ItemStack(Material.BOOK);
        final ItemMeta  meta    = wrapper.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Enchanting");
        meta.setLore(ImmutableList.of(ItemManager.getVanillaName(item)));
        wrapper.setItemMeta(meta);
        return wrapper;
    }

    private static final Set<Material> ENCHANTABLES = ImmutableSet.<Material>builder()
            .add(ItemSet.VANILLA_ENCHANTABLES.getItems())
            .add(Material.BOOK)
            .add(Material.ENCHANTED_BOOK)
            .build();
}
