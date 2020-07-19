package com.tyler58546.MesaMC.game;

import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Represents a map for a game. */
public class GameMap {
    public GameType gameType;
    public String id;
    public String displayName;
    public String author;
    public Boolean published;
    public List<SpawnPoint> spawnPoints;
    public World world;

    private HashMap<Team, Integer> currentSpawn = new HashMap<Team, Integer>();

    /**
     * @param gameType The game type the map is for.
     * @param id Alphanumeric map id.
     * @param displayName Map display name.
     * @param author Map author.
     * @param published Whether the map is published.
     * @param spawnPoints A list of the maps spawnpoints.
     * @param world The maps world.
     */
    public GameMap(GameType gameType,
                   String id,
                   String displayName,
                   String author,
                   Boolean published,
                   List<SpawnPoint> spawnPoints,
                   World world) {
        this.gameType = gameType;
        this.id = id;
        this.displayName = displayName;
        this.author = author;
        this.published = published;
        this.spawnPoints = spawnPoints;
        this.world = world;

    }

    /**
     * Gets a teams spawnpoints
     * @return A list of the teams spawnpoints.
     */
    public List<SpawnPoint> getTeamSpawnPoints(Team team) {
        List<SpawnPoint> output = new ArrayList<SpawnPoint>();
        spawnPoints.forEach(spawnpoint -> {
            if (spawnpoint.team == team) output.add(spawnpoint);
        });
        return output;
    }

    /**
     * Gets the next spawnpoint for a given team.
     * @param team The team of the spawnpoint.
     */
    public SpawnPoint getNextSpawnpoint(Team team) {
        List<SpawnPoint> teamSpawns = getTeamSpawnPoints(team);
        if (teamSpawns.size() == 0) {
            teamSpawns.add(new SpawnPoint(team, new Vector(0.5,10,0.5), 0));
        }
        Integer spawnpointIndex = currentSpawn.get(team);
        if (spawnpointIndex == null) {
            spawnpointIndex = 0;
            currentSpawn.put(team, 0);
        }
        if (spawnpointIndex+1 >= teamSpawns.size()) {
            currentSpawn.put(team, 0);
        } else {
            currentSpawn.put(team, spawnpointIndex+1);
        }
        return teamSpawns.get(spawnpointIndex);
    }
}
