package com.tyler58546.MesaMC.game;

import com.tyler58546.MesaMC.MesaMC;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpectatorGUI extends CustomGUI {
    public SpectatorGUI(MesaMC main, Game game) {
        super(main, "Spectator Menu", 3, getItems(main, game));
    }
    static HashMap<Integer, ItemStack> getItems(MesaMC main, Game game) {
        HashMap<Integer, ItemStack> output = new HashMap<>();
        List<Player> validPlayers = new ArrayList<>();
        game.players.forEach(p -> {
            if (p.getGameMode() != GameMode.SPECTATOR && !game.spectators.contains(p)) validPlayers.add(p);
        });
        for (int i = 0; i < validPlayers.size(); i++) {
            SpectatorGUIItem item = new SpectatorGUIItem(validPlayers.get(i), i);
            output.put(item.slot, item);
        }
        return output;
    }
    @Override
    public void onItemClick(int slot, ItemStack item, Player player) {
        if (items.get(slot) instanceof SpectatorGUIItem) {
            SpectatorGUIItem specItem = (SpectatorGUIItem) items.get(slot);
            player.teleport(specItem.player.getLocation());
            player.sendMessage(ChatColor.BLUE+"Teleport> "+ChatColor.GRAY+"You teleported to "+ChatColor.YELLOW+specItem.player.getDisplayName());
        }
    }
}
