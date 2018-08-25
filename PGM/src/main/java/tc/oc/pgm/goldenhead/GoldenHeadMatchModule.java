package tc.oc.pgm.goldenhead;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tc.oc.minecraft.protocol.MinecraftVersion;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchScope;

import javax.inject.Inject;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ListenerScope(MatchScope.RUNNING)
public class GoldenHeadMatchModule extends MatchModule implements Listener {

    private static String GOLDEN_HEAD_DISPLAY = ChatColor.BOLD.toString() + ChatColor.AQUA + "Golden Head";
    @Inject
    private Server server;
    private HashMap<Enchantment, String> names = new HashMap<>();
    private Map<UUID, Duration> fights = new HashMap<UUID, Duration>();

    public GoldenHeadMatchModule(Match match) {
        super(match);
    }

    @Override
    public void enable() {
        super.enable();

        ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta newMeta = goldenHead.getItemMeta();
        newMeta.setDisplayName(GOLDEN_HEAD_DISPLAY);
        goldenHead.setItemMeta(newMeta);

        ShapedRecipe recipe = new ShapedRecipe(goldenHead);

        recipe.shape("GGG", "GHG", "GGG");

        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('H', new MaterialData(Material.SKULL_ITEM, (byte) 3));

        server.addRecipe(recipe);

        fillEnchantNames();
    }

    @Override
    public void disable() {
        // Recipe changes affect all worlds on the server, so we make changes at match start/end
        // to avoid interfering with adjacent matches. If we wait until unload() to reset them,
        // the next match would already be loaded.
        server.resetRecipes();
        super.disable();
    }

    private void fillEnchantNames() {
        names.put(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
        names.put(Enchantment.PROTECTION_FIRE, "Fire Protection");
        names.put(Enchantment.PROTECTION_FALL, "Feather Falling");
        names.put(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
        names.put(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
        names.put(Enchantment.OXYGEN, "Respiration");
        names.put(Enchantment.WATER_WORKER, "Aqua Affinity");
        names.put(Enchantment.THORNS, "Thorns");
        names.put(Enchantment.DEPTH_STRIDER, "Depth Strider");
        names.put(Enchantment.FROST_WALKER, "Frost Walker");
        names.put(Enchantment.BINDING_CURSE, "Curse of Binding");
        names.put(Enchantment.DAMAGE_ALL, "Sharpness");
        names.put(Enchantment.DAMAGE_UNDEAD, "Smite");
        names.put(Enchantment.DAMAGE_ARTHROPODS, "Bane of Arthropods");
        names.put(Enchantment.KNOCKBACK, "Knockback");
        names.put(Enchantment.FIRE_ASPECT, "Fire Aspect");
        names.put(Enchantment.LOOT_BONUS_MOBS, "Looting");
        names.put(Enchantment.SWEEPING_EDGE, "Sweeping Edge");
        names.put(Enchantment.DIG_SPEED, "Efficiency");
        names.put(Enchantment.SILK_TOUCH, "Silk Touch");
        names.put(Enchantment.DURABILITY, "Unbreaking");
        names.put(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
        names.put(Enchantment.ARROW_DAMAGE, "Power");
        names.put(Enchantment.ARROW_KNOCKBACK, "Punch");
        names.put(Enchantment.ARROW_FIRE, "Flame");
        names.put(Enchantment.ARROW_INFINITE, "Infinity");
        names.put(Enchantment.LUCK, "Luck of the Sea");
        names.put(Enchantment.LURE, "Lure");
        names.put(Enchantment.MENDING, "Mending");
        names.put(Enchantment.VANISHING_CURSE, "Curse of Vanishing");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.hasItemMeta() && GOLDEN_HEAD_DISPLAY.equals(item.getItemMeta().getDisplayName())) {
            event.getActor().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnvil(PrepareAnvilEvent event) {
        Player player = event.getActor();
        if (MinecraftVersion.atLeast(MinecraftVersion.MINECRAFT_1_8, player.getProtocolVersion())) {
            return;
        }
        player.sendMessage(ChatColor.AQUA + "------------------------");
        player.sendMessage(event.getInventory().getRepairCost() + " levels required");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchant(PrepareItemEnchantEvent event) {
        Player player = event.getActor();
        if (MinecraftVersion.atLeast(MinecraftVersion.MINECRAFT_1_8, player.getProtocolVersion())) {
            return;
        }
        player.sendMessage(ChatColor.AQUA + "------------------------");
        for (EnchantmentOffer offer : event.getOffers()) {
            String levels = offer.getCost() == 1 ? "Level" : "Levels";
            player.sendMessage(ChatColor.YELLOW.toString() + offer.getCost() + " " + levels + ": " + names.get(offer.getEnchantment()) + " " + offer.getEnchantmentLevel());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchant(InventoryOpenEvent event) {
        Player player = event.getActor();

        if (event.getInventory() instanceof EnchantingInventory) {
            EnchantingInventory inventory = (EnchantingInventory) event.getInventory();
            for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                if (itemStack.getType().equals(Material.INK_SACK) && itemStack.getData().getData() == (byte) 4) {
                    inventory.setSecondary(itemStack.clone());
                    itemStack.setAmount(0);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchant(InventoryCloseEvent event) {
        Player player = event.getActor();

        if (event.getInventory() instanceof EnchantingInventory) {
            EnchantingInventory inventory = (EnchantingInventory) event.getInventory();
            ItemStack secondary = inventory.getSecondary();
            if (secondary == null || !secondary.getType().equals(Material.INK_SACK)) {
                return;
            }
            player.getInventory().addItem(secondary.clone());
            secondary.setAmount(0);
        }
    }

//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onFight(EntityDamageByEntityEvent event) {
//        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
//            if (!fights.containsKey(event.getDamager().getUniqueId())) {
//                if (!fights.containsKey(event.getEntity().getUniqueId())) {
//                    fights.put(event.getDamager().getUniqueId(), match.runningTime());
//                    fights.put(event.getEntity().getUniqueId(), match.runningTime());
//                    for (MatchPlayer player : match.getObservingPlayers()) {
//                        player.sendMessage(ChatColor.YELLOW + "A fight has started between " + ((Player) event.getDamager()).getDisplayName() + " and " + ((Player) event.getEntity()).getDisplayName());
//                    }
//                    return;
//                }
//                Duration difference = match.runningTime().minus(fights.get(event.getEntity().getUniqueId()));
//                if (Comparables.greaterOrEqual(difference, Duration.ofMinutes(1))) {
//                    for (MatchPlayer player : match.getObservingPlayers()) {
//                        player.sendMessage(ChatColor.YELLOW + "A fight has started between " + ((Player) event.getDamager()).getDisplayName() + " and " + ((Player) event.getEntity()).getDisplayName());
//                    }
//                }
//                return;
//            }
//            Duration difference = match.runningTime().minus(fights.get(event.getEntity().getUniqueId()));
//            if (Comparables.greaterOrEqual(difference, Duration.ofMinutes(1))) {
//                for (MatchPlayer player : match.getObservingPlayers()) {
//                    player.sendMessage(ChatColor.YELLOW + "A fight has started between " + ((Player) event.getDamager()).getDisplayName() + " and " + ((Player) event.getEntity()).getDisplayName());
//                }
//                return;
//            }
//        }
//    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Location location = event.getEntity().getLocation();
        location.getBlock().setType(Material.NETHER_FENCE);
        location.add(0, 1, 0);
        location.getBlock().setType(Material.SKULL);
        location.getBlock().setData((byte) 1);
        Skull skull = (Skull) location.getBlock().getState();
        skull.setSkullType(SkullType.PLAYER);
        skull.setRotation(BlockFace.NORTH);
        skull.setOwner(event.getEntity().getName());
        skull.update();
    }
}
