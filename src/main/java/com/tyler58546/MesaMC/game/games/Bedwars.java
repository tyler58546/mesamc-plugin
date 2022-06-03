package com.tyler58546.MesaMC.game.games;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.*;
import com.tyler58546.MesaMC.game.event.*;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.tyler58546.MesaMC.game.Team.CUSTOM;
import static com.tyler58546.MesaMC.game.Team.PLAYERS;

public class Bedwars extends Game {

    BukkitRunnable ironTimer;
    BukkitRunnable goldTimer;
    BukkitRunnable diamondTimer;
    BukkitRunnable emeraldTimer;

    public Bedwars(MesaMC main) {
        super(main, GameType.BEDWARS.id, GameType.BEDWARS.name, new String[]{"bedwars1", "bedwars2"}, GameType.BEDWARS);
        description.add("You can respawn until your bed is broken.");
        description.add("Purchase blocks to protect your bed.");
        description.add("Destroy your opponent's bed and kill them to win.");
        showFinalKillMessage = true;
        allowInventoryMove = true;
        allowPlace = true;
        enableFallDamage = true;
        useTeams = true;
        teams = new Team[]{Team.RED, Team.BLUE};
    }

    public void openShop(Player player) {
        BedwarsPlayerData data = (BedwarsPlayerData)getPlayerData(player);
        List<ShopItem> shopItems = new ArrayList<ShopItem>();

        // Wool
        shopItems.add(new BedwarsShopItem(10, ((BedwarsTeamData)getTeamData(getPlayerData(player).team)).woolMaterial, "Wool", 16, new ItemStack(Material.IRON_INGOT, 4), "Iron", true, true, null, "wool"));

        // End Stone
        shopItems.add(new BedwarsShopItem(11, Material.END_STONE, "End Stone", 8, new ItemStack(Material.GOLD_INGOT, 8), "Gold", true, true, null, "endstone"));

        // Obsidian
        shopItems.add(new BedwarsShopItem(12, Material.OBSIDIAN, "Obsidian", 4, new ItemStack(Material.EMERALD, 4), "Emeralds", true, true, null, "obsidian"));

        // Shears
        shopItems.add(new BedwarsShopItem(13, Material.SHEARS, "Shears", 1, new ItemStack(Material.IRON_INGOT, 12), "Iron", false, true, null, "shears"));

        // Pickaxe
        ItemStack pickaxe = getPickaxe(data.pickaxeLevel + 1);
        shopItems.add(new BedwarsShopItem(14, pickaxe.getType(), pickaxe.getItemMeta().getDisplayName(), 1, new ItemStack(Material.GOLD_INGOT, 2), "Gold", false, true, getPickaxe(data.pickaxeLevel), "pickaxe"));

        // Armor
        String armorName = data.armorLevel < 1 ? "Iron Armor" : "Diamond Armor";
        Material armorMaterial = data.armorLevel < 1 ? Material.IRON_LEGGINGS : Material.DIAMOND_LEGGINGS;
        ItemStack armorCost = data.armorLevel < 1 ? new ItemStack(Material.GOLD_INGOT, 12) : new ItemStack(Material.EMERALD, 6);
        String armorCurrencyName =  data.armorLevel < 1 ? "Gold" : "Emeralds";
        shopItems.add(new BedwarsShopItem(15, armorMaterial, armorName, 1, armorCost, armorCurrencyName, false, false, null, "armor"));

        // Golden Apple
        shopItems.add(new BedwarsShopItem(16, Material.GOLDEN_APPLE, "Golden Apple", 1, new ItemStack(Material.GOLD_INGOT, 3), "Gold", true, true, null, "gapple"));

        ShopGUI shopGUI = new BedwarsShopGUI(main, "Item Shop", 3, shopItems, player, this);
        shopGUI.open(player);
    }

    public void openUpgrades(Player player) {
        BedwarsPlayerData data = (BedwarsPlayerData)getPlayerData(player);
        BedwarsTeamData td = (BedwarsTeamData)getTeamData(data.team);
        List<ShopItem> shopItems = new ArrayList<ShopItem>();
        int sharpPrice = (int) (4 * (Math.pow(2, td.sharpnessLevel)));
        int protPrice = (int) (2 * (Math.pow(2, td.protectionLevel)));
        shopItems.add(new BedwarsShopItem(10, Material.IRON_SWORD, "Sharpness Upgrade", 1, new ItemStack(Material.DIAMOND, sharpPrice), "Diamonds", true, false, null, "sharpness"));
        shopItems.add(new BedwarsShopItem(11, Material.IRON_CHESTPLATE, "Protection Upgrade", 1, new ItemStack(Material.DIAMOND, protPrice), "Diamonds", true, false, null, "protection"));

        ShopGUI shopGUI = new BedwarsShopGUI(main, "Team Upgrades", 3, shopItems, player, this);
        shopGUI.open(player);
    }

    public static ItemStack getPickaxe(int level) {
        ItemStack pickaxe = null;
        String name = "";
        if (level == 1) {
            pickaxe = new ItemStack(Material.WOODEN_PICKAXE);
            name = "Wooden Pickaxe";
        }
        else if (level == 2) {
            pickaxe = new ItemStack(Material.STONE_PICKAXE);
            name = "Stone Pickaxe";
        }
        else if (level == 3) {
            pickaxe = new ItemStack(Material.IRON_PICKAXE);
            name = "Iron Pickaxe";
        } else {
            pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
            name = "Diamond Pickaxe";
        }
        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.setUnbreakable(true);
        pickaxeMeta.setDisplayName(ChatColor.RESET + name);
        pickaxe.setItemMeta(pickaxeMeta);
        return pickaxe;
    }

    ItemStack getAxe(int level) {
        ItemStack axe = null;
        if (level == 1) {
            axe = new ItemStack(Material.WOODEN_AXE);
        }
        if (level == 2) {
            axe = new ItemStack(Material.STONE_AXE);
        }
        if (level == 3) {
            axe = new ItemStack(Material.IRON_AXE);
        }
        assert axe != null;
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.setUnbreakable(true);
        axe.setItemMeta(axeMeta);
        return axe;
    }

    public void setArmor(Player player) {
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        BedwarsPlayerData data = (BedwarsPlayerData)getPlayerData(player);
        BedwarsTeamData td = (BedwarsTeamData)getTeamData(data.team);
        Material[] armorMaterial = {Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS};
        switch (data.armorLevel) {
            case 1:
                armorMaterial[2] = Material.IRON_LEGGINGS;
                armorMaterial[3] = Material.IRON_BOOTS;
                break;
            case 2:
                armorMaterial[2] = Material.DIAMOND_LEGGINGS;
                armorMaterial[3] = Material.DIAMOND_BOOTS;
                break;
        }
        ArrayList<ItemStack> armorItems = new ArrayList<>();
        for (Material material : armorMaterial) {
            ItemStack item = new ItemStack(material, 1);
            ItemMeta im = item.getItemMeta();
            im.setUnbreakable(true);
            if (im instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta) im).setColor(((BedwarsTeamData)getTeamData(data.team)).color);
            }
            if (td.protectionLevel > 0) {
                im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, td.protectionLevel, true);
            }
            item.setItemMeta(im);
            armorItems.add(item);
        }
        player.getInventory().setArmorContents(new ItemStack[] {armorItems.get(3), armorItems.get(2), armorItems.get(1), armorItems.get(0)});
    }

    void resetInventory(Player player) {

        BedwarsPlayerData data = (BedwarsPlayerData)getPlayerData(player);
        int sharpnessLevel = ((BedwarsTeamData)getTeamData(data.team)).sharpnessLevel;

        //Unbreakable wood sword
        player.getInventory().clear();
        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setUnbreakable(true);
        if (sharpnessLevel > 0) {
            swordMeta.addEnchant(Enchantment.DAMAGE_ALL, sharpnessLevel, true);
        }
        sword.setItemMeta(swordMeta);
        player.getInventory().setItem(0, sword);

        //Unbreakable shears
        if (data.hasShears) {
            ItemStack shears = new ItemStack(Material.SHEARS);
            ItemMeta shearsMeta = shears.getItemMeta();
            shearsMeta.setUnbreakable(true);
            shears.setItemMeta(shearsMeta);
            player.getInventory().addItem(shears);
        }

        //Pickaxe
        if (data.pickaxeLevel > 0) {
            player.getInventory().addItem(getPickaxe(data.pickaxeLevel));
        }

        //Axe
        if (data.axeLevel > 0) {
            player.getInventory().addItem(getAxe(data.axeLevel));
        }

        //Armor
        setArmor(player);
    }

    ArrayList<Entity> getEntitiesInGenerator(Location location) {
        double minX = location.getX() - 1.5;
        double maxX = location.getX() + 1.5;
        double minY = location.getY() - 1;
        double maxY = location.getY() + 4;
        double minZ = location.getZ() - 1.5;
        double maxZ = location.getZ() + 1.5;
        ArrayList<Entity> entsInGen = new ArrayList<>();
        gameWorld.getEntities().forEach(ent -> {
            Location loc = ent.getLocation();
            if (loc.getX() > minX && loc.getX() < maxX && loc.getY() > minY && loc.getY() < maxY && loc.getZ() > minZ && loc.getZ() < maxZ) {
                entsInGen.add(ent);
            }
        });
        return entsInGen;
    }

    void spawnGeneratorItem(Location location, ItemStack item, int maxItems) {
        ArrayList<Player> playersInGen = new ArrayList<>();
        ArrayList<Entity> entitiesInGen = getEntitiesInGenerator(location);
        int itemsInGen = 0;
        for (Entity entity : entitiesInGen) {
            if (entity instanceof Item) {
                Item itemEntity = (Item)entity;
                if (itemEntity.getItemStack().getType() == item.getType()) {
                    itemsInGen += itemEntity.getItemStack().getAmount();
                }
            }
            if (entity instanceof Player) {
                Player player = (Player)entity;
                if (players.contains(player) && player.getGameMode() != GameMode.SPECTATOR) {
                    playersInGen.add(player);
                }
            }
        }
        if (itemsInGen < maxItems) {
            if (playersInGen.size() > 1) {
                playersInGen.forEach(player -> {
                    player.getInventory().addItem(item);
                });
            } else {
                gameWorld.dropItem(location, item);
            }
        }

    }

    @EventHandler
    public void onEntityInteraction(PlayerInteractAtEntityEvent e) {
        if (e.getPlayer().getWorld() != gameWorld) return;
        e.setCancelled(true);
        npcs.forEach((id, npc) -> {
            if (npc == e.getRightClicked() && id.startsWith("shop_items")) {
                openShop(e.getPlayer());
            }
            if (npc == e.getRightClicked() && id.startsWith("shop_upgrades")) {
                openUpgrades(e.getPlayer());
            }
        });
    }

    @Override
    protected PlayerData createPlayerData(Player player) {
        return new BedwarsPlayerData();
    }

    @Override
    protected TeamData createTeamData(Team team) {
        return new BedwarsTeamData(team);
    }

    @Override
    protected Boolean canRespawn(Player player) {
        BedwarsTeamData data = (BedwarsTeamData)getTeamData(getPlayerData(player).team);
        return !data.bedDestroyed;
    }

    @Override
    protected Location getSpawnpoint(Player player) {
        Team playerTeam = getPlayerData(player).team;
        return gameMap.getNextSpawnpoint(playerTeam).location.toLocation(gameWorld);
    }

    @EventHandler
    public void onStart(GameStartEvent e) {
        if (e.game != this) {
            return;
        }
        players.forEach(player -> {
            resetInventory(player);
        });
        ironTimer = new BukkitRunnable() {
            @Override
            public void run() {
                gameMap.getTeamSpawnPoints(CUSTOM).forEach((spawnpoint) -> {
                    if (spawnpoint.id < 2) spawnGeneratorItem(spawnpoint.location.toLocation(gameWorld), new ItemStack(Material.IRON_INGOT), 512);
                });
            }
        };
        gameTimers.add(ironTimer);
        goldTimer = new BukkitRunnable() {
            @Override
            public void run() {
                gameMap.getTeamSpawnPoints(CUSTOM).forEach((spawnpoint) -> {
                    if (spawnpoint.id < 2) spawnGeneratorItem(spawnpoint.location.toLocation(gameWorld), new ItemStack(Material.GOLD_INGOT), 128);
                });
            }
        };
        gameTimers.add(goldTimer);
        diamondTimer = new BukkitRunnable() {
            @Override
            public void run() {
                gameMap.getTeamSpawnPoints(CUSTOM).forEach((spawnpoint) -> {
                    if (spawnpoint.id == 2 || spawnpoint.id == 3) spawnGeneratorItem(spawnpoint.location.toLocation(gameWorld), new ItemStack(Material.DIAMOND), 4);
                });
            }
        };
        gameTimers.add(diamondTimer);
        emeraldTimer = new BukkitRunnable() {
            @Override
            public void run() {
                gameMap.getTeamSpawnPoints(CUSTOM).forEach((spawnpoint) -> {
                    if (spawnpoint.id == 4 || spawnpoint.id == 5) spawnGeneratorItem(spawnpoint.location.toLocation(gameWorld), new ItemStack(Material.EMERALD), 2);
                });
            }
        };
        gameTimers.add(emeraldTimer);

        ironTimer.runTaskTimer(main, 20, 20*2);
        goldTimer.runTaskTimer(main, 20, 20*15);
        diamondTimer.runTaskTimer(main, 20, 20*60);
        emeraldTimer.runTaskTimer(main, 20, 20*60);
        updateScoreboard();
    }

    @EventHandler
    public void onMapLoad(GameMapLoadEvent e) {
        if (e.game != this) {
            return;
        }
        gameMap.getTeamSpawnPoints(CUSTOM).forEach(spawnpoint -> {
            switch (spawnpoint.id) {
                case 2:
                case 3:
                    npcs.put("hologram_diamondgen_"+spawnpoint.id, gameWorld.spawn(spawnpoint.location.toLocation(gameWorld).add(0,2.75,0), ArmorStand.class));
                    break;
                case 4:
                case 5:
                    npcs.put("hologram_emeraldgen_"+spawnpoint.id, gameWorld.spawn(spawnpoint.location.toLocation(gameWorld).add(0,2.75,0), ArmorStand.class));
                    break;
                case 6:
                case 8:
                    npcs.put("shop_items_"+spawnpoint.id, gameWorld.spawn(spawnpoint.location.toLocation(gameWorld), Villager.class));
                    break;
                case 7:
                case 9:
                    npcs.put("shop_upgrades_"+spawnpoint.id, gameWorld.spawn(spawnpoint.location.toLocation(gameWorld), Villager.class));
                    break;
            }
        });
        npcs.forEach((id, npc) -> {
            if (id.startsWith("shop")) {
                Villager v = (Villager) npc;
                if (id.startsWith("shop_items")) {
                    v.setCustomName(ChatColor.GREEN+""+ChatColor.BOLD+"ITEM SHOP");
                }
                if (id.startsWith("shop_upgrades")) {
                    v.setCustomName(ChatColor.GOLD+""+ChatColor.BOLD+"TEAM UPGRADES");
                }
                if (id.endsWith("8") || id.endsWith("9")) {
                    v.setRotation(180, 0);
                }
                v.setCustomNameVisible(true);
                v.setAI(false);
                v.setGravity(false);
                v.setInvulnerable(true);
            }
            if (id.startsWith("hologram")) {
                ArmorStand a = (ArmorStand) npc;
                a.setInvulnerable(true);
                a.setVisible(false);
                a.setGravity(false);
                a.setCustomNameVisible(true);
                a.setMarker(true);
            }
            if (id.startsWith("hologram_diamondgen_")) {
                ArmorStand a = (ArmorStand) npc;
                a.setCustomName(ChatColor.AQUA+""+ChatColor.BOLD+"DIAMOND GENERATOR");
            }
            if (id.startsWith("hologram_emeraldgen_")) {
                ArmorStand a = (ArmorStand) npc;
                a.setCustomName(ChatColor.GREEN+""+ChatColor.BOLD+"EMERALD GENERATOR");
            }
        });
    }

    @EventHandler
    public void onSpawn(GameSpawnPlayerEvent e) {
        if (e.game != this) {
            return;
        }
        resetInventory(e.player);
    }

    @EventHandler
    public void onDeath(GameDeathEvent e) {
        if (e.game != this) {
            return;
        }
        int remainingTeams = 0;
        Team winningTeam = null;
        for (Team team : teams) {
            if (getTeamMembers(team).size() > 0) {
                remainingTeams += 1;
                winningTeam = team;
            }
        }
        if (remainingTeams < 2) {
            winner = new Winner(winningTeam);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, this::stop, 20L);
        }
    }

    @EventHandler
    public void onScoreboardUpdate(GameUpdateScoreboardEvent e) {
        if (e.game != this) return;
        if (state != gameState.RUNNING) return;
        e.lines.add(ChatColor.YELLOW+""+ChatColor.BOLD+"Teams");
        for (Team team : teams) {
            boolean bedDestroyed = ((BedwarsTeamData)getTeamData(team)).bedDestroyed;
            int members = getTeamMembers(team).size();
            e.lines.add(team.displayName + " " + (bedDestroyed ? (members > 0 ? ChatColor.GREEN+""+members : ChatColor.RED+"✗") : ChatColor.GREEN+"✓"));
        }
        e.lines.add("     ");
    }

    @EventHandler
    public final void onBreak(BlockBreakEvent e) {
        super.onBreak(e);
        Team[] allTeams = Team.values();
        for (Team bedTeam : allTeams) {
            BedwarsTeamData data = (BedwarsTeamData) getTeamData(bedTeam);
            Material bedMaterial = data.bedMaterial;
            if (e.getBlock().getType() == bedMaterial) {
                if (getPlayerData(e.getPlayer()).team == bedTeam) {
                    e.getPlayer().sendMessage(ChatColor.RED + "You cannot destroy your own bed!");
                    e.setCancelled(true);
                    return;
                }
                e.setCancelled(false);
                e.setDropItems(false);
                data.bedDestroyed = true;
                gameBroadcast(ChatColor.BOLD + "BED DESTRUCTION > " + ChatColor.GRAY + bedTeam.displayName + ChatColor.RESET + "" + ChatColor.GRAY + " bed was destroyed by " + e.getPlayer().getName());
                for (Player teamMember : getTeamMembers(bedTeam)) {
                    teamMember.sendTitle(ChatColor.RED + "BED DESTROYED", ChatColor.YELLOW + "You can no longer respawn!", 10, 80, 10);
                }
                for (Player player : players) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
                }
                updateScoreboard();
            }
        }
    }

    @EventHandler
    public final void onInteract(PlayerInteractEvent e) {
        World world = e.getPlayer().getWorld();
        if (world != gameWorld) {
            return;
        }
        if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material clickedMaterial = e.getClickedBlock().getType();
            if (!e.getPlayer().isSneaking() && (clickedMaterial == Material.RED_BED || clickedMaterial == Material.BLUE_BED)) {
                e.setCancelled(true);
            }
        }

    }

    public static boolean isPermanantItem(Material m) {
        switch (m) {
            case WOODEN_SWORD:
            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case SHEARS:
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return true;
        }
        return false;
    }

    @EventHandler
    public final void onDrop(PlayerDropItemEvent e) {
        World world = e.getPlayer().getWorld();
        if (world != gameWorld) {
            return;
        }
        if (isPermanantItem(e.getItemDrop().getItemStack().getType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public final void onInvClick(InventoryClickEvent e) {
        World world = e.getWhoClicked().getLocation().getWorld();
        if (world != gameWorld) {
            return;
        }
        if ((e.getInventory().getType().equals(InventoryType.CHEST) || e.getInventory().getType().equals(InventoryType.ENDER_CHEST)) && e.getCurrentItem() != null && isPermanantItem(e.getCurrentItem().getType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public final void onMove(PlayerMoveEvent e) {
        World world = e.getPlayer().getWorld();
        if (world != gameWorld) {
            return;
        }
        Player player = e.getPlayer();
        BedwarsTeamData td = (BedwarsTeamData) getTeamData(getPlayerData(player).team);
        if (td.sharpnessLevel > 0) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.WOODEN_SWORD) {
                ItemMeta im = item.getItemMeta();
                if (!im.hasEnchant(Enchantment.DAMAGE_ALL) || im.getEnchantLevel(Enchantment.DAMAGE_ALL) != td.sharpnessLevel) {
                    im.addEnchant(Enchantment.DAMAGE_ALL, td.sharpnessLevel, true);
                }
                item.setItemMeta(im);
            }
        }
    }
}

