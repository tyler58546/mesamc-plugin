package com.tyler58546.MesaMC;

import com.tyler58546.MesaMC.game.*;
import com.tyler58546.MesaMC.game.games.Duels1v1;
import com.tyler58546.MesaMC.game.games.Quiver;
import com.tyler58546.MesaMC.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class MesaMC extends JavaPlugin {

    public static String mainWorldName = "hthm";
    public static String serverName = "MesaMC";

    public ArrayList<Game> games = new ArrayList<Game>();
    public ArrayList<GameMap> editorMaps = new ArrayList<GameMap>();
    public World defaultWorld;
    GameCommands cmdHandler;
    MapCommands mapCmdHandler;

    @Override
    public void onEnable() {
        cmdHandler = new GameCommands(this);
        mapCmdHandler = new MapCommands(this);

        this.getCommand("play").setExecutor(cmdHandler);
        this.getCommand("play").setTabCompleter(cmdHandler);
        this.getCommand("start").setExecutor(cmdHandler);
        this.getCommand("start").setTabCompleter(cmdHandler);
        this.getCommand("leave").setExecutor(cmdHandler);
        this.getCommand("leave").setTabCompleter(cmdHandler);

        this.getCommand("map").setExecutor(mapCmdHandler);
        this.getCommand("map").setTabCompleter(mapCmdHandler);

        Bukkit.getServer().getPluginManager().registerEvents(new Hub(this), this);

        MesaMC main = this;
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
            public void run(){
                defaultWorld = Bukkit.getWorld(mainWorldName);
                //Add quiver game
                games.add(new Quiver(main));

                //Add duels game
                games.add(new Duels1v1(main));
            }
        });
    }

    public void onDisable() {
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

    public void executeCommandAsPlayer(Player player, String cmd) {
        Bukkit.getServer().dispatchCommand(player, cmd);
    }

}
