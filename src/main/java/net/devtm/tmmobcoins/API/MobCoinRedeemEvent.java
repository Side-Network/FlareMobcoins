package net.devtm.tmmobcoins.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MobCoinRedeemEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Player player;
    private final MobcoinsPlayer mobCoinsPlayer;
    private final double amount;

    public MobCoinRedeemEvent(Player player, MobcoinsPlayer mobCoinsPlayer, double amount) {
        this.player = player;
        this.mobCoinsPlayer = mobCoinsPlayer;
        this.amount = amount;
    }

    public Player getPlayer() { return player; }

    public MobcoinsPlayer getMobCoinsPlayer() { return mobCoinsPlayer; }

    public double getAmount() {
        return amount;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
