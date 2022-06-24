package io.github.kpgtb.kkthirst.object;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BaseMachine {
    private final String type;

    private final String inventoryTitle;
    private final int inventorySize;
    private final int[] ingredientSlots;
    private final int[] resultSlots;
    private final Character customInventoryChar;
    private final String progressBarChars;
    private final int progressBarLength;

    private final ItemStack machineItemStack;

    private final HashMap<String,MachineRecipe> recipes;

    public BaseMachine(String type, String inventoryTitle, int inventorySize, int[] ingredientSlots, int[] resultSlots, Character customInventoryChar, String progressBarChars, int progressBarLength, ItemStack machineItemStack) {
        this.type = type;
        this.inventoryTitle = inventoryTitle;
        this.inventorySize = inventorySize;
        this.ingredientSlots = ingredientSlots;
        this.resultSlots = resultSlots;
        this.customInventoryChar = customInventoryChar;
        this.progressBarChars = progressBarChars;
        this.progressBarLength = progressBarLength;
        this.machineItemStack = machineItemStack;

        recipes = new HashMap<>();
    }

    public String getType() {
        return type;
    }

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    public Character getCustomInventoryChar() {
        return customInventoryChar;
    }

    public String getProgressBarChars() {
        return progressBarChars;
    }

    public int getInventorySize() {
        return inventorySize;
    }

    public int[] getIngredientSlots() {
        return ingredientSlots;
    }

    public int[] getResultSlots() {
        return resultSlots;
    }

    public int getProgressBarLength() {
        return progressBarLength;
    }

    public ItemStack getMachineItemStack() {
        return machineItemStack;
    }

    public void registerRecipe(String recipeName, MachineRecipe recipe) {
        recipes.put(recipeName,recipe);
    }
    public void unregisterRecipe(String recipeName) {
        recipes.remove(recipeName);
    }
    public MachineRecipe getRecipe(String recipeName) {
        return recipes.get(recipeName);
    }
}
