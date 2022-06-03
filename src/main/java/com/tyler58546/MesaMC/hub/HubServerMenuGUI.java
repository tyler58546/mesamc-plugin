package com.tyler58546.MesaMC.hub;

import com.tyler58546.MesaMC.MesaMC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HubServerMenuGUI extends HubGUI {
    public HubServerMenuGUI(MesaMC main, Player player) {
        super(main, "MesaMC Menu", 3, getItems(main), player);
    }
    static List<HubGUIItem> getItems(MesaMC main) {
        List<HubGUIItem> output = new ArrayList<HubGUIItem>();
        output.add(HubGUIItem.createHubGUIMessageItem(Material.DIAMOND, "Discord", "Join us on Discord!", 13, ChatColor.WHITE+""+ChatColor.BOLD+"JOIN US ON DISCORD > "+ChatColor.BLUE+""+ChatColor.UNDERLINE+"https://discord.gg/4EWS6zs7Fp", "get invite link"));
        return output;
    }

    @Override
    protected ItemStack getDefaultItem(int slot) {
        return null;
    }
}
