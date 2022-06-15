package io.github.kpgtb.kkthirst.object;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkui.ui.Alignment;
import io.github.kpgtb.kkui.ui.BaseUI;

import java.util.UUID;

public class User {
    private final UUID uuid;
    private double thirst;
    private final double maxThirst;

    private final DataManager dataManager;
    private final BaseUI baseUI;

    private boolean damaging;

    public User(UUID uuid, double thirst, double maxThirst, DataManager dataManager, int uiOffset) {
        this.uuid = uuid;
        this.thirst = thirst;
        this.maxThirst = maxThirst;

        this.dataManager = dataManager;
        this.damaging = false;
        baseUI = new BaseUI("", Alignment.LEFT, uiOffset);

        setupUI();
    }

    private void setupUI() {
        // 1 icon = 2 points
        // icons = 10 = 20 points

        double fullIcon = maxThirst / 10.0;
        int fullIconsInUI = (int) Math.floor(thirst / fullIcon);
        boolean hasHalfIconInUI = thirst % fullIcon > 0;

        int emptyIconsInUI = 10 - fullIconsInUI;
        if(hasHalfIconInUI) {
            emptyIconsInUI -= 1;
        }

        String fullIconChar = "\uA001\uF802";
        String halfIconChar = "\uA002\uF802";
        String emptyIconChar = "\uA003\uF802";

        StringBuilder ui = new StringBuilder();
        for(int i = 0; i < emptyIconsInUI; i++) {
            ui.append(emptyIconChar);
        }

        if(hasHalfIconInUI) {
            ui.append(halfIconChar);
        }

        for(int i = 0; i < fullIconsInUI; i++) {
            ui.append(fullIconChar);
        }

        baseUI.update(ui.toString());
    }

    public void save() {
        dataManager.set("users",uuid.toString(), "thirst", thirst);
    }

    public void setThirst(double thirst) {
        if(thirst > maxThirst) {
            this.thirst = maxThirst;
        } else {
            this.thirst = thirst;
        }
        setupUI();
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getThirst() {
        return thirst;
    }

    public BaseUI getBaseUI() {
        return baseUI;
    }

    public boolean isDamaging() {
        return damaging;
    }

    public double getMaxThirst() {
        return maxThirst;
    }

    public void setDamaging(boolean damaging) {
        this.damaging = damaging;
    }
}
