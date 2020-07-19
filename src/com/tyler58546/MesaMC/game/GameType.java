package com.tyler58546.MesaMC.game;

public enum GameType {
    LOBBY ("Lobby", "Lobby"),
    QUIVER ("Quiver", "One in the Quiver"),
    DUELS ("Duels", "Duels")
    ;

    public String id;
    public String name;

    private GameType(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
