package com.tyler58546.MesaMC.game;

import com.sun.istack.internal.NotNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ShopItem extends ItemStack {
    public ItemStack price;
    public String currencyName;
    public int slot;
    public Boolean allowMultiple;
    protected Player player;
    public ShopItem(int slot, @NotNull Material type, String customName, int amount, ItemStack price, String currencyName, Boolean allowMultiple) {
        super(type, amount);
        this.slot = slot;
        this.currencyName = currencyName;
        this.price = price;
        this.allowMultiple = allowMultiple;
        ItemMeta meta = getItemMeta();
        meta.setLore(Collections.singletonList(ChatColor.YELLOW + "Price: " + ChatColor.YELLOW+ getPriceString()));
        meta.setDisplayName(ChatColor.RESET+customName);
        setItemMeta(meta);
    }
    public String getPriceString() {
        return price.getAmount()+" "+currencyName;
    }
    public ItemStack getInventoryItemStack() {
        return new ItemStack(getType(), getAmount());
    }
    public void setPlayer(Player player) {
        this.player = player;
        if (!allowMultiple && alreadyPurchased()) {
            ItemMeta meta = getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.RED + "Already Purchased!"));
            setItemMeta(meta);
        }
    }
    public boolean alreadyPurchased() {
        if (player.getInventory().contains(getType()) || player.getInventory().getItemInOffHand().getType() == getType()) return true;
        else return false;
    }
}
