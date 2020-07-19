package com.tyler58546.MesaMC.game.games;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.Game;
import com.tyler58546.MesaMC.game.event.GameCombatKillEvent;
import com.tyler58546.MesaMC.game.event.GameSpawnPlayerEvent;
import com.tyler58546.MesaMC.game.event.GameStartEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Duels extends Game {

    protected Duels(MesaMC main, String id, String name, String[] maps) {
        super(main, id, name, maps);
    }

    @Override
    protected Boolean canRespawn(Player player) {
        return false;
    }

    @EventHandler
    public void onStart(GameStartEvent e) {
        if (e.game != this) return;
        players.forEach(p -> {
            p.teleport(getSpawnpoint(p));
            resetInventory(p);
            p.sendTitle(ChatColor.GREEN+"FIGHT!", "", 0, 10, 10);
        });
    }

    public void resetInventory(Player player) {
        ItemMeta im;

        //Diamond sword
        ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD);
        im = diamondSword.getItemMeta();
        im.setUnbreakable(true);
        diamondSword.setItemMeta(im);
        player.getInventory().setItem(0, diamondSword);

        //Fishing rod
        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);
        im = fishingRod.getItemMeta();
        im.setUnbreakable(true);
        fishingRod.setItemMeta(im);
        player.getInventory().setItem(1, fishingRod);

        //Bow
        ItemStack bow = new ItemStack(Material.BOW);
        im = bow.getItemMeta();
        im.setUnbreakable(true);
        bow.setItemMeta(im);
        player.getInventory().setItem(2, bow);

        //Arrows
        player.getInventory().setItem(3, new ItemStack(Material.ARROW, 5));

        //Iron Armor
        player.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        player.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
    }

}
