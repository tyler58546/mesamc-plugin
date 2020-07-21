package com.tyler58546.MesaMC.game;

import com.tyler58546.MesaMC.MesaMC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameCommands implements CommandExecutor, TabCompleter {

    public MesaMC main;

    /**
     * Finds a game based on its ID
     * @param id The game ID to find
     * @return The game, or null if it does not exist.
     */
    Game findGame(String id) {
        for (Game g : main.games) {
            if (g.id.equalsIgnoreCase(id)) {
                return g;
            }
        }
        return null;
    }



    public GameCommands(MesaMC mainClass) {
        this.main = mainClass;
    }


    String prefixedMessage(String msg) {
        return ChatColor.BLUE+"Portal> "+ChatColor.GRAY+msg;
    }

    String gamePrefixedMessage(String msg) {
        return ChatColor.BLUE+"Game> "+ChatColor.GRAY+msg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player)sender;

        if (command.getName().equalsIgnoreCase("play")) {
            if (args.length != 1) {
                return false;
            }
            Game game = findGame(args[0]);
            if (game == null) {
                sender.sendMessage(prefixedMessage("Game not found."));
                return true;
            }
            if (game == main.getCurrentGame(player)) {
                sender.sendMessage(prefixedMessage("You are already in this game."));
                return true;
            }
            if (game.state != Game.gameState.WAITING) {
                if (game.state == Game.gameState.RUNNING) {
                    game.addSpectator(player, true);
                    sender.sendMessage(prefixedMessage("You are now spectating "+ChatColor.YELLOW+game.name));
                    return true;
                }
                sender.sendMessage(prefixedMessage("This game cannot be joined right now."));
                return true;
            }
            if (game.players.size() >= game.maxPlayers) {
                sender.sendMessage(prefixedMessage("This game is currently full."));
                return true;
            }
            sender.sendMessage(prefixedMessage("You joined "+ChatColor.YELLOW+game.name));
            game.addPlayer(player);
            return true;
        }
        if (command.getName().equalsIgnoreCase("start")) {
            Game currentGame = main.getCurrentGame(player);
            if (currentGame != null) {
                if (currentGame.state == Game.gameState.WAITING) {
                    if (currentGame.players.size() >= currentGame.minPlayers) {
                        currentGame.start();
                        return true;
                    } else if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("force")) currentGame.start();
                        return true;
                    } else {
                        sender.sendMessage(gamePrefixedMessage("This game needs "+ChatColor.YELLOW+currentGame.minPlayers+ChatColor.GRAY+" players to start."));
                        return true;
                    }
                } else {
                    sender.sendMessage(gamePrefixedMessage("The game cannot be started now."));
                }
            } else {
                sender.sendMessage(gamePrefixedMessage("You are not in a game."));
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("leave")) {
            Game currentGame = main.getCurrentGame(player);
            if (currentGame != null) {
                currentGame.removePlayer(player);
            } else {
                sender.sendMessage(gamePrefixedMessage("You are not in a game."));
            }
            return true;
        }


        return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("play")) {
            List<String> c = new ArrayList<String>();
            main.games.forEach(game -> {
                c.add(game.id);
            });
            return c;
        }
        return new ArrayList<String>();
    }
}
