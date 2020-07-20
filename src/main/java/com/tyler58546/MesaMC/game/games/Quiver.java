package com.tyler58546.MesaMC.game.games;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.*;
import com.tyler58546.MesaMC.game.event.*;
import com.tyler58546.MesaMC.game.stats.Statistic;
import com.tyler58546.MesaMC.util.Sort;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

import static com.tyler58546.MesaMC.game.Team.CUSTOM;
import static com.tyler58546.MesaMC.game.Team.PLAYERS;

/**
 * One in the Quiver
 */
public class Quiver extends Game {


    public Quiver(MesaMC main) {
        super(main, GameType.QUIVER.id, GameType.QUIVER.name, new String[]{"quiver-floating-island"}, GameType.QUIVER);
        description.add("Bows insta-kill.");
        description.add("You get one arrow every time you get a kill.");
        description.add("First to 20 kills wins the game.");
        allowInventoryMove = true;
        allowPlace = true;
    }

    TreeMap<String, Integer> kills = new TreeMap<String, Integer>();

    Integer emeraldTimerDelay = 20*10;

    /** Spawns an emerald at custom spawnpoints */
    BukkitRunnable emeraldTimer;


    @Override
    protected Boolean canRespawn(Player player) {
        return true;
    }

    @Override
    protected Location getSpawnpoint(Player player) {
        return gameMap.getNextSpawnpoint(PLAYERS).location.toLocation(gameWorld);
    }

    /*void updateScoreboard() {
        //Reset all entries
        try {
            Set<String> allScores = scoreboard.getEntries();
            if (allScores != null) {
                if (allScores.size() > 0) {
                    allScores.forEach((ent) -> {
                        scoreboard.resetScores(ent);
                    });
                }
            }
        } catch (NullPointerException e) {
            //err
        }


        //Add blank space
        if (objective == null) Bukkit.getLogger().warning("objective is null");
        objective.getScore(" ").setScore(4);
        objective.getScore(ChatColor.YELLOW+""+ChatColor.BOLD+"Game").setScore(3);
        objective.getScore("One in The Quiver").setScore(2);
        objective.getScore("  ").setScore(1);
        objective.getScore(ChatColor.YELLOW+""+ChatColor.BOLD+"Kills").setScore(0);

        //Add player kills
        if (kills != null && kills.size() > 0) {
            final int[] i = {0};
            Sort.entriesSortedByValues(kills).forEach(e -> {
                UUID uuid = UUID.fromString(e.getKey());
                i[0]--;
                Player player = Bukkit.getPlayer(uuid);
                Integer playerKills = e.getValue();
                Score score = objective.getScore(playerKills+" "+ ChatColor.GREEN+player.getName());
                score.setScore(i[0]);
            });
            objective.getScore("   ").setScore(i[0]-1);
        } else {
            objective.getScore(ChatColor.GRAY+""+ChatColor.ITALIC+"Waiting...").setScore(-1);
        }

        //Show the objective in the sidebar
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        //Display scoreboard to players
        players.forEach((player) -> {
            player.setScoreboard(scoreboard);
        });
    }*/

    void resetInventory(Player player) {

        //Unbreakable wood sword
        player.getInventory().clear();
        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setUnbreakable(true);
        sword.setItemMeta(swordMeta);
        player.getInventory().setItem(0, sword);

        //Unbreakable bow
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setUnbreakable(true);
        bow.setItemMeta(bowMeta);
        player.getInventory().setItem(1, bow);

        //Arrow
        player.getInventory().setItem(2, new ItemStack(Material.ARROW));

        //Tracking compass
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.RESET+""+ChatColor.BLUE+"Tracking Compass");
        compass.setItemMeta(compassMeta);
        player.getInventory().setItem(8, compass);
    }

    void openShop(Player player) {
        List<ShopItem> shopItems = new ArrayList<ShopItem>();
        String currencyName = "Emeralds";
        shopItems.add(new ShopItem(10, Material.RED_WOOL, "Red Wool", 8, new ItemStack(Material.EMERALD, 2), currencyName, true));
        shopItems.add(new ShopItem(11, Material.SHIELD, "Shield", 1, new ItemStack(Material.EMERALD, 2), currencyName, false));
        shopItems.add(new ShopItem(12, Material.ARROW, "Arrow", 1, new ItemStack(Material.EMERALD, 4), currencyName, true));
        shopItems.add(new ShopItem(13, Material.CROSSBOW, "Crossbow", 1, new ItemStack(Material.EMERALD, 3), currencyName, true));
        shopItems.add(new ShopItem(14, Material.GOLDEN_APPLE, ChatColor.AQUA+"Golden Apple", 1, new ItemStack(Material.EMERALD, 2), currencyName, true));
        shopItems.add(new ShopItem(15, Material.SHEARS, "Shears", 1, new ItemStack(Material.EMERALD, 1), "Emerald", true));
        shopItems.add(new ShopItem(16, Material.SKELETON_SPAWN_EGG, "Skeleton Spawn Egg", 1, new ItemStack(Material.EMERALD, 6), currencyName, true));
        ShopGUI shopGUI = new ShopGUI(main, "Emerald Shop", 3, shopItems, player);
        shopGUI.open(player);
    }

    @EventHandler
    public void onScoreboardUpdate(GameUpdateScoreboardEvent e) {
        if (e.game != this) return;
        if (state != gameState.RUNNING) return;

        e.lines.add(ChatColor.YELLOW+""+ChatColor.BOLD+"Kills");
        if (kills != null && kills.size() > 0) {
            final int[] i = {0};
            Sort.entriesSortedByValues(kills).forEach(entry -> {
                UUID uuid = UUID.fromString(entry.getKey());
                i[0]--;
                Player player = Bukkit.getPlayer(uuid);
                Integer playerKills = entry.getValue();
                e.lines.add(playerKills+" "+ ChatColor.GREEN+player.getName());
            });
        } else {
            e.lines.add(ChatColor.GRAY+""+ChatColor.ITALIC+"Waiting...");
        }
        e.lines.add("     ");
    }

    @EventHandler
    public void onStart(GameStartEvent e) {
        if (e.game != this) {
            return;
        }
        players.forEach(player -> {
            resetInventory(player);
        });
        emeraldTimer = new BukkitRunnable() {
            @Override
            public void run() {
                gameMap.getTeamSpawnPoints(CUSTOM).forEach((spawnpoint) -> {
                    if (spawnpoint.id < 2) gameWorld.dropItem(spawnpoint.location.toLocation(gameWorld), new ItemStack(Material.EMERALD));
                });
            }
        };
        emeraldTimer.runTaskTimer(main, 20, emeraldTimerDelay);
        updateScoreboard();
    }

    @EventHandler
    public void onMapLoad(GameMapLoadEvent e) {
        if (e.game != this) {
            return;
        }
        gameWorld.setGameRule(GameRule.NATURAL_REGENERATION, false);
        gameMap.getTeamSpawnPoints(CUSTOM).forEach(spawnpoint -> {
            if (spawnpoint.id <= 1) npcs.put("hologram_emeraldgen_"+spawnpoint.id, gameWorld.spawn(spawnpoint.location.toLocation(gameWorld).add(0,3.75,0), ArmorStand.class));
            if (spawnpoint.id >= 2) npcs.put("shop_"+spawnpoint.id, gameWorld.spawn(spawnpoint.location.toLocation(gameWorld), Villager.class));
        });
        npcs.forEach((id, npc) -> {
            if (npc instanceof Villager) {
                Villager v = (Villager) npc;
                v.setCustomName(ChatColor.GREEN+""+ChatColor.BOLD+"EMERALD SHOP");
                v.setCustomNameVisible(true);
                v.setAI(false);
                v.setGravity(false);
                v.setInvulnerable(true);
            }
            if (id.startsWith("hologram")) {
                ArmorStand a = (ArmorStand) npc;
                a.setInvulnerable(true);
                a.setVisible(false);
                a.setGravity(false);
                a.setCustomNameVisible(true);
                a.setMarker(true);
            }
            if (id.startsWith("hologram_emeraldgen_")) {
                ArmorStand a = (ArmorStand) npc;
                a.setCustomName(ChatColor.GREEN+""+ChatColor.BOLD+"EMERALD GENERATOR");
            }
        });
    }

    @EventHandler
    public void onEntityInteraction(PlayerInteractAtEntityEvent e) {
        if (e.getPlayer().getWorld() != gameWorld) return;
        e.setCancelled(true);
        npcs.forEach((id, npc) -> {
            if (npc == e.getRightClicked() && id.startsWith("shop_")) {
                openShop(e.getPlayer());
            }
        });
    }


    @EventHandler
    public void onKill(GameCombatKillEvent e) {
        if (e.game != this) {
            return;
        }
        World world = e.killed.getWorld();
        if (world != gameWorld || state != gameState.RUNNING) {
            return;
        }
        Player killer = e.killer;
        Integer killerKills = kills.get(killer.getUniqueId()+"");
        if (killerKills == null) killerKills = 0;
        kills.put(killer.getUniqueId()+"", killerKills+1);
        updateScoreboard();
        killer.getInventory().addItem(new ItemStack(Material.ARROW));
        killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        if (killerKills >= 19) {
            winner = new Winner(killer);
            stop();
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        World world = e.getEntity().getWorld();
        if (world != gameWorld || state != gameState.RUNNING) {
            return;
        }

        if (e.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getEntity();
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            arrow.setDamage(100);
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                addStat(Statistic.ARROWS_SHOT, player);
            }
            if (e.getHitBlock() != null) {
                if (blockPlacedByPlayer(e.getHitBlock().getLocation())) {
                    if (e.getHitBlock().getType() == Material.RED_WOOL) e.getHitBlock().breakNaturally();
                    arrow.remove();
                    if (arrow.getShooter() instanceof Player) {
                        ((Player) arrow.getShooter()).getInventory().addItem(new ItemStack(Material.ARROW));
                    }
                }
            }
            if (e.getHitEntity() != null) {
                arrow.remove();
                if (arrow.getShooter() instanceof Player && !(e.getHitEntity() instanceof Player)) {
                    ((Player) arrow.getShooter()).getInventory().addItem(new ItemStack(Material.ARROW));
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(GameSpawnPlayerEvent e) {
        if (e.game != this) {
            return;
        }
        //e.player.teleport(gameSpawn);
        resetInventory(e.player);
    }

    @EventHandler
    public void onReset(GameResetEvent e) {
        if (e.game != this) {
            return;
        }
        players.forEach(player -> {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        });
        kills = new TreeMap<String, Integer>();
        emeraldTimer.cancel();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if (e.getPlayer().getWorld() != gameWorld) return;
        if (e.getItem() == null) return;
        if(e.getAction()== Action.RIGHT_CLICK_BLOCK && e.getItem().getType()==Material.SKELETON_SPAWN_EGG){
            e.setCancelled(true);
            if (e.getHand() == EquipmentSlot.HAND) {
                int amount = e.getPlayer().getInventory().getItemInMainHand().getAmount();
                if (amount > 1) {
                    e.getPlayer().getInventory().getItemInMainHand().setAmount(amount-1);
                } else {
                    e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }
            } else if (e.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
            Entity i = e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation().add(0.5, 1, 0.5), EntityType.SKELETON);
            i.setCustomName(ChatColor.YELLOW+e.getPlayer().getName()+"'s Skeleton");
            i.setCustomNameVisible(true);
            ((Skeleton) i).getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
            npcs.put("skeleton_"+e.getPlayer().getUniqueId()+"_"+i.getUniqueId(), i);
        }
    }

    @EventHandler
    public void onTarget (EntityTargetEvent e) {
        if (e.getEntity().getWorld() != gameWorld) return;
        if (e.getTarget() instanceof Player) {
            Player player = (Player) e.getTarget();
            npcs.forEach((id, ent) -> {
                if (id.startsWith("skeleton_"+player.getUniqueId())) {
                    if (ent == e.getEntity()) {
                        e.setCancelled(true);
                    }
                }
            });
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().getWorld() != gameWorld) return;
        switch (e.getItemDrop().getItemStack().getType()) {
            case SHIELD:
            case WOODEN_SWORD:
            case BOW:
            case COMPASS:
                e.setCancelled(true);
                break;
        }
    }
}
