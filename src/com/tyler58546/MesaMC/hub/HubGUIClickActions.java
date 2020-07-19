package com.tyler58546.MesaMC.hub;

import org.bukkit.entity.Player;

public abstract class HubGUIClickActions {
    public abstract String getLeftClickAction();
    public abstract String getRightClickAction();
    public abstract void onLeftClick(Player player);
    public abstract void onRightClick(Player player);
}
