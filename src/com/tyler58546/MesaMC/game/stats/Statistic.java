package com.tyler58546.MesaMC.game.stats;

public enum Statistic {
    KILLS ("Kills", null),
    DEATHS ("Deaths", null),
    ARROWS_SHOT ("Arrows Shot", null),
    DAMAGE_TAKEN ("Damage Taken", " HP"),
    DAMAGE_DEALT ("Damage Dealt", " HP"),
    WINS ("Wins", null),
    LOSSES ("Losses", null),
    GAMES_PLAYED ("Games Played", null)
    ;
    String displayName;
    String suffix;
    Statistic(String displayName, String suffix) {
        this.displayName = displayName;
        this.suffix = suffix;
    }
}
