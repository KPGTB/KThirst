/*
 * Copyright 2022 KPG-TB
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.kpgtb.kkthirst.object;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkui.ui.Alignment;
import io.github.kpgtb.kkui.ui.BaseUI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User {
    private final UUID uuid;
    private double thirst;
    private final double maxThirst;

    private final DataManager dataManager;
    private final BaseUI baseUI;

    private boolean damaging;
    private boolean inWater;

    public User(UUID uuid, double thirst, double maxThirst, DataManager dataManager, int uiOffset) {
        this.uuid = uuid;
        this.thirst = thirst;
        this.maxThirst = maxThirst;

        this.dataManager = dataManager;
        this.damaging = false;
        this.inWater = false;

        baseUI = new BaseUI("", Alignment.LEFT, uiOffset);

        setupUI();
    }

    public void setupUI() {
        // 1 icon = 2 points
        // icons = 10 = 20 points

        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        if(!op.isOnline() || op.getPlayer() == null) {
            return;
        }
        Player player = op.getPlayer();
        GameMode gameMode = player.getGameMode();

        if(gameMode.equals(GameMode.CREATIVE) || gameMode.equals(GameMode.SPECTATOR)) {
            baseUI.update("");
            return;
        }

        double fullIcon = maxThirst / 10.0;
        int fullIconsInUI = (int) Math.floor(thirst / fullIcon);
        boolean hasHalfIconInUI = thirst % fullIcon > 0;

        int emptyIconsInUI = 10 - fullIconsInUI;
        if(hasHalfIconInUI) {
            emptyIconsInUI -= 1;
        }

        String fullIconChar = inWater ? "\uA004\uF802" : "\uA001\uF802";
        String halfIconChar = inWater ? "\uA005\uF802" : "\uA002\uF802";
        String emptyIconChar = inWater ? "\uA006\uF802" : "\uA003\uF802";

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

    public boolean isInWater() {
        return inWater;
    }

    public void setInWater(boolean inWater) {
        this.inWater = inWater;
    }

}
