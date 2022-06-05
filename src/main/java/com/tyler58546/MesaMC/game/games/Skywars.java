package com.tyler58546.MesaMC.game.games;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.Game;
import com.tyler58546.MesaMC.game.GameType;
import com.tyler58546.MesaMC.game.Winner;
import com.tyler58546.MesaMC.game.event.GameStartEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Skywars extends Game {

    protected List<Location> openedChests = new ArrayList<>();

    enum Rarity {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC
    }

    static ItemStack createItem(Material m, String name, int amount, Rarity rarity) {
        ItemStack output = new ItemStack(m, amount);
        ItemMeta im = output.getItemMeta();
        switch (rarity) {
            case COMMON:
                im.setDisplayName(ChatColor.WHITE+name);
                break;
            case UNCOMMON:
                im.setDisplayName(ChatColor.YELLOW+name);
                break;
            case RARE:
                im.setDisplayName(ChatColor.AQUA+name);
                break;
            case EPIC:
                im.setDisplayName(ChatColor.LIGHT_PURPLE+name);
                break;
        }
        output.setItemMeta(im);
        return output;
    }

    static List<ItemStack> getCommonItems() {
        List<ItemStack> output = new ArrayList<>();
        output.add(createItem(Material.OAK_PLANKS, "Oak Planks", 16, Rarity.COMMON));
        output.add(createItem(Material.SNOWBALL, "Snowball", 8, Rarity.COMMON));

        output.add(createItem(Material.LEATHER_HELMET, "Leather Cap", 1, Rarity.COMMON));
        output.add(createItem(Material.LEATHER_CHESTPLATE, "Leather Tunic", 1, Rarity.COMMON));
        output.add(createItem(Material.LEATHER_LEGGINGS, "Leather Pants", 1, Rarity.COMMON));
        output.add(createItem(Material.LEATHER_BOOTS, "Leather Boots", 1, Rarity.COMMON));

        output.add(createItem(Material.STONE_SWORD, "Stone Sword", 1, Rarity.COMMON));
        output.add(createItem(Material.ARROW, "Arrow", 4, Rarity.COMMON));

        return output;
    }

    static List<ItemStack> getUncommonItems() {
        List<ItemStack> output = new ArrayList<>();
        output.add(createItem(Material.IRON_SWORD, "Iron Sword", 1, Rarity.UNCOMMON));

        output.add(createItem(Material.IRON_HELMET, "Iron Helmet", 1, Rarity.UNCOMMON));
        output.add(createItem(Material.IRON_CHESTPLATE, "Iron Chestplate", 1, Rarity.UNCOMMON));
        output.add(createItem(Material.IRON_LEGGINGS, "Iron Leggings", 1, Rarity.UNCOMMON));
        output.add(createItem(Material.IRON_BOOTS, "Iron Boots", 1, Rarity.UNCOMMON));

        output.add(createItem(Material.CHAINMAIL_HELMET, "Chainmail Helmet", 1, Rarity.UNCOMMON));
        output.add(createItem(Material.CHAINMAIL_CHESTPLATE, "Chainmail Chestplate", 1, Rarity.UNCOMMON));
        output.add(createItem(Material.CHAINMAIL_LEGGINGS, "Chainmail Leggings", 1, Rarity.UNCOMMON));
        output.add(createItem(Material.CHAINMAIL_BOOTS, "Chainmail Boots", 1, Rarity.UNCOMMON));

        output.add(createItem(Material.BOW, "Bow", 1, Rarity.UNCOMMON));

        return output;
    }

    static List<ItemStack> getRareItems() {
        List<ItemStack> output = new ArrayList<>();
        output.add(createItem(Material.IRON_SWORD, "Iron Sword", 1, Rarity.RARE));
        output.add(createItem(Material.ENDER_PEARL, "Ender Pearl", 2, Rarity.RARE));
        output.add(createItem(Material.GOLDEN_APPLE, "Golden Apple", 1, Rarity.RARE));

        return output;
    }

    static List<ItemStack> getEpicItems() {
        List<ItemStack> output = new ArrayList<>();
        output.add(createItem(Material.IRON_SWORD, "Diamond Sword", 1, Rarity.RARE));

        return output;
    }


    protected Skywars(MesaMC main, String id, String name, String[] maps) {
        super(main, id, name, maps, GameType.SKYWARS);
        allowPlace = true;
        allowBreak = true;
        allowInventoryMove = true;
    }

    @EventHandler
    public void onStart(GameStartEvent e) {
        if (e.game != this) return;
        players.forEach(p -> {
            resetInventory(p);
            p.sendTitle(ChatColor.GREEN+"FIGHT!", "", 0, 10, 10);

        });
        gameMap.spawnPoints.forEach(spawnPoint -> {
            List<Location> glassBlocks = new ArrayList<>();
            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(0,-1,0));

            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(0,0,1));
            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(1,0,0));
            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(0,0,-1));
            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(-1,0,0));

            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(0,1,1));
            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(1,1,0));
            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(0,1,-1));
            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(-1,1,0));

            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(0,2,1));
            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(1,2,0));
            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(0,2,-1));
            glassBlocks.add(spawnPoint.location.toLocation(gameWorld).add(-1,2,0));

            glassBlocks.forEach(location -> {
                location.getBlock().setType(Material.AIR);
            });
        });
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {
        if (e.getPlayer().getWorld() != gameWorld) return;

        if (e.getInventory().getHolder() instanceof Chest){
            Chest chest = (Chest) e.getInventory().getHolder();
            if (playerPlacedBlocks.contains(chest.getLocation())) return;
            if (openedChests.contains(chest.getLocation())) return;
            openedChests.add(chest.getLocation());
            populateChest(chest.getBlockInventory(), 1f);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getWorld() != gameWorld) return;
        if (e.getBlock() instanceof Chest) {
            Chest chest = (Chest) e.getBlock();
            if (playerPlacedBlocks.contains(chest.getLocation())) return;
            if (openedChests.contains(chest.getLocation())) return;
            openedChests.add(chest.getLocation());
            populateChest(chest.getBlockInventory(), 1f);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntDamage(EntityDamageEvent e) {
        if (e.getEntity().getWorld() != gameWorld || e.getEntityType() != EntityType.PLAYER) return;
        if (!e.isCancelled() && getNonSpectators().size() <= 1) {
            if (getNonSpectators().size() > 0) {
                winner = new Winner(getNonSpectators().get(0));
            }
            stop();
        }
    }

    @Override
    public Boolean canRespawn(Player player) {
        return false;
    }

    ItemStack getItem(Rarity rarity) {
        List<ItemStack> possibleItems;
        switch (rarity) {
            case COMMON:
                possibleItems = getCommonItems();
                break;
            case UNCOMMON:
                possibleItems = getUncommonItems();
                break;
            case RARE:
                possibleItems = getRareItems();
                break;
            case EPIC:
                possibleItems = getEpicItems();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + rarity);
        }
        return possibleItems.get(ThreadLocalRandom.current().nextInt(possibleItems.size()));
    }

    void populateChest(Inventory inv, Float luck) {
        for (int i = 0; i < 3; i++) {
            int slot = ThreadLocalRandom.current().nextInt(inv.getSize());
            float rand = ThreadLocalRandom.current().nextFloat() * (1/luck);
            Rarity rarity = Rarity.COMMON;
            if (rand < 0.25) rarity = Rarity.UNCOMMON;
            if (rand < 0.1) rarity = Rarity.RARE;
            if (rand < 0.025) rarity = Rarity.EPIC;
            inv.setItem(slot, getItem(rarity));
        }
    }

    public void resetInventory(Player player) {
        player.getInventory().setItem(0, new ItemStack(Material.WOODEN_SWORD));
        player.getInventory().setItem(1, new ItemStack(Material.WOODEN_PICKAXE));
        player.getInventory().setItem(2, new ItemStack(Material.WOODEN_AXE));

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.YELLOW+"Tracking Compass");
        compass.setItemMeta(compassMeta);
        player.getInventory().setItem(8, compass);
    }
}
