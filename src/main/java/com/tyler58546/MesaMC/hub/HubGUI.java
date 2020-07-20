package com.tyler58546.MesaMC.hub;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.CustomGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public abstract class HubGUI extends CustomGUI {
    List<HubGUIItem> hubItems;
    Player player;
    int rows;

    /**
     * @param main
     * @param title Title of the GUI
     * @param rows  Number of rows in the GUI
     */
    public HubGUI(MesaMC main, String title, Integer rows, List<HubGUIItem> hubItems, Player player) {
        super(main, title, rows, null);
        this.player = player;
        this.hubItems = hubItems;
        this.rows = rows;
        initHubItems();
    }

    protected abstract ItemStack getDefaultItem(int slot);

    private void initHubItems() {
        items = new HashMap<Integer, ItemStack>();

        for (int i = 0; i < rows*9; i++) {
            ItemStack defaultItem = getDefaultItem(i);
            if (defaultItem != null) {
                items.put(i, getDefaultItem(i));
            }
        }

        hubItems.forEach(item -> {
            item.player = player;
            items.put(item.slot, item);
        });
        initializeItems();
    }

    @Override
    public void onItemClick(int slot, ItemStack item, Player player) {
        hubItems.forEach(hubItem -> {
            if (slot == hubItem.slot) {
                hubItem.clickActions.onLeftClick(player);
                close(player);
            }
        });
    }
}
