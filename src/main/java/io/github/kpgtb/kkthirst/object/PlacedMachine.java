package io.github.kpgtb.kkthirst.object;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlacedMachine {
    private final Location location;
    private final String type;
    private final Inventory baseInventory;
    private final int progressBarLength;

    private ItemStack[] ingredients;
    private ItemStack[] results;
    private MachineRecipe actualRecipe;
    private int progress;
    private int progressTime;

    public PlacedMachine(Location location, String type, Inventory baseInventory, ItemStack[] ingredients, ItemStack[] results, MachineRecipe actualRecipe, int progressTime, int progressBarLength) {
        this.location = location;
        this.type = type;
        this.baseInventory = baseInventory;
        this.ingredients = ingredients;
        this.results = results;
        this.actualRecipe = actualRecipe;
        this.progressTime = progressTime;
        this.progressBarLength = progressBarLength;

        progress = 0;
        if(actualRecipe != null) {
            progress = Math.round(progressTime / progressBarLength);
        }
    }

    public Location getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public Inventory getBaseInventory() {
        return baseInventory;
    }

    public ItemStack[] getIngredients() {
        return ingredients;
    }

    public ItemStack[] getResults() {
        return results;
    }

    public MachineRecipe getActualRecipe() {
        return actualRecipe;
    }

    public int getProgress() {
        return progress;
    }

    public int getProgressTime() {
        return progressTime;
    }

    public int getProgressBarLength() {
        return progressBarLength;
    }

    public void setIngredients(ItemStack[] ingredients) {
        this.ingredients = ingredients;
    }

    public void setResults(ItemStack[] results) {
        this.results = results;
    }

    public void setActualRecipe(MachineRecipe actualRecipe) {
        this.actualRecipe = actualRecipe;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setProgressTime(int progressTime) {
        this.progressTime = progressTime;
    }
}
