package com.tyler58546.MesaMC.game.stats;

import com.tyler58546.MesaMC.MesaMC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StatsCommand implements CommandExecutor, TabCompleter {
    MesaMC main;
    public StatsCommand(MesaMC main) {
        this.main = main;
    }
    public String prefixedMessage(String msg) {
        return ChatColor.BLUE+"Stats> "+ChatColor.GRAY+msg;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefixedMessage("This command can only be used by a player."));
            return true;
        }
        Player player = (Player) sender;
        new StatsGUI(main, player).open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return new ArrayList<>();
    }
}
