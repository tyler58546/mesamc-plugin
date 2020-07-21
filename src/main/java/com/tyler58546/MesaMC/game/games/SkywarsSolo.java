package com.tyler58546.MesaMC.game.games;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.SpawnPoint;
import com.tyler58546.MesaMC.game.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SkywarsSolo extends Skywars {
    public SkywarsSolo(MesaMC main) {
        super(main, "SkywarsSolo", "Skywars Solo", new String[]{"skywars-temple"});
        description.add("Each player starts on their own floating island.");
        description.add("Collect loot from chests and fight other players.");
        description.add("Last person standing wins.");
    }
    @Override
    protected Location getSpawnpoint(Player player) {
        SpawnPoint spawnPoint = gameMap.getNextSpawnpoint(Team.PLAYERS);
        return spawnPoint.location.toLocation(gameWorld);
    }
}
