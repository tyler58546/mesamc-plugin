package com.tyler58546.MesaMC.game;

import org.bukkit.ChatColor;

public enum Team {
    PLAYERS (ChatColor.YELLOW+"Players"),
    SPECTATORS (ChatColor.ITALIC+"Spectators"+ChatColor.RESET),
    CUSTOM (ChatColor.ITALIC+"Custom Data"+ChatColor.RESET),
    RED (ChatColor.RED+"Red"),
    BLUE (ChatColor.BLUE+"Blue");
    ;

    public String displayName;
    Team(String displayName) {
        this.displayName = displayName;
    }
}
