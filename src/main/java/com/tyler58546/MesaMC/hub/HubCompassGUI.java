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

//        items.add(HubGUIItem.createHubGUIMessageItem(Material.IRON_PICKAXE, "Survival", "1.17 survival with minimal plugins.", 37, ChatColor.BLUE+"Portal> "+ChatColor.GRAY+"To play survival, type "+ChatColor.YELLOW+"/server mmc117", "play"));
        items.add(HubGUIItem.createHubGUIServerItem(main, Material.IRON_PICKAXE, "Survival", "1.18 survival with minimal plugins.", 39, "mmc118", "join"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.BOW, "One in the Quiver", "Bows insta-kill-n-You get one arrow every time you get a kill.-n-First to 20 kills wins the game.", 40, "play Quiver", "play"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.IRON_SWORD, "Duels "+ChatColor.GRAY+"(1v1)", "Fight to the death!-n-Defeat your opponent to win.-n-Attack cooldowns are disabled.", 41, "play Duels1v1", "play"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.ENDER_EYE, "Skywars "+ChatColor.GRAY+"(Solo)", "Each player starts on their own floating island.-n-Collect loot from chests and fight other players.-n-Last person standing wins.", 48, "play SkywarsSolo", "play"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.RED_BED, "Bedwars", "You can respawn until your bed is broken.-n-Purchase blocks to protect your bed.-n-Destroy your opponent's bed and kill them to win.", 49, "play Bedwars", "play"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.COOKIE, "Slaparoo", "Use your cookie to slap other players off the map.-n-You get 1 point for every kill.-n-First to 20 points wins the game.", 50, "play Slaparoo", "play"));

        return items;
    }

    @Override
    protected ItemStack getDefaultItem(int slot) {
        ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        Hub.renameItem(whiteGlass, " ");
        ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        Hub.renameItem(blackGlass, " ");
        if (slot < 9*3) return whiteGlass;
        //else if (slot >= 9*4 && slot < 9*5) return null;
        else return blackGlass;
    }
}
