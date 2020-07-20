package com.tyler58546.MesaMC.game.stats;

import com.tyler58546.MesaMC.game.GameType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StatsItem extends ItemStack {
    public GameType game;
    public Player player;

    public StatsItem(Material m, GameType game, Player player, StatisticsManager statisticsManager) {
        super(m);
        this.game = game;
        this.player = player;
        ItemMeta im = getItemMeta();
        if (game != null) im.setDisplayName(ChatColor.BLUE+game.name);
        else im.setDisplayName(ChatColor.BLUE+"Global Stats");
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        setItemMeta(im);
        List<String> lore = new ArrayList<String>();
        lore.add(" ");
        Statistic[] statList = new Statistic[]{
                Statistic.GAMES_PLAYED,
                Statistic.KILLS,
                Statistic.DEATHS,
                Statistic.WINS,
                Statistic.LOSSES,
                Statistic.DAMAGE_DEALT,
                Statistic.DAMAGE_TAKEN
        };
        if (game != null) statList = game.stats;
        for (Statistic stat : statList) {
            if (stat.suffix == null) stat.suffix = "";
            if (game != null) lore.add(ChatColor.WHITE+stat.displayName+": "+ChatColor.YELLOW+statisticsManager.getGameStatistic(game, stat, player)+stat.suffix);
            else lore.add(ChatColor.WHITE+stat.displayName+": "+ChatColor.YELLOW+statisticsManager.getGlobalStatistic(stat, player)+stat.suffix);
        }
        setLore(lore);
    }
}
