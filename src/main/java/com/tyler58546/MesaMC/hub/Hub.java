package com.tyler58546.MesaMC.hub;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.stats.StatsGUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Hub implements Listener {

    MesaMC main;

    public Hub(MesaMC main) {
        this.main = main;
    }

    public World getHubWorld() {
        return Bukkit.getWorld(MesaMC.mainWorldName);
    }

    static void renameItem(ItemStack item, String name) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(im);
    }
    static ItemStack createItem(Material m, String name) {
        ItemStack output = new ItemStack(m);
        renameItem(output, name);
        return output;
    }
    static ItemStack getPlayerHead(OfflinePlayer player, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) head.getItemMeta();
        sm.setOwningPlayer(player);
        sm.setDisplayName(name);
        head.setItemMeta(sm);
        return head;
    }
    public static void giveHubItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, createItem(Material.COMPASS, ChatColor.YELLOW+"Quick Compass"));
        player.getInventory().setItem(7, getPlayerHead(player, ChatColor.YELLOW+"My Profile"));
        player.getInventory().setItem(8, createItem(Material.NETHER_STAR, ChatColor.YELLOW+"MesaMC Menu"));
    }

    void handleInteraction(Player player, int slot) {
        switch (slot) {
            case 0:
                //Compass
                new HubCompassGUI(main, player).open(player);
                break;
            case 7:
                //Profile
                new StatsGUI(main, player).open(player);
                break;
            case 8:
                //Menu
                new HubServerMenuGUI(main, player).open(player);
                break;
        }
    }

    @EventHandler
    public final void onInteract(PlayerInteractEvent e) {
        if (e.getPlayer().getWorld() != getHubWorld()) return;
        if (e.getAction() == Action.PHYSICAL) return;
        ItemStack firstItem = e.getPlayer().getInventory().getItem(0);
        if (firstItem == null) return;
        if (firstItem.getType() != Material.COMPASS) return;
        handleInteraction(e.getPlayer(), e.getPlayer().getInventory().getHeldItemSlot());
    }

    @EventHandler
    public final void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.teleport(getHubWorld().getSpawnLocation());
        e.getPlayer().getInventory().setHeldItemSlot(0);
        giveHubItems(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onWorldChange(PlayerChangedWorldEvent e) {
        if (e.getPlayer().getWorld() != getHubWorld()) return;
        e.getPlayer().getInventory().clear();
        e.getPlayer().getInventory().setHeldItemSlot(0);
        new BukkitRunnable() {
            @Override
            public void run() {
                giveHubItems(e.getPlayer());
            }
        }.runTaskLater(main, 40);
    }

    @EventHandler
    public final void onInvMove(InventoryClickEvent e) {
        if (e.getWhoClicked().getWorld() != getHubWorld()) return;
        if (e.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
        e.setCancelled(true);
    }

    @EventHandler
    public final void onDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().getWorld() != getHubWorld()) return;
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        e.setCancelled(true);
    }
}
