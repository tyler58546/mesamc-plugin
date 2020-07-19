package com.tyler58546.MesaMC.game.event;

import com.tyler58546.MesaMC.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveGameEvent extends Event {

    public Game game;
    public Player player;

    public PlayerLeaveGameEvent(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
