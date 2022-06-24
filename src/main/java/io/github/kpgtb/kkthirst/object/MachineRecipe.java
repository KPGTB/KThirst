package io.github.kpgtb.kkthirst.object;

import org.bukkit.inventory.ItemStack;

public class MachineRecipe {

    private final String recipeName;
    private final ItemStack[] ingredients;
    private final ItemStack[] result;
    private final int workTime;

    public MachineRecipe(String recipeName, ItemStack[] ingredients, ItemStack[] result, int workTime) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.result = result;
        this.workTime = workTime;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public ItemStack[] getIngredients() {
        return ingredients;
    }

    public ItemStack[] getResult() {
        return result;
    }

    public int getWorkTime() {
        return workTime;
    }
}
