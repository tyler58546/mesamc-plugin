package com.tyler58546.MesaMC.game.event;

import com.tyler58546.MesaMC.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameDeathEvent extends Event {
    public Game game;
    public Player killed;
    public boolean isFinal;

    public GameDeathEvent(Game game, Player killed, boolean isFinal) {
        this.game = game;
        this.killed = killed;
        this.isFinal = isFinal;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
