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
