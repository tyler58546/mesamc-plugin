package com.tyler58546.MesaMC.game;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class BedwarsShopItem extends ShopItem {
    public boolean giveItem;
    public String id;
    public ItemStack removeItem;
    public BedwarsShopItem(int slot, Material type, String customName, int amount, ItemStack price, String currencyName, Boolean allowMultiple, boolean giveItem, @Nullable ItemStack removeItem, String id) {
        super(slot, type, customName, amount, price, currencyName, allowMultiple);
        this.giveItem = giveItem;
        this.id = id;
        this.removeItem = removeItem;
    }
}
