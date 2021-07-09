package com.tyler58546.MesaMC.game.games;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.Game;
import com.tyler58546.MesaMC.game.GameType;
import com.tyler58546.MesaMC.game.Winner;
import com.tyler58546.MesaMC.game.event.*;
import com.tyler58546.MesaMC.util.Sort;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.TreeMap;
import java.util.UUID;

import static com.tyler58546.MesaMC.game.Team.CUSTOM;
import static com.tyler58546.MesaMC.game.Team.PLAYERS;

public class Slaparoo extends Game {

    public Slaparoo(MesaMC main) {
        super(main, GameType.SLAPAROO.id, GameType.SLAPAROO.name, new String[]{"slaparoo-ender", "slaparoo-shield", "slaparoo-clockaroo"}, GameType.SLAPAROO);
        description.add("Use your cookie to slap other players off the map.");
        description.add("You get 1 point for every kill.");
        description.add("First to 20 points wins the game.");
        allowInventoryMove = false;
        allowPlace = false;
        allowBreak = false;
        enableFallDamage = false;
    }

    TreeMap<String, Integer> kills = new TreeMap<String, Integer>();

    @Override
    protected Boolean canRespawn(Player player) {
        return true;
    }

    @Override
    protected Location getSpawnpoint(Player player) {
        return gameMap.getNextSpawnpoint(PLAYERS).location.toLocation(gameWorld);
    }

    void resetInventory(Player player) {
        player.getInventory().clear();
        ItemStack cookie = new ItemStack(Material.COOKIE);
        ItemMeta im = cookie.getItemMeta();
        im.setDisplayName(ChatColor.BLUE + "Larry");
        im.addEnchant(Enchantment.KNOCKBACK, 10, true);
        cookie.setItemMeta(im);

        player.getInventory().addItem(cookie);
    }

    @EventHandler
    public void onStart(GameStartEvent e) {
        if (e.game != this) {
            return;
        }
        kills = new TreeMap<String, Integer>();
        players.forEach(this::resetInventory);
        updateScoreboard();
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
        killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        if (killerKills >= 19) {
            winner = new Winner(killer);
            stop();
        }
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
    public void onReset(GameResetEvent e) {
        if (e.game != this) {
            return;
        }
        players.forEach(player -> {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        });
        kills = new TreeMap<String, Integer>();
    }

    @EventHandler
    public void onSpawn(GameSpawnPlayerEvent e) {
        if (e.game != this) {
            return;
        }
        resetInventory(e.player);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().getWorld() != gameWorld) return;
        e.setCancelled(true);
    }
}
