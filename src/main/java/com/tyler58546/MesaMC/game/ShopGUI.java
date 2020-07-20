package com.tyler58546.MesaMC.game;

import com.tyler58546.MesaMC.MesaMC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class ShopGUI extends CustomGUI {

    List<ShopItem> shopItems;
    Integer rows;

    /**
     * @param main
     * @param title Title of the GUI
     * @param rows  Number of rows in the GUI
     */
    public ShopGUI(MesaMC main, String title, Integer rows, List<ShopItem> shopItems, Player player) {
        super(main, title, rows, null);
        this.shopItems = shopItems;
        this.rows = rows;
        initShop(player);
    }

    String prefixedMessage(String msg) {
        return ChatColor.BLUE+"Shop> "+ChatColor.GRAY+msg;
    }

    public void playFailSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
    }

    public void playSuccessSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }

    public void initShop(Player player) {
        items = new HashMap<Integer, ItemStack>();
        shopItems.forEach(item -> {
            item.setPlayer(player);
            items.put(item.slot, item);
        });
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        for (int i = 0; i < rows*9; i++) {
            inv.setItem(i, glass);
        }
        initializeItems();
    }

    @Override
    public void onItemClick(int slot, ItemStack item, Player player) {
        shopItems.forEach(shopItem -> {
            if (shopItem.slot == slot) {
                if (player.getInventory().contains(shopItem.price.getType(), shopItem.price.getAmount())) {
                    if (!shopItem.allowMultiple && shopItem.alreadyPurchased()) {
                        playFailSound(player);
                        player.sendMessage(prefixedMessage(ChatColor.RED+"You have already purchased this item!"));
                        return;
                    }
                    switch (shopItem.getType()) {
                        case SHIELD:
                            ItemStack shield = new ItemStack(Material.SHIELD);
                            Damageable shieldMeta = (Damageable) shield.getItemMeta();
                            shieldMeta.setDamage(335);
                            shield.setItemMeta((ItemMeta)shieldMeta);
                            ItemStack og = player.getInventory().getItemInOffHand();
                            if (og != null && og.getType() != Material.AIR) {
                                player.getInventory().addItem(player.getInventory().getItemInOffHand());
                            }
                            player.getInventory().setItemInOffHand(shield);
                            break;
                        default:
                            player.getInventory().addItem(shopItem.getInventoryItemStack());
                            break;
                    }
                    shopItem.setPlayer(player);
                    int amountLeft = shopItem.price.getAmount();
                    for (int i = 0; i < player.getInventory().getSize(); i++) {
                        ItemStack ci = player.getInventory().getItem(i);

                        if (ci != null && ci.getType() == shopItem.price.getType()) {
                            if (ci.getAmount() <= amountLeft) {
                                amountLeft -= ci.getAmount();
                                player.getInventory().setItem(i, new ItemStack(Material.AIR));
                            } else {
                                ci.setAmount(ci.getAmount() - shopItem.price.getAmount());
                                break;
                            }
                            if (amountLeft <= 0) break;
                        }
                    }
                    playSuccessSound(player);
                    player.sendMessage(prefixedMessage("You purchased "+ChatColor.YELLOW+shopItem.getAmount()+"x "+shopItem.getItemMeta().getDisplayName()+ChatColor.GRAY+" for "+shopItem.getPriceString()));
                } else {
                    playFailSound(player);
                    player.sendMessage(prefixedMessage(ChatColor.RED+"You cannot afford this item!"));
                }
                initShop(player);
            }
        });
    }
}
