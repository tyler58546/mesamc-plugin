package com.tyler58546.MesaMC.game.event;

import com.tyler58546.MesaMC.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class GameUpdateScoreboardEvent extends Event {

    public Game game;
    public Player player;
    public List<String> lines;
    public String title = ChatColor.BOLD+"MesaMC";

    public GameUpdateScoreboardEvent(Game game, Player player, List<String> lines) {
        this.game = game;
        this.player = player;
        this.lines = lines;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
