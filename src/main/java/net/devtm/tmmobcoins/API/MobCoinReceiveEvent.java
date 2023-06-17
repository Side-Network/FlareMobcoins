package net.devtm.tmmobcoins.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MobCoinReceiveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Player player;
    private final MobcoinsPlayer mobCoinsPlayer;
    private final String entity;
    private boolean isCancelled = false;
    private double obtainedAmount;

    public MobCoinReceiveEvent(Player player, MobcoinsPlayer mobCoinsPlayer, String entity, double obtainedAmount) {
        this.player = player;
        this.mobCoinsPlayer = mobCoinsPlayer;
        this.entity = entity;
        this.obtainedAmount = obtainedAmount;
    }

    public Player getPlayer() { return player; }

    public double getObtainedAmount() { return obtainedAmount;  }

    public String getEntity() { return entity; }

    public void setDropAmount(double amount) { this.obtainedAmount = amount; }

    public MobcoinsPlayer getMobCoinsPlayer() { return mobCoinsPlayer; }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() { return isCancelled; }

    @Override
    public void setCancelled(boolean cancel) { this.isCancelled = cancel; }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
