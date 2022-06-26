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

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Set;

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
    public Set<String> getRecipesName() {return recipes.keySet();}
}
