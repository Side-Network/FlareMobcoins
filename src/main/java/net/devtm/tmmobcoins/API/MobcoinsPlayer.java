package net.devtm.tmmobcoins.API;

import java.util.UUID;
import net.devtm.tmmobcoins.service.ServiceHandler;

public class MobcoinsPlayer {

    UUID uuid;

    double mobcoins;

    double multiplier = 1.0D;

    public MobcoinsPlayer() {
        this.mobcoins = 0.0D;
    }

    public MobcoinsPlayer(UUID uuid, double mobcoins, double multiplier) {
        this.uuid = uuid;
        this.mobcoins = mobcoins;
        this.multiplier = multiplier;
    }

    public UUID getPlayer() {
        return this.uuid;
    }

    public void setPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public double getMobcoins() {
        return this.mobcoins;
    }

    public String getFormattedMobcoins() {
        return String.format("%,.2f", mobcoins);
    }

    public void setMobcoins(double mobcoins) {
        this.mobcoins = mobcoins;
        if (this.uuid == null)
            return;
        ServiceHandler.SERVICE.getDataService().setMobcoins(this.uuid, this.mobcoins);
    }

    public void giveMobcoins(double mobcoins, boolean save) {
        this.mobcoins += mobcoins;
        if (this.uuid == null)
            return;

        if (save)
            ServiceHandler.SERVICE.getDataService().setMobcoins(this.uuid, this.mobcoins);
        else
            ServiceHandler.SERVICE.getDataService().markForAutoSave(this);
    }

    public void removeMobcoins(double mobcoins) {
        this.mobcoins -= mobcoins;
        if (this.uuid == null)
            return;
        ServiceHandler.SERVICE.getDataService().setMobcoins(this.uuid, this.mobcoins);
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
        if (this.uuid == null)
            return;
        ServiceHandler.SERVICE.getDataService().setMultiplier(this.uuid, this.multiplier);
    }
}
