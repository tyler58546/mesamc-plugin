package com.tyler58546.MesaMC.game.event;

import com.tyler58546.MesaMC.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class GameCombatKillEvent extends Event {

    public Game game;
    public Player killer;
    public Player killed;
    public String weapon;

    public GameCombatKillEvent(Game game, Player killer, Player killed, String weapon) {
        this.game = game;
        this.killer = killer;
        this.killed = killed;
        this.weapon = weapon;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}