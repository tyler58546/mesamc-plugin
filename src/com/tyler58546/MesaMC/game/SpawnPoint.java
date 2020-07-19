package com.tyler58546.MesaMC.game;

import org.bukkit.util.Vector;

/** Represents a spawnpoint in a map. */
public class SpawnPoint {
    /** The team the spawnpoint belongs to */
    public Team team;

    /** The location of the spawnpoint */
    public Vector location;

    /** The spawnpoint's numerical ID */
    public Integer id;

    public SpawnPoint(Team team, Vector location, Integer id) {
        this.team = team;
        this.location = location;
        this.id = id;
    }
}
