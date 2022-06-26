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

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Drink {
    private final String codeName;

    private final double thirstPoints;

    private final String drinkName;
    private final ArrayList<String> drinkLore;
    private final Color drinkColor;
    private final int drinkCustomModelData;

    private final ArrayList<PotionEffect> drinkEffects;

    private ItemStack finalDrink;

    public Drink(String codeName, double thirstPoints, String drinkName, ArrayList<String> drinkLore, Color drinkColor, int drinkCustomModelData, ArrayList<PotionEffect> drinkEffects) {
        this.codeName = codeName;
        this.thirstPoints = thirstPoints;
        this.drinkName = drinkName;
        this.drinkLore = drinkLore;
        this.drinkColor = drinkColor;
        this.drinkCustomModelData = drinkCustomModelData;
        this.drinkEffects = drinkEffects;

        prepareDrink();
    }

    private void prepareDrink() {
        finalDrink = new ItemStack(Material.POTION);
        ItemMeta drinkMeta = finalDrink.getItemMeta();

        if(drinkCustomModelData != 0) {
            if(Integer.parseInt(
                    Bukkit.getBukkitVersion()
                            .split("-")[0] // ex. 1.17
                            .split("\\.")[1] // ex. 17
            ) > 13) {
                try {
                    drinkMeta.getClass().getMethod("setCustomModelData", int.class).invoke(drinkMeta, drinkCustomModelData);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        drinkMeta.setDisplayName(drinkName);
        drinkMeta.setLore(drinkLore);
        drinkMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        PotionMeta potionMeta = (PotionMeta) drinkMeta;
        potionMeta.clearCustomEffects();

        potionMeta.setColor(drinkColor);

        finalDrink.setItemMeta(potionMeta);
    }

    public String getCodeName() {
        return codeName;
    }

    public double getThirstPoints() {
        return thirstPoints;
    }

    public ArrayList<PotionEffect> getDrinkEffects() {
        return drinkEffects;
    }

    public ItemStack getFinalDrink() {
        return finalDrink;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public ArrayList<String> getDrinkLore() {
        return drinkLore;
    }

    public Color getDrinkColor() {
        return drinkColor;
    }

    public int getDrinkCustomModelData() {
        return drinkCustomModelData;
    }
}
