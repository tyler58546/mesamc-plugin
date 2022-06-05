package com.tyler58546.MesaMC.game.games;

import com.tyler58546.MesaMC.MesaMC;
import com.tyler58546.MesaMC.game.Game;
import com.tyler58546.MesaMC.game.GameType;
import com.tyler58546.MesaMC.game.Winner;
import com.tyler58546.MesaMC.game.event.GameDeathEvent;
import com.tyler58546.MesaMC.game.event.GameMapLoadEvent;
import com.tyler58546.MesaMC.game.event.GameSpawnPlayerEvent;
import com.tyler58546.MesaMC.game.event.GameStartEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BattleRoyale extends Game {

    public BattleRoyale(MesaMC main) {
        super(main, GameType.BATTLE_ROYALE.id, GameType.BATTLE_ROYALE.name, null, GameType.BATTLE_ROYALE);
        description.add("Glide to a spot with good resources.");
        description.add("Kill other players to become the last one standing.");
        description.add("Watch out for the shrinking world border!");
        allowBreak = true;
        allowPlace = true;
        disableHunger = false;
        allowInventoryMove = true;
        dropItemsOnDeath = true;
    }

    double initialBorderSize = 7500.0;
    double finalBorderSize = 3.0;
    long borderChangeSeconds = 60 * 15;

    @EventHandler
    public void onMapLoad(GameMapLoadEvent e) {
        if (e.game != this) {
            return;
        }

    }

    @EventHandler
    public void onStart(GameStartEvent e) {
        gameWorld.setDifficulty(Difficulty.NORMAL);
        gameWorld.setGameRule(GameRule.DO_MOB_LOOT, true);
        WorldBorder worldBorder = gameWorld.getWorldBorder();
        worldBorder.setCenter(0.0, 0.0);
        worldBorder.setSize(initialBorderSize, 0);
        worldBorder.setSize(finalBorderSize, borderChangeSeconds);

        players.forEach(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 200));
        });
    }

    @EventHandler
    public void onSpawn(GameSpawnPlayerEvent e) {
        if (e.game != this || state != gameState.STARTING) {
            return;
        }
        e.player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
        e.player.setGliding(true);
        e.player.setGameMode(GameMode.ADVENTURE);
    }

    @Override
    protected Location getSpawnpoint(Player player) {
        return new Location(gameWorld, (Math.random() * 1000) - 500, 150, (Math.random() * 1000) - 500);
    }

    @Override
    protected Boolean canRespawn(Player player) {
        return false;
    }

    @EventHandler
    public void onDeath(GameDeathEvent e) {
        if (getNonSpectators().size() <= 1) {
            if (getNonSpectators().size() > 0) {
                winner = new Winner(getNonSpectators().get(0));
            }
            stop();
        }
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent e) {
        if (e.getEntity().getWorld() != gameWorld || e.isGliding() || state != gameState.STARTING) {
            return;
        }
        e.setCancelled(true);
    }
}
