package com.tyler58546.MesaMC.game.stats;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.CustomGUI;
import com.tyler58546.MesaMC.game.GameType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsGUI extends CustomGUI {

    public StatsGUI(MesaMC main, Player player) {
        super(main, player.getDisplayName(), 6, getItems(main, player));
    }

    static HashMap<Integer, ItemStack> getItems(MesaMC main, Player player) {
        HashMap<Integer, ItemStack> output = new HashMap<>();
        output.put(13, new StatsItem(Material.NETHER_STAR, null, player, main.statisticsManager));
        output.put(28, new StatsItem(Material.BOW, GameType.QUIVER, player, main.statisticsManager));
        output.put(30, new StatsItem(Material.IRON_SWORD, GameType.DUELS, player, main.statisticsManager));
        output.put(32, new StatsItem(Material.ENDER_EYE, GameType.SKYWARS, player, main.statisticsManager));
        output.put(34, new StatsItem(Material.RED_BED, GameType.BEDWARS, player, main.statisticsManager));
        return output;
    }
}
