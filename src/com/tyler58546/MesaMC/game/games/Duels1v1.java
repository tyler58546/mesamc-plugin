package com.tyler58546.MesaMC.game.games;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.SpawnPoint;
import com.tyler58546.MesaMC.game.Team;
import com.tyler58546.MesaMC.game.Winner;
import com.tyler58546.MesaMC.game.event.GameCombatKillEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Duels1v1 extends Duels {
    public Duels1v1(MesaMC main) {
        super(main, "Duels1v1", "Duels 1v1", new String[]{"duels1"});
        maxPlayers = 2;
        description.add("Fight to the death!");
        description.add("Defeat your opponent to win.");
        description.add("Attack cooldowns are disabled.");
    }

    @Override
    protected Location getSpawnpoint(Player player) {
        SpawnPoint spawnPoint = gameMap.getNextSpawnpoint(Team.PLAYERS);
        if (spawnPoint.id == 0) return spawnPoint.location.toLocation(gameWorld);
        else {
            Location loc = spawnPoint.location.toLocation(gameWorld);
            loc.setYaw(180);
            return loc;
        }
    }

    @Override
    protected void handleDeath(Player player, String deathMessage) {
        gameBroadcast(deathMessage);
        player.setGameMode(GameMode.SPECTATOR);
        players.forEach(p -> {
            if (p != player) {
                winner = new Winner(p);
                stop();
            }
        });
    }

}
