package com.tyler58546.MesaMC.game.games;

import com.tyler58546.MesaMC.game.Team;
import com.tyler58546.MesaMC.game.TeamData;
import org.bukkit.Color;
import org.bukkit.Material;

public class BedwarsTeamData extends TeamData {
    public Color color;
    public boolean bedDestroyed = false;
    public Material bedMaterial;
    public Material woolMaterial;
    public int sharpnessLevel = 0;
    public int protectionLevel = 0;
    public BedwarsTeamData(Team team) {
        if (team == Team.RED) {
            bedMaterial = Material.RED_BED;
            woolMaterial = Material.RED_WOOL;
            color = Color.RED;
        }
        if (team == Team.BLUE) {
            bedMaterial = Material.BLUE_BED;
            woolMaterial = Material.BLUE_WOOL;
            color = Color.BLUE;
        }
    }
}
