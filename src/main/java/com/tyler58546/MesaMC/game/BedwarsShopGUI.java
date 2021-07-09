package com.tyler58546.MesaMC.game;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.games.Bedwars;
import com.tyler58546.MesaMC.game.games.BedwarsPlayerData;
import com.tyler58546.MesaMC.game.games.BedwarsTeamData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BedwarsShopGUI extends ShopGUI {

    public Bedwars game;

    /**
     * @param main
     * @param title     Title of the GUI
     * @param rows      Number of rows in the GUI
     * @param shopItems
     * @param player
     */
    public BedwarsShopGUI(MesaMC main, String title, Integer rows, List<ShopItem> shopItems, Player player, Bedwars game) {
        super(main, title, rows, shopItems, player);
        this.game = game;
    }

    @Override
    public void onItemClick(int slot, ItemStack item, Player player) {
        shopItems.forEach(si -> {
            if (!(si instanceof BedwarsShopItem)) {
                return;
            }
            BedwarsShopItem shopItem = (BedwarsShopItem)si;
            if (shopItem.slot == slot) {
                if (player.getInventory().contains(shopItem.price.getType(), shopItem.price.getAmount())) {
                    if (!shopItem.allowMultiple && shopItem.alreadyPurchased()) {
                        playFailSound(player);
                        player.sendMessage(prefixedMessage(ChatColor.RED+"You have already purchased this item!"));
                        return;
                    }
                    if (shopItem.removeItem != null) {
                        player.getInventory().remove(shopItem.removeItem.getType());
                    }
                    if (shopItem.giveItem) {
                            player.getInventory().addItem(shopItem.getInventoryItemStack());
                    }

                    BedwarsPlayerData playerData = (BedwarsPlayerData) game.getPlayerData(player);
                    BedwarsTeamData teamData = (BedwarsTeamData) game.getTeamData(playerData.team);

                    switch (shopItem.id) {
                        case "pickaxe":
                            if (playerData.pickaxeLevel < 3) {
                                playerData.pickaxeLevel++;
                            }
                            break;
                        case "shears":
                            playerData.hasShears = true;
                            break;
                        case "armor":
                            if (playerData.armorLevel < 2) playerData.armorLevel++;
                            game.setArmor(player);
                            break;
                        case "sharpness":
                            teamData.sharpnessLevel++;
                            break;
                        case "protection":
                            teamData.protectionLevel++;
                            for (Team team : game.teams) {
                                for (Player teamMember : game.getTeamMembers(team)) {
                                    game.setArmor(teamMember);
                                }
                            }
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
                if (title.equals("Item Shop")) {
                    game.openShop(player);
                } else {
                    game.openUpgrades(player);
                }

                //initShop(player);
            }
        });
    }
}
