package com.tyler58546.MesaMC.game.event;

import com.tyler58546.MesaMC.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameMapLoadEvent extends Event {
    public Game game;

    public GameMapLoadEvent(Game game) {
        this.game = game;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
