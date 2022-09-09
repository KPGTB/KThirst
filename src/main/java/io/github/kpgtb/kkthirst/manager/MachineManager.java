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

package io.github.kpgtb.kkthirst.manager;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.nms.*;
import io.github.kpgtb.kkthirst.object.BaseMachine;
import io.github.kpgtb.kkthirst.object.MachineRecipe;
import io.github.kpgtb.kkthirst.object.PlacedMachine;
import io.github.kpgtb.kkthirst.util.ItemStackSaver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Set;

public class MachineManager {
    private final HashMap<String, BaseMachine> machines;
    private final HashMap<Location, PlacedMachine> placedMachines;

    private final DataManager dataManager;
    private final MessageUtil messageUtil;
    private final JavaPlugin plugin;

    public MachineManager(DataManager dataManager, MessageUtil messageUtil, JavaPlugin plugin) {
        this.dataManager = dataManager;
        this.messageUtil = messageUtil;
        this.plugin = plugin;

        machines = new HashMap<>();
        placedMachines = new HashMap<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Location location : getMachinesLocation()) {
                    saveMachine(getPlacedMachine(location));
                }
            }
        }.runTaskTimerAsynchronously(plugin, plugin.getConfig().getInt("saveTimeTicks"), plugin.getConfig().getInt("saveTimeTicks"));
    }

    public BaseMachine registerMachine(String machineType, String inventoryTitle, int inventorySize, int[] ingredientSlots, int[] resultSlots, Character customInventoryChar, String progressBarChars,int progressBarCharSize, int progressBarLength,int progressBarOffset, ItemStack machineItemStack, boolean replace) {
        BaseMachine baseMachine = new BaseMachine(machineType,inventoryTitle, inventorySize, ingredientSlots, resultSlots,customInventoryChar,progressBarChars, progressBarCharSize,progressBarLength, progressBarOffset, machineItemStack);

        if(machines.containsKey(machineType)) {
            if(replace) {
                machines.remove(machineType);
            } else {
                return null;
            }
        }

        machines.put(machineType, baseMachine);
        return baseMachine;
    }
    public void unregisterMachine(String machineType) {
        machines.remove(machineType);
    }
    public BaseMachine getMachine(String machineType) {
        return machines.get(machineType);
    }
    public BaseMachine getMachineFromItemStack(ItemStack itemStack) {
        for(BaseMachine machine : machines.values()) {
            if(itemStack.isSimilar(machine.getMachineItemStack())) {
                return machine;
            }
        }
        return null;
    }
    public Set<String> getMachinesName() {return machines.keySet();}

    public void loadPlacedMachines() {
        for(Object key : dataManager.getKeys("machines")) {
            loadPlacedMachine((String) key);
        }
    }
    public void loadPlacedMachine(String key) {
        try {
            String[] locationRaw = key.split(", ");
            int x = Integer.parseInt(locationRaw[0]);
            int y = Integer.parseInt(locationRaw[1]);
            int z = Integer.parseInt(locationRaw[2]);
            String worldName = locationRaw[3];

            World world = Bukkit.getWorld(worldName);
            Location location = new Location(world,x,y,z);

            String type = (String) dataManager.get("machines", key, "machineType");
            BaseMachine machine = getMachine(type);

            if(machine == null) {
                throw new IllegalArgumentException();
            }

            if(location.getBlock().getType() != machine.getMachineItemStack().getType()) {
                dataManager.delete("machines", locationRaw);
                return;
            }

            String ingredientsRawString = (String) dataManager.get("machines", key, "ingredients");
            String[] ingredientsRaw = ingredientsRawString.split("<new_ingredient>");

            ItemStack[] ingredients = new ItemStack[machine.getIngredientSlots().length];
            int ingredientsI = 0;

            for(String ingredientRaw : ingredientsRaw) {
                if(ingredientRaw.equalsIgnoreCase("<empty>")) {
                    ingredients[ingredientsI] = null;
                } else if(ingredientRaw.isEmpty()) {
                    continue;
                }else {
                    ItemStack ingredient = new ItemStackSaver().load(ingredientRaw);
                    ingredients[ingredientsI] = ingredient;
                }
                ingredientsI++;
            }

            String resultsRawString = (String) dataManager.get("machines", key, "results");
            String[] resultsRaw = resultsRawString.split("<new_result>");

            ItemStack[] results = new ItemStack[machine.getResultSlots().length];
            int resultsI = 0;

            for(String resultRaw : resultsRaw) {
                if(resultRaw.equalsIgnoreCase("<empty>") ) {
                    results[resultsI] = null;
                } else if(resultRaw.isEmpty()){
                    continue;
                }else {
                    ItemStack result = new ItemStackSaver().load(resultRaw);
                    results[resultsI] = result;
                }
                resultsI++;
            }

            String actualRecipeName = (String) dataManager.get("machines", key, "actualRecipeName");
            MachineRecipe actualRecipe = machine.getRecipe(actualRecipeName);

            int progressTime = (int) dataManager.get("machines", key, "progressTime");
            int progressBarLength = machine.getProgressBarLength();
            int inventorySize = machine.getInventorySize();
            String inventoryTitle = machine.getInventoryTitle();

            Inventory inventory = Bukkit.createInventory(null,inventorySize,inventoryTitle);
            for (int i = 0; i < ingredients.length; i++) {
                if(ingredients[i] != null) {
                    inventory.setItem(machine.getIngredientSlots()[i], ingredients[i]);
                }
            }
            for (int i = 0; i < results.length; i++) {
                if(results[i] != null) {
                    inventory.setItem(machine.getResultSlots()[i], results[i]);
                }
            }

            PlacedMachine placedMachine = new PlacedMachine(location,type,inventory,ingredients,results,actualRecipe,progressTime,progressBarLength, this);
            placedMachines.put(location,placedMachine);

        } catch (Exception e) {
            messageUtil.sendErrorToConsole("Error while loading machine with location " + key);
            e.printStackTrace();
        }
    }
    public boolean placeMachine(String machineType, Location location) {
        BaseMachine machine = getMachine(machineType);

        if(machine == null) {
            messageUtil.sendErrorToConsole("Error while placing machine " + machineType);
            return false;
        }
        PlacedMachine placedMachine = new PlacedMachine(
                new Location(location.getWorld(), location.getBlockX(),location.getBlockY(),location.getBlockZ()),
                machineType,
                Bukkit.createInventory(null,machine.getInventorySize(), machine.getInventoryTitle()),
                new ItemStack[machine.getIngredientSlots().length],
                new ItemStack[machine.getResultSlots().length],
                null,
                0,
                machine.getProgressBarLength(),
                this);

        if(placedMachines.containsKey(location)) {
            placedMachines.remove(location);
        }
        saveMachine(placedMachine);
        placedMachines.put(location, placedMachine);
        return true;
    }
    public boolean destroyMachine(Location location) {
        if(!placedMachines.containsKey(location)) {
            return false;
        }
        placedMachines.get(location).getBaseInventory().getViewers().forEach(HumanEntity::closeInventory);
        location.getBlock().setType(Material.AIR);
        placedMachines.remove(location);
        String locationRaw = location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", " + location.getWorld().getName();
        dataManager.delete("machines", locationRaw);
        return true;
    }
    public boolean saveMachine(PlacedMachine machine) {
        try {
            Location location = machine.getLocation();
            String key = location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", " + location.getWorld().getName();

            dataManager.set("machines", key, "machineType", machine.getType());

            StringBuilder ingredientsRaw = new StringBuilder();

            for (ItemStack ingredient : machine.getIngredients()) {
                if(ingredient == null || ingredient.getType().equals(Material.AIR)) {
                    ingredientsRaw.append("<empty>").append("<new_ingredient>");
                    continue;
                }
                ingredientsRaw.append(new ItemStackSaver().save(ingredient)).append("<new_ingredient>");
            }

            dataManager.set("machines", key, "ingredients", ingredientsRaw.toString());

            StringBuilder resultsRaw = new StringBuilder();

            for (ItemStack result : machine.getResults()) {
                if(result == null || result.getType().equals(Material.AIR)) {
                    resultsRaw.append("<empty>").append("<new_result>");
                    continue;
                }
                resultsRaw.append(new ItemStackSaver().save(result)).append("<new_result>");
            }

            dataManager.set("machines", key, "results", resultsRaw.toString());

            String actualRecipeName;

            if(machine.getActualRecipe() == null) {
                actualRecipeName = "";
            } else {
                actualRecipeName = machine.getActualRecipe().getRecipeName();
            }

            dataManager.set("machines", key, "actualRecipeName", actualRecipeName);
            dataManager.set("machines", key, "progressTime", machine.getProgressTime());
        }catch (Exception e) {
            messageUtil.sendErrorToConsole("Error while saving machine " + machine.getLocation().toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public PlacedMachine getPlacedMachine(Location location) {return placedMachines.get(location);}
    public Set<Location> getMachinesLocation() {return placedMachines.keySet();}

    public IInventoryHelper getInventoryHelper() {
        IInventoryHelper result;
        switch (Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]) {
            case "v1_14_R1":
            case "v1_16_R3":
            case "v1_16_R2":
            case "v1_16_R1":
            case "v1_15_R1":
                result = new InventoryHelper_1_14_1_16();
                break;
            case "v1_17_R1":
                result = new InventoryHelper_1_17();
                break;
            case "v1_18_R1":
                result = new InventoryHelper_1_18();
                break;
            case "v1_18_R2":
                result = new InventoryHelper_1_18_2();
                break;
            case "v1_19_R1":
                result =  new InventoryHelper_1_19();
                break;
            default:
                result =  new InventoryHelper_1_13();
                break;
        }
        return result;
    }

    public void saveItems(Inventory inv, PlacedMachine placedMachine) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack[] newIngredients = new ItemStack[placedMachine.getBaseMachine().getIngredientSlots().length];
                for (int i = 0; i < newIngredients.length; i++) {
                    newIngredients[i] = inv.getItem(placedMachine.getBaseMachine().getIngredientSlots()[i]);
                }
                ItemStack[] newResults = new ItemStack[placedMachine.getBaseMachine().getResultSlots().length];
                for (int i = 0; i < newResults.length; i++) {
                    newResults[i] = inv.getItem(placedMachine.getBaseMachine().getResultSlots()[i]);
                }
                placedMachine.setIngredients(newIngredients);
                placedMachine.setResults(newResults);
            }
        }.runTaskLaterAsynchronously(plugin, 10);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
