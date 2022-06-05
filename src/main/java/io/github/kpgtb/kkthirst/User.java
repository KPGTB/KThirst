package io.github.kpgtb.kkthirst;

import io.github.kpgtb.kkcore.manager.DataManager;

import java.util.UUID;

public class User {
    private UUID uuid;
    private double thirst;
    private DataManager dataManager;

    public User(UUID uuid, double thirst, DataManager dataManager) {
        this.uuid = uuid;
        this.thirst = thirst;
        this.dataManager = dataManager;
    }

    public void save() {
        dataManager.set("users",uuid.toString(), "thirst", thirst);
    }

    public void setThirst(double thirst) {
        this.thirst = thirst;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getThirst() {
        return thirst;
    }
}
