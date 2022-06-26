package io.github.kpgtb.kkthirst.object;

import io.github.kpgtb.kkthirst.manager.MachineManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;

public class PlacedMachine {
    private final Location location;
    private final String type;
    private final Inventory baseInventory;
    private final int progressBarLength;
    private final BaseMachine baseMachine;

    private ItemStack[] ingredients;
    private ItemStack[] results;
    private MachineRecipe actualRecipe;
    private int progress;
    private int progressTime;

    private final MachineManager machineManager;

    public PlacedMachine(Location location, String type, Inventory baseInventory, ItemStack[] ingredients, ItemStack[] results, MachineRecipe actualRecipe, int progressTime, int progressBarLength, MachineManager machineManager) {
        this.location = location;
        this.type = type;
        this.baseInventory = baseInventory;
        this.ingredients = ingredients;
        this.results = results;
        this.actualRecipe = actualRecipe;
        this.progressTime = progressTime;
        this.progressBarLength = progressBarLength;

        this.machineManager = machineManager;

        this.baseMachine = machineManager.getMachine(type);

        progress = 0;
        if(actualRecipe != null) {
            startRecipe(actualRecipe);
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

    public BaseMachine getBaseMachine() {
        return baseMachine;
    }

    public void setIngredients(ItemStack[] ingredients) {
        this.ingredients = ingredients;

        for (int i = 0; i < ingredients.length; i++) {
            if(getBaseInventory().getItem(baseMachine.getIngredientSlots()[i]) == null ||
                    getBaseInventory().getItem(baseMachine.getIngredientSlots()[i]).getType().equals(Material.AIR)) {
                if(ingredients[i] != null && !ingredients[i].getType().equals(Material.AIR)) {
                    getBaseInventory().setItem(baseMachine.getIngredientSlots()[i], ingredients[i]);
                }
                continue;
            }

            if(!getBaseInventory().getItem(baseMachine.getIngredientSlots()[i]).isSimilar(ingredients[i])
            || getBaseInventory().getItem(baseMachine.getIngredientSlots()[i]).getAmount() != ingredients[i].getAmount()) {
                getBaseInventory().setItem(baseMachine.getIngredientSlots()[i], ingredients[i]);
            }
        }

        if(getActualRecipe() == null) {
            checkAvailableRecipe();
        }
    }

    public void setResults(ItemStack[] results) {
        this.results = results;

        for (int i = 0; i < results.length; i++) {
            if(getBaseInventory().getItem(baseMachine.getResultSlots()[i]) == null ||
                    getBaseInventory().getItem(baseMachine.getResultSlots()[i]).getType().equals(Material.AIR)) {
                if(results[i] != null && !results[i].getType().equals(Material.AIR)) {
                    getBaseInventory().setItem(baseMachine.getResultSlots()[i], results[i]);
                }
                continue;
            }

            if(!getBaseInventory().getItem(baseMachine.getResultSlots()[i]).isSimilar(results[i])
                    || getBaseInventory().getItem(baseMachine.getResultSlots()[i]).getAmount() != results[i].getAmount()) {
                getBaseInventory().setItem(baseMachine.getResultSlots()[i], results[i]);
            }
        }
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

    public void startRecipe(MachineRecipe recipe) {
        setActualRecipe(recipe);
        if(progressTime == 0) {setProgressTime(recipe.getWorkTime());}
        setProgress(Math.round((Math.abs(getProgressTime() - recipe.getWorkTime())) / (recipe.getWorkTime() / getProgressBarLength())));

        new BukkitRunnable() {
            @Override
            public void run() {
                // When finish
                if(progressTime -10 <= 0) {
                    boolean ended = true;

                    ItemStack[] results = actualRecipe.getResult();

                    ItemStack[] newResult = new ItemStack[getResults().length];
                    for (int i = 0; i < results.length; i++) {
                        if(getResults()[i] == null || getResults()[i].getType().equals(Material.AIR)) {
                            newResult[i] = results[i];
                            continue;
                        }
                        if(getResults()[i].isSimilar(results[i]) && getResults()[i].getAmount() + results[i].getAmount() <= getResults()[i].getMaxStackSize()) {
                            ItemStack resultItemStack = getResults()[i].clone();
                            resultItemStack.setAmount(getResults()[i].getAmount() + results[i].getAmount());
                            newResult[i] = resultItemStack;
                            continue;
                        }
                        ended = false;
                    }

                    if(ended) {
                        setProgress(0);
                        setProgressTime(0);
                        setActualRecipe(null);
                        setResults(newResult);

                        for(HumanEntity viewer : baseInventory.getViewers()) {
                            if(viewer instanceof Player) {
                                try {
                                    machineManager.getInventoryHelper().updateInventoryTitle((Player) viewer, baseMachine.getInventoryTitle());
                                } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                                         IllegalAccessException | NoSuchFieldException | InstantiationException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        checkAvailableRecipe();
                        cancel();
                    } else {
                    }
                    return;
                }
                setProgressTime(getProgressTime() - 10);
                setProgress(Math.round((Math.abs(getProgressTime() - recipe.getWorkTime())) / (recipe.getWorkTime() / getProgressBarLength())));

                StringBuilder invTitle = new StringBuilder(baseMachine.getInventoryTitle());

                if(getProgress() > 0) {
                    for (int i = 0; i < getProgress(); i++) {
                        invTitle.append(baseMachine.getProgressBarChars());
                    }
                }

                for(HumanEntity viewer : baseInventory.getViewers()) {
                    if(viewer instanceof Player) {
                        try {
                            machineManager.getInventoryHelper().updateInventoryTitle((Player) viewer, invTitle.toString());
                        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                                 IllegalAccessException | NoSuchFieldException | InstantiationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(machineManager.getPlugin(), 10,10);

    }

    public void checkAvailableRecipe() {
        if(getActualRecipe() == null) {
            for(String recipeName : getBaseMachine().getRecipesName()) {
                MachineRecipe recipe = getBaseMachine().getRecipe(recipeName);

                boolean available = true;

                for (int i = 0; i < getIngredients().length; i++) {
                    if(getIngredients()[i] == null) {
                        if(recipe.getIngredients() != null) {
                            available = false;
                            break;
                        }
                        continue;
                    }
                    if(!getIngredients()[i].isSimilar(recipe.getIngredients()[i]) ||
                    getIngredients()[i].getAmount() < recipe.getIngredients()[i].getAmount()) {
                        available = false;
                        break;
                    }
                }

                if(available) {
                    startRecipe(recipe);

                    ItemStack[] newIngredients = new ItemStack[recipe.getIngredients().length];

                    for (int i = 0; i < newIngredients.length; i++) {
                        if(getIngredients()[i] == null || getIngredients()[i].equals(Material.AIR)) {
                            newIngredients[i] = null;
                            continue;
                        }

                        if(getIngredients()[i].getAmount() - recipe.getIngredients()[i].getAmount() < 0) {
                            newIngredients[i] = null;
                            continue;
                        }

                        ItemStack ingredient = getIngredients()[i].clone();
                        ingredient.setAmount(ingredient.getAmount() - recipe.getIngredients()[i].getAmount());
                        newIngredients[i] = ingredient;
                    }

                    setIngredients(newIngredients);
                    break;
                }
            }
        }
    }
}
