package com.tyler58546.MesaMC.game;

import net.md_5.bungee.api.ChatMessageType;
import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.WorldLoader;
import com.tyler58546.MesaMC.game.event.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a minigame.
 */
public abstract class Game implements Listener {
    public String id;
    public String name;
    public String[] maps;
    public World gameWorld;
    public World lobbyWorld;
    public GameMap gameMap;
    public GameMap lobbyMap;
    public Location lobbySpawn;
    public ArrayList<Player> players = new ArrayList<Player>();
    public Player[] spectators;
    public Integer minPlayers = 2;
    public Integer maxPlayers = 16;
    public Integer respawnDelay = 5;
    protected abstract Boolean canRespawn(Player player);
    public Winner winner;
    public HashMap<String, Entity> npcs = new HashMap<String, Entity>();
    public List<Location> playerPlacedBlocks = new ArrayList<Location>();
    public ArrayList<String> description = new ArrayList<String>();
    public enum gameState {
        WAITING,
        STARTING,
        RUNNING,
        FINISHED
    }
    public gameState state = gameState.WAITING;

    public Boolean allowBreak = false;
    public Boolean allowPlace = false;
    public Boolean disableHunger = true;
    public Boolean allowInventoryMove = false;
    public GameMode gameMode = GameMode.SURVIVAL;

    private BukkitRunnable compassTimer;

    protected MesaMC main;

    protected Location getSpawnpoint(Player player) {
        return new Location(gameWorld, 0.5, 4, 0.5, 0, 0);
    }

    protected Game(MesaMC main, String id, String name, String[] maps) {
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
        this.main = main;
        this.id = id;
        this.name = name;
        this.maps = maps;

        //Create the lobby world
        Bukkit.getLogger().info("Loading worlds for "+id+"...");
        this.lobbyMap = WorldLoader.loadMap("lobby", id+"_lobby");
        this.lobbyWorld = lobbyMap.world;
        loadMap();


        this.lobbySpawn = new Location(lobbyWorld, 0.5, 4, 0.5, 0, 0);

        compassTimer = new BukkitRunnable() {
            @Override
            public void run() {
                players.forEach(p -> {
                    updateCompass(p);
                });
            }
        };
        compassTimer.runTaskTimer(main, 0, 20);
        updateScoreboard();
    }

    /**
     * Sends a message to players in the game.
     * @param message The message to send to players
     */
    public void gameBroadcast(String message) {
        players.forEach(p -> {
            p.sendMessage(message);
        });
    }

    /**
     * Sends a message to all players in the game prefixed with "Game> ".
     * @param message The message to send to players
     */
    public void gamePrefixedBroadcast(String message) {
        gameBroadcast(ChatColor.BLUE+"Game> "+ChatColor.GRAY+message);
    }

    /**
     * Adds a player to the game.
     * @param player
     */
    public void addPlayer(Player player) {
        if (state != gameState.WAITING) {
            return;
        }
        players.add(player);
        player.teleport(lobbySpawn);
        resetPlayer(player);
        giveLobbyItems(player);
        gameBroadcast(ChatColor.DARK_GRAY+"Join> "+ChatColor.GRAY+player.getName());
        updateScoreboard();
    }

    /**
     * Removes a player from the game.
     * @param player
     */
    public void removePlayer(Player player) {

        resetPlayer(player);
        players.remove(player);
        removePlayerFromWorld(player, false);
        gameBroadcast(ChatColor.DARK_GRAY+"Quit> "+ChatColor.GRAY+player.getName());
        if (players.size() <= 1 && state == gameState.RUNNING) {
            if (players.size() == 1) winner = new Winner(players.get(0));
            stop();
        }
    }

    void addSpectator(Player player) {

    }

    /**
     * Resets players health, hunger, inventory, and gamemode.
     * @param player
     */
    void resetPlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(10);
        player.getActivePotionEffects().clear();
        player.getEquipment().clear();
        player.getInventory().clear();
        if (player.getWorld() == lobbyWorld) player.setGameMode(GameMode.ADVENTURE);
        else player.setGameMode(gameMode);
    }

    /**
     * Gives player lobby items
     * @param player
     */
    void giveLobbyItems(Player player) {
        player.getInventory().clear();

        ItemMeta im;

        //Start game
        ItemStack startItem = new ItemStack(Material.LIME_CONCRETE);
        im = startItem.getItemMeta();
        im.setDisplayName(ChatColor.GREEN+""+ChatColor.BOLD+"Start Game");
        startItem.setItemMeta(im);
        player.getInventory().setItem(7, startItem);

        //Bed
        ItemStack bedItem = new ItemStack(Material.RED_BED);
        im = bedItem.getItemMeta();
        im.setDisplayName(ChatColor.RESET+""+ChatColor.BOLD+"Leave Lobby");
        bedItem.setItemMeta(im);
        player.getInventory().setItem(8, bedItem);
    }

    /**
     * Teleports players to the game world and starts counting down to game start.
     */
    public void start() {
        if (state != gameState.WAITING) {
            return;
        }
        if (players.size() <= 0) {
            Bukkit.getLogger().warning("Unable to start "+name+" because there are not enough players.");
            return;
        }

        //Teleport players to the game
        players.forEach((player) -> {
            player.teleport(getSpawnpoint(player));
            resetPlayer(player);
        });

        //Start countdown
        state = gameState.STARTING;
        updateScoreboard();
        gameBroadcast(ChatColor.BLUE+""+ChatColor.BOLD+"=================================");
        gameBroadcast(ChatColor.AQUA+"Game - "+ChatColor.YELLOW+ChatColor.BOLD+name);
        gameBroadcast(" ");
        description.forEach(msg -> {
            gameBroadcast("  "+msg);
        });
        gameBroadcast(" ");
        gameBroadcast(ChatColor.AQUA+"Map - "+ChatColor.YELLOW+ChatColor.BOLD+gameMap.displayName+ChatColor.RESET+ChatColor.GRAY+" created by "+ChatColor.YELLOW+ChatColor.BOLD+gameMap.author);
        gameBroadcast(ChatColor.BLUE+""+ChatColor.BOLD+"=================================");
        gamePrefixedBroadcast("Starting in 10 seconds...");
        new BukkitRunnable() {
            int countdown = 3;
            @Override
            public void run() {
                if (countdown <= 0) {
                    players.forEach(p -> {
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                    });
                    cancel();
                    return;
                }
                players.forEach(p -> {
                    p.sendTitle(ChatColor.YELLOW+""+countdown, "", 0, 30, 5);
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1 ,1);
                });
                countdown--;
            }
        }.runTaskTimer(main, 7*20, 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            //Start the game
            state = gameState.RUNNING;
            updateScoreboard();
            gamePrefixedBroadcast(name+" has started.");
            players.forEach(this::resetPlayer);
            Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent(this));
        }, 200L);


    }

    /**
     * Teleports a player out of the game.
     * @param player
     * @param moveToLobby Whether to move the player to the game lobby or send them to the main world.
     */
    void removePlayerFromWorld(Player player, Boolean moveToLobby) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        resetPlayer(player);
        if(moveToLobby) {
            player.teleport(lobbySpawn);
            giveLobbyItems(player);
        } else {
            player.teleport(main.defaultWorld.getSpawnLocation());
        }
    }

    /**
     * Removes all players from the game.
     * @param moveToLobby Whether to move the players to the game lobby or send them to the main world.
     */
    void removePlayers(Boolean moveToLobby) {
        players.forEach(player -> {
            removePlayerFromWorld(player, moveToLobby);
        });
    }

    /**
     * Ends the game, showing the winner. Players will be teleported to the lobby after 10 seconds.
     */
    public void stop() {
        state = gameState.FINISHED;
        players.forEach(player -> {
            if (winner.player != null) {
                player.sendTitle(ChatColor.YELLOW+winner.player.getName(), ChatColor.YELLOW+"won the game!", 0, 200, 5);
            } else if (winner.team != null) {
                player.sendTitle(winner.team.displayName+" Team", ChatColor.YELLOW+"won the game!", 0, 200, 5);
            } else {
                player.sendTitle(ChatColor.RED+"GAME OVER", "", 0, 200, 5);
            }

        });
        updateScoreboard();
        if (players.size() == 0) {
            state = gameState.WAITING;
            removePlayers(true);
            loadMap();
            Bukkit.getServer().getPluginManager().callEvent(new GameResetEvent(this));
            updateScoreboard();
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            state = gameState.WAITING;
            removePlayers(true);
            loadMap();
            Bukkit.getServer().getPluginManager().callEvent(new GameResetEvent(this));
            updateScoreboard();
        }, 200L);
    }

    /**
     * Removes players and unloads worlds.
     */
    public void shutdown() {
        removePlayers(false);
        WorldLoader.unloadWorld(lobbyWorld);
        WorldLoader.unloadWorld(gameWorld);
    }

    /**
     * Reloads the game map.
     */
    void loadMap() {
        try {
            gameMap = WorldLoader.loadMap(maps[0], id+"_game");
            gameWorld = gameMap.world;
            Bukkit.getServer().getPluginManager().callEvent(new GameMapLoadEvent(this));
        } catch (Exception e) {
            e.printStackTrace();
            gamePrefixedBroadcast("Failed to load map!");
            shutdown();
        }
    }

    /**
     * Checks if a block was placed by a player.
     * @param loc The location of the block.
     * @return Whether the block was placed by a player.
     */
    protected Boolean blockPlacedByPlayer(Location loc) {
        for (Location blockLocation : playerPlacedBlocks) {
            if (loc.equals(blockLocation)) return true;
        }
        return false;
    }

    protected void handleDeath(Player player, String deathMessage) {
        gameBroadcast(deathMessage);
        resetPlayer(player);
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(gameMap.getNextSpawnpoint(Team.SPECTATORS).location.toLocation(gameWorld));
        Game game = this;
        BukkitRunnable respawnTimer = new BukkitRunnable() {
            int secondsRemaining = respawnDelay;
            @Override
            public void run() {
                if (state != gameState.RUNNING) return;
                if (secondsRemaining <= 0) {
                    player.setGameMode(game.gameMode);
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.setSaturation(10);

                    player.teleport(getSpawnpoint(player));
                    player.sendTitle(ChatColor.GREEN+"RESPAWNED","",0,10,10);
                    Bukkit.getServer().getPluginManager().callEvent(new GameSpawnPlayerEvent(game, player));
                    cancel();
                    return;
                }
                player.sendTitle(ChatColor.RED+"YOU DIED", ChatColor.YELLOW+"Respawn in "+secondsRemaining+"...", 0, 40, 0);
                secondsRemaining--;
            }
        };
        respawnTimer.runTaskTimer(main, 0, 20);
    }



    void updateCompass(Player player) {
        //Get closest player
        List<Player> playerLocations = new ArrayList<Player>();
        players.forEach(p -> {
            if (p.getGameMode() != GameMode.SPECTATOR && p != player) playerLocations.add(p);
        });
        if (playerLocations.size() == 0) {
            return;
        }
        Player closest = playerLocations.get(0);
        Double closestDist = closest.getLocation().distance(player.getLocation());
        for (Player p : playerLocations) {
            if (p.getLocation().distance(player.getLocation()) < closestDist) {
                closestDist = p.getLocation().distance(player.getLocation());
                closest = p;
            }
        }

        //Set compass target
        player.setCompassTarget(closest.getLocation());

        //Send title
        if (player.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
            player.sendActionBar(ChatColor.BLUE+"Nearest Player: "+ChatColor.YELLOW+closest.getName()+ChatColor.BLUE+" Distance: "+ChatColor.YELLOW+Math.round(closestDist)+"m");
        }
    }

    final protected void updateScoreboard() {
        players.forEach(player -> {
            List<String> defaultLines = new ArrayList<String>();
            defaultLines.add(" ");
            defaultLines.add(ChatColor.YELLOW+""+ChatColor.BOLD+"Game");
            defaultLines.add(name);
            defaultLines.add("  ");

            defaultLines.add(ChatColor.YELLOW+""+ChatColor.BOLD+"Map");
            defaultLines.add(gameMap.displayName);
            defaultLines.add("   ");

            if (state == gameState.WAITING || state == gameState.STARTING) {
                defaultLines.add(ChatColor.YELLOW+""+ChatColor.BOLD+"Status");
                defaultLines.add(ChatColor.GRAY+""+ChatColor.ITALIC+WordUtils.capitalizeFully(state.toString())+"...");
                defaultLines.add("    ");
            }

            GameUpdateScoreboardEvent event = new GameUpdateScoreboardEvent(this, player, defaultLines);
            Bukkit.getServer().getPluginManager().callEvent(event);
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("MesaMC","dummy", event.title);
            List<String> lines = event.lines;
            List<String> usedLines = new ArrayList<String>();
            for (int i = 0; i < lines.size(); i++) {
                String line = ""+lines.get(i);
                if (usedLines.contains(line)) {
                    line = line+" ";
                }
                usedLines.add(line);
                objective.getScore(line).setScore(i*-1);
            }
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            player.setScoreboard(scoreboard);
        });
    }

    @EventHandler
    public final void onBreak(BlockBreakEvent e) {
        World world = e.getBlock().getWorld();
        if (world == lobbyWorld) {
            e.setCancelled(true);
        }
        if (world == gameWorld) {
            if (!allowBreak || state != gameState.RUNNING) {
                if (allowPlace) {
                    e.setCancelled(!blockPlacedByPlayer(e.getBlock().getLocation()));
                    return;
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public final void onPlace(BlockPlaceEvent e) {
        World world = e.getBlock().getWorld();
        if (world == lobbyWorld) {
            e.setCancelled(true);
        }
        if (world == gameWorld) {
            if (!allowPlace || state != gameState.RUNNING) {
                e.setCancelled(true);
            } else {
                playerPlacedBlocks.add(e.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public final void onHungerDeplete(FoodLevelChangeEvent e) {
        if (!(e.getEntity().getWorld() == lobbyWorld || e.getEntity().getWorld() == gameWorld)) {
            return;
        }
        if (e.getEntity().getWorld() == gameWorld && disableHunger == false && state == gameState.RUNNING) {
            return;
        }
        e.setCancelled(true);
        if (e.getEntity() instanceof Player) {
            Player player = (Player)e.getEntity();
            player.setFoodLevel(20);
            player.setSaturation(10);
        }

    }

    @EventHandler
    public final void onDamage(EntityDamageEvent e) {
        if (e.getEntity().getWorld() == lobbyWorld || (e.getEntity().getWorld() == gameWorld && state != gameState.RUNNING)) {
            e.setCancelled(true);
        }
        if (e.getEntity().getWorld() == gameWorld) {
            if (e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                if (player.getHealth() - e.getFinalDamage() <= 0) {
                    e.setCancelled(true);
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.setSaturation(10);
                    EntityDamageEvent.DamageCause cause = e.getCause();
                    if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK && cause != EntityDamageEvent.DamageCause.MAGIC && cause != EntityDamageEvent.DamageCause.PROJECTILE) {
                        handleDeath(player, ChatColor.BLUE+"Death> "+ChatColor.YELLOW+player.getName()+ChatColor.GRAY+" killed by "+ChatColor.YELLOW+WordUtils.capitalizeFully(cause.toString()));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public final void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity().getWorld() == gameWorld) {
            if (e.getEntity() instanceof Player && (e.getDamager() instanceof Player || e.getDamager() instanceof Arrow)) {
                if (e.getDamager() == e.getEntity()) {
                    e.setCancelled(true);
                    return;
                }
                if (!(((Player) e.getEntity()).getHealth() - e.getFinalDamage() <= 0)) {
                    return;
                }
                AtomicReference<Player> killer = new AtomicReference<Player>();
                killer.set(null);
                AtomicReference<String> weapon = new AtomicReference<>("Archery");
                if (e.getDamager() instanceof Player) {
                    killer.set((Player) e.getDamager());
                    weapon.set(((Player) e.getDamager()).getInventory().getItemInMainHand().getType().toString()
                            .replace('_', ' ')
                            .replace("AIR", "FIST"));
                }
                if (e.getDamager() instanceof Arrow) {
                    ProjectileSource source = ((Arrow)e.getDamager()).getShooter();
                    if (source instanceof Player) killer.set((Player) source);
                    if (source instanceof Skeleton) {
                        players.forEach(p -> {
                            npcs.forEach((id, ent) -> {
                                if (id.startsWith("skeleton_"+p.getUniqueId())) {
                                    if (ent == source) {
                                        killer.set(p);
                                        weapon.set("Skeleton");
                                    }
                                }
                            });
                        });


                    }
                }

                weapon.set(WordUtils.capitalizeFully(weapon.get()));
                GameCombatKillEvent killEvent = new GameCombatKillEvent(this, (Player) killer.get(), (Player) e.getEntity(), weapon.get());

                if (killEvent.killed == killEvent.killer) return;

                if (killer.get() != null) {
                    Bukkit.getServer().getPluginManager().callEvent(killEvent);
                    handleDeath(killEvent.killed,ChatColor.BLUE+"Death> "+ChatColor.YELLOW+killEvent.killed.getName()+ChatColor.GRAY+" killed by "+ChatColor.YELLOW+killEvent.killer.getName()+ChatColor.GRAY+ " using "+ChatColor.YELLOW+killEvent.weapon);
                } else {
                    handleDeath(killEvent.killed,ChatColor.BLUE+"Death> "+ChatColor.YELLOW+killEvent.killed.getName()+ChatColor.GRAY+" killed by "+ChatColor.YELLOW+e.getCause().toString());
                }

            }
        }
    }

    @EventHandler
    public final void onTeleport(PlayerTeleportEvent e) {

        World from = e.getFrom().getWorld();
        World to = e.getTo().getWorld();

        //Player left game world
        if (from == gameWorld && to != gameWorld) {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerLeaveGameEvent(this, e.getPlayer()));
        }
        if ((from != lobbyWorld && from != gameWorld) && (to == lobbyWorld || to == gameWorld)) {
            if (!players.contains(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public final void onInteraction(PlayerInteractEvent e) {
        if (e.getPlayer().getWorld() == lobbyWorld) {
            e.setCancelled(true);
            switch (e.getPlayer().getInventory().getHeldItemSlot()) {
                case 7:
                    Bukkit.getServer().dispatchCommand(e.getPlayer(), "start");
                    break;
                case 8:
                    removePlayer(e.getPlayer());
                    break;
            }
        }
    }

    @EventHandler final void onPickup(PlayerAttemptPickupItemEvent e) {
        if (e.getPlayer().getWorld() == lobbyWorld) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public final void onInvMove(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (e.getWhoClicked().getWorld() == lobbyWorld) {
                e.setCancelled(true);
            }
            if (e.getWhoClicked().getWorld() == gameWorld && !allowInventoryMove) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public final void onQuit(PlayerQuitEvent e) {
        removePlayer(e.getPlayer());
    }




}
