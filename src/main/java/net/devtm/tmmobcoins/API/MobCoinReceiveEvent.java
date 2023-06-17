package net.devtm.tmmobcoins.API;

import net.devtm.tmmobcoins.util.MobCoinsPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MobCoinReceiveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Player player;
    private final MobCoinsPlayer mobCoinsPlayer;
    private final Entity entity;
    private boolean isCancelled = false;
    private double obtainedAmount;

    public MobCoinReceiveEvent(Player player, MobCoinsPlayer mobCoinsPlayer, Entity entity, double obtainedAmount) {
        this.player = player;
        this.mobCoinsPlayer = mobCoinsPlayer;
        this.entity = entity;
        this.obtainedAmount = obtainedAmount;
    }

    public Player getPlayer() { return player; }

    public double getObtainedAmount() { return obtainedAmount;  }

    public Entity getEntity() { return entity; }

    public void setDropAmount(double amount) { this.obtainedAmount = amount; }

    public MobCoinsPlayer getMobCoinsPlayer() { return mobCoinsPlayer; }

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
