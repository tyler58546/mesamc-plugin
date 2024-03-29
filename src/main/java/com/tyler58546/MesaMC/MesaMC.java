package com.tyler58546.MesaMC;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.tyler58546.MesaMC.game.*;
import com.tyler58546.MesaMC.game.games.*;
import com.tyler58546.MesaMC.game.stats.StatisticsManager;
import com.tyler58546.MesaMC.game.stats.StatsCommand;
import com.tyler58546.MesaMC.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class MesaMC extends JavaPlugin implements PluginMessageListener {

    public static String mainWorldName = "hthm";
    public static String serverName = "MesaMC";

    public StatisticsManager statisticsManager;
    public ArrayList<Game> games = new ArrayList<Game>();
    public ArrayList<GameMap> editorMaps = new ArrayList<GameMap>();
    public World defaultWorld;
    GameCommands cmdHandler;
    MapCommands mapCmdHandler;
    StatsCommand statsCmdHandler;

    @Override
    public void onEnable() {
        this.statisticsManager = new StatisticsManager(this);
        cmdHandler = new GameCommands(this);
        mapCmdHandler = new MapCommands(this);
        statsCmdHandler = new StatsCommand(this);

        this.getCommand("play").setExecutor(cmdHandler);
        this.getCommand("play").setTabCompleter(cmdHandler);

        this.getCommand("start").setExecutor(cmdHandler);
        this.getCommand("start").setTabCompleter(cmdHandler);

        this.getCommand("team").setExecutor(cmdHandler);
        this.getCommand("team").setTabCompleter(cmdHandler);

        this.getCommand("leave").setExecutor(cmdHandler);
        this.getCommand("leave").setTabCompleter(cmdHandler);

        this.getCommand("map").setExecutor(mapCmdHandler);
        this.getCommand("map").setTabCompleter(mapCmdHandler);

        this.getCommand("statistics").setExecutor(statsCmdHandler);
        this.getCommand("statistics").setTabCompleter(statsCmdHandler);

        Bukkit.getServer().getPluginManager().registerEvents(new Hub(this), this);

        MesaMC main = this;

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
            public void run(){
                defaultWorld = Bukkit.getWorld(mainWorldName);
                //Add quiver game
                games.add(new Quiver(main));

                //Add duels game
                games.add(new Duels1v1(main));

                //Add skywars game
                games.add(new SkywarsSolo(main));

                //Add bedwars game
                games.add(new Bedwars(main));

                //Add slaparoo game
//                games.add(new Slaparoo(main));

                //Add br game
                games.add(new BattleRoyale(main));
            }
        });
        new BukkitRunnable() {
            @Override
            public void run() {
                updateHidden();
            }
        }.runTaskTimer(this, 20, 20);
    }

    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        statisticsManager.saveStats();
        games.forEach(game -> {
            game.shutdown();
        });
        editorMaps.forEach(map -> {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                if (player.getWorld() == map.world) player.teleport(defaultWorld.getSpawnLocation());
            });
            WorldLoader.unloadWorld(map.world);
        });
    }

    public void updateHidden() {
        Bukkit.getServer().getOnlinePlayers().forEach(p1 -> {
            Bukkit.getServer().getOnlinePlayers().forEach(p2 -> {
                if (p1 != p2) {
                    if (shouldBeHidden(p1, p2)) {
                        p2.hidePlayer(this, p1);
                    } else {
                        p2.showPlayer(this, p1);
                    }
                }
            });
        });
    }

    /**
     * Gets a players current game.
     * @param player
     * @return The game the player is in, or null of they are not in a game.
     */
    public Game getCurrentGame(Player player) {
        for (Game g : games) {
            if (g.players.contains(player)) {
                return g;
            }
        }
        return null;
    }

    Boolean shouldBeHidden(Player p, Player from) {
        if (getCurrentGame(from) == null) return false;
        if (getCurrentGame(from) != getCurrentGame(p)) return true;
        if (getCurrentGame(from).spectators.contains(from)) return false;
        if (getCurrentGame(from).spectators.contains(p)) return true;
        return false;
    }

    public void executeCommandAsPlayer(Player player, String cmd) {
        Bukkit.getServer().dispatchCommand(player, cmd);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) { }

    public void sendPlayerToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
}
