package com.tyler58546.MesaMC.game;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.WorldLoader;
import com.tyler58546.MesaMC.util.ZipUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapCommands implements CommandExecutor, TabCompleter {

    MesaMC main;

    public MapCommands(MesaMC main) {
        this.main = main;
    }

    String prefixedMessage(String msg) {
        return ChatColor.BLUE+"Map Editor> "+ChatColor.GRAY+msg;
    }

    GameMap getEditorMap(String id) {
        for (GameMap m : main.editorMaps) {
            if (m.id.equals(id)) return  m;
        }
        return null;
    }
    World getEditorWorld(String id) {
        GameMap map = getEditorMap(id);
        if (map != null) {
            return map.world;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefixedMessage("You must be a player to use this command."));
            return false;
        }
        Player player = (Player) sender;

        GameMap currentMap = getEditorMap(player.getWorld().getName().replace("editor_", ""));
        World currentWorld = null;
        if (currentMap != null) currentWorld = currentMap.world;
        switch (args[0]) {
            case "create":
                if (args.length != 2) return false;
                sender.sendMessage(prefixedMessage("Creating map..."));
                World newWorld;
                try {
                    newWorld = WorldLoader.createWorld("editor_"+args[1]);
                    GameMap newMap = new GameMap(null, args[1], null, null, false, null, newWorld);
                    main.editorMaps.add(newMap);
                    player.teleport(new Location(newWorld, 0.5, 10, 0.5));
                    player.setGameMode(GameMode.CREATIVE);
                    sender.sendMessage(prefixedMessage("You are now editing "+ChatColor.YELLOW+args[1]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case "edit":
                if (args.length != 2) return false;
                World og = getEditorWorld(args[1]);
                if (og != null) {
                    player.teleport(new Location(og, 0.5, 10, 0.5));
                } else {
                    String[] allMaps = new File("maps").list();
                    List<String> mapsList = Arrays.asList(allMaps);
                    if (!mapsList.contains(args[1]+".zip")) {
                        sender.sendMessage(prefixedMessage("Map not found."));
                        return true;
                    }
                    sender.sendMessage(prefixedMessage("Loading map..."));
                    GameMap loadedMap = WorldLoader.loadMap(args[1], "editor_"+args[1]);
                    main.editorMaps.add(loadedMap);
                    player.teleport(new Location(loadedMap.world, 0.5, 10, 0.5));
                    player.setGameMode(GameMode.CREATIVE);
                }
                sender.sendMessage(prefixedMessage("You are now editing "+ChatColor.YELLOW+args[1]));
                return true;
            case "save":
                if (args.length != 1) return false;
                if (currentWorld == null) {
                    sender.sendMessage(prefixedMessage("You must be editing a map to use this command."));
                    return true;
                }
                currentWorld.save();
                for (GameMap m : main.editorMaps) {
                    if (m.world == currentWorld) {
                        sender.sendMessage(prefixedMessage("Saving map..."));
                        try {
                            WorldLoader.saveMap(m);
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage(prefixedMessage("Map failed to save."));
                        }
                        sender.sendMessage(prefixedMessage("Map has been saved."));
                        return true;
                    }
                }
                return true;
            case "setauthor":
                if (args.length < 2) return false;
                if (currentWorld == null) {
                    sender.sendMessage(prefixedMessage("You must be editing a map to use this command."));
                    return true;
                }
                currentMap.author = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                sender.sendMessage(prefixedMessage("Map author changed to "+ChatColor.YELLOW+currentMap.author));
                return true;
            case "setname":
                if (args.length < 2) return false;
                if (currentWorld == null) {
                    sender.sendMessage(prefixedMessage("You must be editing a map to use this command."));
                    return true;
                }
                currentMap.displayName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                sender.sendMessage(prefixedMessage("Map name changed to "+ChatColor.YELLOW+currentMap.displayName));
                return true;
            case "setgame":
                if (args.length != 2) return false;
                if (currentWorld == null) {
                    sender.sendMessage(prefixedMessage("You must be editing a map to use this command."));
                    return true;
                }
                GameType newType = GameType.valueOf(args[1].toUpperCase());
                if (newType == null) {
                    sender.sendMessage(prefixedMessage("Invalid game type."));
                }
                currentMap.gameType = newType;
                sender.sendMessage(prefixedMessage("Game type changed to "+ChatColor.YELLOW+currentMap.gameType.name));
                return true;
            case "publish":
                if (args.length != 1) return false;
                if (currentWorld == null) {
                    sender.sendMessage(prefixedMessage("You must be editing a map to use this command."));
                    return true;
                }
                currentMap.published = true;
                sender.sendMessage(prefixedMessage("Map is now published. (use /map save to save)"));
                return true;
            case "unpublish":
                if (args.length != 1) return false;
                if (currentWorld == null) {
                    sender.sendMessage(prefixedMessage("You must be editing a map to use this command."));
                    return true;
                }
                currentMap.published = false;
                sender.sendMessage(prefixedMessage("Map is no longer published. (use /map save to save)"));
                return true;
            case "setspawn":
                if (args.length != 3) return  false;
                if (currentWorld == null) {
                    sender.sendMessage(prefixedMessage("You must be editing a map to use this command."));
                    return true;
                }
                Team spawnpointTeam = Team.valueOf(args[1].toUpperCase());
                if (spawnpointTeam == null) {
                    sender.sendMessage(prefixedMessage("Invalid team."));
                    return true;
                }
                Integer spawnpointID = Integer.parseInt(args[2]);
                if (spawnpointID == null) {
                    sender.sendMessage(prefixedMessage("Invalid spawnpoint ID."));
                    return true;
                }

                if (currentMap.spawnPoints == null) currentMap.spawnPoints = new ArrayList<SpawnPoint>();

                currentMap.spawnPoints.add(new SpawnPoint(spawnpointTeam, player.getLocation().toVector(), spawnpointID));
                sender.sendMessage(prefixedMessage("Updated spawnpoint "+ChatColor.YELLOW+"#"+spawnpointID+ChatColor.GRAY+" for team "+spawnpointTeam.displayName)+ChatColor.GRAY+" to "+player.getLocation().toVector().getX()+" "+player.getLocation().toVector().getY()+" "+player.getLocation().toVector().getZ());
                return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> output = new ArrayList<String>();
        if (args.length == 1) {
            output.add("create");
            output.add("edit");
            output.add("save");
            output.add("setauthor");
            output.add("setname");
            output.add("setgame");
            output.add("publish");
            output.add("unpublish");
            output.add("setspawn");
        }
        if (args.length == 2) {
            switch (args[0]) {
                case "edit":
                    String[] allMaps = new File("maps").list();
                    List<String> mapsList = Arrays.asList(allMaps);
                    mapsList.forEach(mapFilename -> {
                        if (mapFilename.endsWith(".zip")) output.add(mapFilename.replace(".zip", ""));
                    });
                    break;
                case "setgame":
                    for (GameType t: GameType.values()) {
                        output.add(t.toString());
                    }
                    break;
                default:
                    return output;
            }
        }
        ArrayList<String> realOutput = new ArrayList<String>();
        output.forEach(o -> {
            if (args.length > 0) {
                if (o.startsWith(args[args.length-1])) {
                    realOutput.add(o);
                }
            }
        });
        return realOutput;
    }
}
