package com.tyler58546.MesaMC.hub;

import com.tyler58546.MesaMC.MesaMC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HubCompassGUI extends HubGUI {
    public HubCompassGUI(MesaMC main, Player player) {
        super(main, "Quick Compass", 6, getItems(main), player);
    }
    static List<HubGUIItem> getItems(MesaMC main) {
        List<HubGUIItem> items = new ArrayList<HubGUIItem>();
        items.add(HubGUIItem.createHubGUICommandItem(Material.IRON_BARS, "Playground", null, 3, "warp playground", "teleport"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.GRAY_CONCRETE_POWDER, "North Parking Lot", null, 4, "warp nparking", "teleport"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.PLAYER_HEAD, "Theater", null, 5, "warp theater", "teleport"));

        items.add(HubGUIItem.createHubGUICommandItem(Material.BOOK, "High Tech Elementary Mesa", null, 12, "warp htem", "teleport"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.NETHER_STAR, "High Tech High Mesa", null, 13, "spawn", "teleport"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.GREEN_CONCRETE_POWDER, "HTHM Grass Field", null, 14, "warp hthmfield", "teleport"));

        items.add(HubGUIItem.createHubGUICommandItem(Material.BOOKSHELF, "High Tech Middle Mesa", null, 21, "warp htmm", "teleport"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.GRASS_BLOCK, "Field", null, 22, "warp field", "teleport"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.MAGMA_CREAM, "Gym", null, 23, "warp gym", "teleport"));

        items.add(HubGUIItem.createHubGUIMessageItem(Material.IRON_PICKAXE, "Survival", "1.16 anarchy survival with minimal plugins.", 38, ChatColor.BLUE+"Portal> "+ChatColor.GRAY+"To play survival, type "+ChatColor.YELLOW+"/server mmc116", "play"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.BOW, "One in the Quiver", "Bows insta-kill-n-You get one arrow every time you get a kill.-n-First to 20 kills wins the game.", 40, "play Quiver", "play"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.IRON_SWORD, "Duels 1v1", "Fight to the death!-n-Defeat your opponent to win.-n-Attack cooldowns are disabled.", 42, "play Duels1v1", "play"));

        return items;
    }

    @Override
    protected ItemStack getDefaultItem(int slot) {
        ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        Hub.renameItem(whiteGlass, " ");
        ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        Hub.renameItem(blackGlass, " ");
        if (slot < 9*3) return whiteGlass;
        else if (slot >= 9*4 && slot < 9*5) return null;
        else return blackGlass;
    }
}
