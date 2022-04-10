package net.devtm.tmmobcoins.util;

import java.util.UUID;

public class MobCoinsPlayer {

    private double mobcoins;
    private double multiplier = 1;
    private UUID uuid = null;

    public MobCoinsPlayer(UUID uuid, double mobcoins, double multiplier) {
        this.uuid = uuid;
        this.mobcoins = mobcoins;
        this.multiplier = multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    public void setMobcoins(double amount) {
        this.mobcoins = amount;
    }

    public void removeMobcoins(double amount) {
        this.mobcoins =  Double.parseDouble(String.format("%.2f",mobcoins - amount));
    }

    public void addMobcoins(double amount) {
        this.mobcoins = Double.parseDouble(String.format("%.2f",amount + mobcoins));
    }

    public double getMobcoins() {
        return mobcoins;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void uploadPlayer() {
        StorageAccess.insertAccount(this);
    }

    public double getMultiplier() {
        return multiplier;
    }
}
