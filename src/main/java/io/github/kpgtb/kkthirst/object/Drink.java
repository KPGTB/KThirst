package io.github.kpgtb.kkthirst.object;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

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
            // ? +
            //drinkMeta.setCustomModelData(drinkCustomModelData);
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
