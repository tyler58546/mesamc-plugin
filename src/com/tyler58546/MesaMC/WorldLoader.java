package com.tyler58546.MesaMC;

import com.tyler58546.MesaMC.game.GameMap;
import com.tyler58546.MesaMC.game.GameType;
import com.tyler58546.MesaMC.game.SpawnPoint;
import com.tyler58546.MesaMC.game.Team;
import com.tyler58546.MesaMC.util.ZipUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldLoader {
    public static World createWorld(String name) throws IOException {


        /*if (source != null) {
            UnzipUtility unzipUtility = new UnzipUtility();
            try {
                unzipUtility.unzip("maps/"+source, name);
            } catch (IOException e) {
                throw e;
            }
        }*/
        WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.type(WorldType.FLAT)
                .environment(World.Environment.NORMAL)
                .generateStructures(false);
        World newWorld = worldCreator.createWorld();
        //newWorld.setAutoSave(false);
        newWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        newWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        newWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        newWorld.setGameRule(GameRule.DO_MOB_LOOT, false);
        newWorld.setGameRule(GameRule.DO_FIRE_TICK, false);
        return newWorld;

    }

    /**
     * Loads a map from a zip file.
     * @param mapID The ID of the map to load.
     * @param name The name of the created world.
     * @return The loaded map.
     */
    public static GameMap loadMap(String mapID, String name) {
        GameMap output = new GameMap(null, mapID, null, null, false, null, null);
        World og  = Bukkit.getServer().getWorld(name);
        if (og != null) {
            Bukkit.unloadWorld(og, false);
            deleteFolder(new File(name));
        }
        UnzipUtility unzipUtility = new UnzipUtility();
        unzipUtility.unzip("maps/"+mapID+".zip", name);
        YamlConfiguration mapData = YamlConfiguration.loadConfiguration(new File(name+"/map.yml"));
        String gt = mapData.getString("game");
        if (gt != null) {
            output.gameType = GameType.valueOf(gt);
        }
        output.displayName = mapData.getString("name");
        output.author = mapData.getString("author");
        output.published = mapData.getBoolean("published");
        output.spawnPoints = new ArrayList<SpawnPoint>();
        try {
            for (String team : mapData.getConfigurationSection("spawnpoints").getKeys(false)) {
                for (String i : mapData.getConfigurationSection("spawnpoints."+team).getKeys(false)) {
                    output.spawnPoints.add(new SpawnPoint(Team.valueOf(team),  mapData.getVector("spawnpoints."+team+"."+i), Integer.parseInt(i)));
                }
            }
        } catch (NullPointerException e) {
            Bukkit.getServer().getLogger().warning("Map "+mapID+" does not have any spawnpoints defined.");
        }

        try {
            output.world = createWorld(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Saves a maps region files and metadata to a zip file.
     * @param map The map to save.
     * @throws IOException
     */
    public static void saveMap(GameMap map) throws IOException {
        if (map.world == null) {
            throw new FileNotFoundException("Map world is not loaded");
        }
        YamlConfiguration mapData = YamlConfiguration.loadConfiguration(new File(map.world.getName()+"/map.yml"));
        if (map.gameType != null) {
            mapData.set("game", map.gameType.toString());
        }
        mapData.set("name", map.displayName);
        mapData.set("author", map.author);
        mapData.set("published", map.published);

        map.spawnPoints.forEach(spawnPoint -> {
            mapData.set("spawnpoints."+spawnPoint.team.toString()+"."+spawnPoint.id, spawnPoint.location);
        });

        mapData.save(new File(map.world.getName()+"/map.yml"));

        ZipUtils.zipWorld(map.world.getName(), map.id+".zip");
    }
    public ArrayList<GameMap> getMaps() {
        String[] maps = new File("maps").list();
        ArrayList<GameMap> output = new ArrayList<GameMap>();
        for (String map:maps) {
            output.add(new GameMap(null, map.replace(".zip",""), null, null, null, null, null));
        }
        return output;
    }

    static void deleteFolder(File file){
        for (File subFile : file.listFiles()) {
            if(subFile.isDirectory()) {
                deleteFolder(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }
    public static void unloadWorld(World world) {
        String worldName = world.getName();
        Bukkit.unloadWorld(world, false);
        deleteFolder(new File(worldName));
    }
}
