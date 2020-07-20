package com.tyler58546.MesaMC.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class SpectatorGUIItem extends ItemStack {
    public Player player;
    public int slot;
    public SpectatorGUIItem(Player player, int slot) {
        super(Material.PLAYER_HEAD);
        this.player = player;
        this.slot = slot;
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.GREEN+"> Click to spectate");
        setLore(lore);
        SkullMeta skull = (SkullMeta) getItemMeta();
        skull.setOwningPlayer(player);
        skull.setDisplayName(ChatColor.YELLOW+player.getDisplayName());
        setItemMeta(skull);
    }
}
