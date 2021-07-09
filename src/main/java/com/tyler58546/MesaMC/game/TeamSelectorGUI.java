package com.tyler58546.MesaMC.game;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.hub.Hub;
import com.tyler58546.MesaMC.hub.HubGUI;
import com.tyler58546.MesaMC.hub.HubGUIItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TeamSelectorGUI extends HubGUI {
    public TeamSelectorGUI(MesaMC main, Player player) {
        super(main, "Team Selector", 3, getItems(main), player);
    }

    static List<HubGUIItem> getItems(MesaMC main) {
        List<HubGUIItem> items = new ArrayList<HubGUIItem>();
        items.add(HubGUIItem.createHubGUICommandItem(Material.RED_WOOL, "Red Team", null, 12, "team red", "join"));
        items.add(HubGUIItem.createHubGUICommandItem(Material.BLUE_WOOL, "Blue Team", null, 14, "team blue", "join"));

        return items;
    }

    @Override
    protected ItemStack getDefaultItem(int slot) {
        return new ItemStack(Material.AIR);
    }
}
