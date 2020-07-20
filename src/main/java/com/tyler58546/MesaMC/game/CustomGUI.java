package com.tyler58546.MesaMC.game;

import com.tyler58546.MesaMC.MesaMC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;

public class CustomGUI implements Listener {
    protected final Inventory inv;
    public HashMap<Integer, ItemStack> items;
    public MesaMC main;

    public void onItemClick(int slot, ItemStack item, Player player) {}

    /**
     * @param title Title of the GUI
     * @param rows Number of rows in the GUI
     */
    public CustomGUI(MesaMC main, String title, Integer rows, HashMap<Integer, ItemStack> items) {
        inv = Bukkit.createInventory(null, rows*9, title);
        this.items = items;
        this.main = main;
        initializeItems();
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    public void initializeItems() {
        if (items == null) return;
        items.forEach((slot, item) -> {
            inv.setItem(slot, item);
        });
    }

    public void setItem(int slot, ItemStack item) {
        inv.setItem(slot, item);
    }

    // Nice little method to create a gui item with a custom name, and description
    public static ItemStack CreateGuiItem(final Material material, final int amount, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }

    /** Opens the inventory */
    public void open(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    public void close(final HumanEntity ent) {
        ent.closeInventory();
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();

        onItemClick(e.getRawSlot(), clickedItem, p);
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
            e.setCancelled(true);
        }
    }
}
