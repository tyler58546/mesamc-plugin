package com.tyler58546.MesaMC.game;

import org.bukkit.entity.Player;

public class Winner {
    public Player player;
    public Team team;

    public Winner(Player player) {
        this.player = player;
    }
    public Winner(Team team) {
        this.team = team;
    }
}
