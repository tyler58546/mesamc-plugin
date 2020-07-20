package com.tyler58546.MesaMC.game.stats;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.GameType;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsManager {
    MesaMC main;
    YamlConfiguration statsFile;
    static String filePath = "plugins/MesaMC/stats.yml";
    public StatisticsManager(MesaMC main) {
        this.main = main;
        statsFile = YamlConfiguration.loadConfiguration(new File(filePath));
    }

    /**
     * Saves stats to disk.
     */
    public void saveStats() {
        try {
            statsFile.save(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getGameStatistic(GameType game, Statistic type, OfflinePlayer player) {
        return statsFile.getInt(player.getUniqueId()+"."+game.toString()+"."+type.toString());
    }

    public void setGameStatistic(GameType game, Statistic type, OfflinePlayer player, int value) {
        statsFile.set(player.getUniqueId()+"."+game.toString()+"."+type.toString(), value);
    }

    /**
     * Adds 1 to to a statistic
     * @param game The game type of the statistic
     * @param type The statistic type
     * @param player The player
     */
    public void addGameStatistic(GameType game, Statistic type, OfflinePlayer player) {
        setGameStatistic(game, type, player, getGameStatistic(game, type, player)+1);
    }

    public int getGlobalStatistic(Statistic type, OfflinePlayer player) {
        AtomicInteger output = new AtomicInteger();
        try {
            statsFile.getConfigurationSection(player.getUniqueId().toString()).getKeys(false).forEach(key -> {
                statsFile.getConfigurationSection(player.getUniqueId()+"."+key).getKeys(false).forEach(stat -> {
                    if (Statistic.valueOf(stat) == type) output.addAndGet(statsFile.getInt(player.getUniqueId() + "." + key + "." + stat));
                });
            });
        } catch (NullPointerException e) {
            return 0;
        }

        return output.get();
    }
}
