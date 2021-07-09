package com.tyler58546.MesaMC.hub;

import com.tyler58546.MesaMC.MesaMC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HubGUIItem extends ItemStack {
    public int slot;
    public Player player;
    public HubGUIClickActions clickActions;

    public HubGUIItem(Material m, String name, String[] description, int slot, HubGUIClickActions clickActions) {
        super(m);
        this.slot = slot;
        this.clickActions = clickActions;
        List<String> lore = new ArrayList<String>();
        if (description != null) {
            for (String line : description) {
                lore.add(ChatColor.GRAY+line);
            }
        }
        if (clickActions.getLeftClickAction() != null || clickActions.getRightClickAction() != null) {
            lore.add(" ");
        }
        if (clickActions.getLeftClickAction() != null) {
            lore.add(ChatColor.GREEN+"> Left click to "+clickActions.getLeftClickAction());
        }
        if (clickActions.getRightClickAction() != null) {
            lore.add(ChatColor.GREEN+"> Right click to "+clickActions.getRightClickAction());
        }
        ItemMeta im = getItemMeta();
        im.setDisplayName(ChatColor.BLUE+name);
        im.setLore(lore);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        setItemMeta(im);
    }
    public static HubGUIItem createHubGUICommandItem(Material m, String name, String description, int slot, String leftClickCommand, String leftClickAction) {
        String[] splitDesc = new String[]{};
        if (description != null) {
            splitDesc = description.split("-n-");
        }
        return new HubGUIItem(m, name, splitDesc, slot, new HubGUIClickActions() {
            @Override
            public String getLeftClickAction() {
                return leftClickAction;
            }

            @Override
            public String getRightClickAction() {
                return null;
            }

            @Override
            public void onLeftClick(Player player) {
                Bukkit.getServer().dispatchCommand(player, leftClickCommand);
//                player.closeInventory();
            }

            @Override
            public void onRightClick(Player player) {

            }
        });
    }
    public static HubGUIItem createHubGUIMessageItem(Material m, String name, String description, int slot, String leftClickMessage, String leftClickAction) {
        String[] splitDesc = new String[]{};
        if (description != null) {
            splitDesc = description.split("-n-");
        }
        return new HubGUIItem(m, name, splitDesc, slot, new HubGUIClickActions() {
            @Override
            public String getLeftClickAction() {
                return leftClickAction;
            }

            @Override
            public String getRightClickAction() {
                return null;
            }

            @Override
            public void onLeftClick(Player player) {
                player.sendMessage(leftClickMessage);
            }

            @Override
            public void onRightClick(Player player) {

            }
        });
    }

    public static HubGUIItem createHubGUIServerItem(MesaMC main, Material m, String name, String description, int slot, String leftClickServer, String leftClickAction) {
        String[] splitDesc = new String[]{};
        if (description != null) {
            splitDesc = description.split("-n-");
        }
        return new HubGUIItem(m, name, splitDesc, slot, new HubGUIClickActions() {
            @Override
            public String getLeftClickAction() {
                return leftClickAction;
            }

            @Override
            public String getRightClickAction() {
                return null;
            }

            @Override
            public void onLeftClick(Player player) {
                main.sendPlayerToServer(player, leftClickServer);
            }

            @Override
            public void onRightClick(Player player) {

            }
        });
    }
}
