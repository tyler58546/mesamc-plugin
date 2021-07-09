package com.tyler58546.MesaMC.game;

import com.tyler58546.MesaMC.game.stats.Statistic;

public enum GameType {
    LOBBY ("Lobby", "Lobby", new Statistic[]{}),
    QUIVER (
            "Quiver",
            "One in the Quiver",
            new Statistic[]{
                Statistic.GAMES_PLAYED,
                Statistic.KILLS,
                Statistic.DEATHS,
                Statistic.WINS,
                Statistic.LOSSES,
                Statistic.ARROWS_SHOT,
                Statistic.DAMAGE_DEALT,
                Statistic.DAMAGE_TAKEN
            }),
    DUELS (
            "Duels",
            "Duels",
            new Statistic[]{
                Statistic.GAMES_PLAYED,
                Statistic.WINS,
                Statistic.LOSSES,
                Statistic.DAMAGE_DEALT,
                Statistic.DAMAGE_TAKEN
    }),
    SKYWARS (
            "Skywars",
            "Skywars",
            new Statistic[]{
                Statistic.GAMES_PLAYED,
                Statistic.KILLS,
                Statistic.DEATHS,
                Statistic.WINS,
                Statistic.LOSSES,
                Statistic.DAMAGE_DEALT,
                Statistic.DAMAGE_TAKEN
    }),
    BEDWARS (
            "Bedwars",
            "Bedwars",
            new Statistic[]{
                Statistic.GAMES_PLAYED,
                Statistic.KILLS,
                Statistic.DEATHS,
                Statistic.WINS,
                Statistic.LOSSES,
                Statistic.DAMAGE_DEALT,
                Statistic.DAMAGE_TAKEN
    })
    ;

    public String id;
    public String name;
    public Statistic[] stats;

    private GameType(String id, String name, Statistic[] stats) {
        this.id = id;
        this.name = name;
        this.stats = stats;
    }
}
