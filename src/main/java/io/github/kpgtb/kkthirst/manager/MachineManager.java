package io.github.kpgtb.kkthirst.manager;

import com.google.gson.Gson;
import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.object.BaseMachine;
import io.github.kpgtb.kkthirst.object.MachineRecipe;
import io.github.kpgtb.kkthirst.object.PlacedMachine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Set;

public class MachineManager {
    private final HashMap<String, BaseMachine> machines;
    private final HashMap<Location, PlacedMachine> placedMachines;

    private final DataManager dataManager;
    private final MessageUtil messageUtil;

    public MachineManager(DataManager dataManager, MessageUtil messageUtil) {
        this.dataManager = dataManager;
        this.messageUtil = messageUtil;

        machines = new HashMap<>();
        placedMachines = new HashMap<>();
    }

    public BaseMachine registerMachine(String machineType, String inventoryTitle, int inventorySize, int[] ingredientSlots, int[] resultSlots, Character customInventoryChar, String progressBarChars, int progressBarLength, ItemStack machineItemStack, boolean replace) {
        BaseMachine baseMachine = new BaseMachine(machineType,inventoryTitle, inventorySize, ingredientSlots, resultSlots,customInventoryChar,progressBarChars, progressBarLength, machineItemStack);

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
                location.getBlock().setType(machine.getMachineItemStack().getType());
            }

            String ingredientsRawString = (String) dataManager.get("machines", key, "ingredients");
            String[] ingredientsRaw = ingredientsRawString.split("<new_ingredient>");

            ItemStack[] ingredients = new ItemStack[machine.getIngredientSlots().length];
            Gson gson = new Gson();
            int ingredientsI = 0;

            for(String ingredientRaw : ingredientsRaw) {
                if(ingredientRaw.equalsIgnoreCase("<empty>")) {
                    ingredients[ingredientsI] = null;
                } else if(ingredientRaw.isEmpty()) {
                    continue;
                }else {
                    ItemStack ingredient = gson.fromJson(ingredientRaw, ItemStack.class);
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
                    ItemStack result = gson.fromJson(resultRaw, ItemStack.class);
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

            PlacedMachine placedMachine = new PlacedMachine(location,type,inventory,ingredients,results,actualRecipe,progressTime,progressBarLength);
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
                machine.getProgressBarLength());

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

            Gson gson = new Gson();
            StringBuilder ingredientsRaw = new StringBuilder();

            for (ItemStack ingredient : machine.getIngredients()) {
                String ingredientRaw = gson.toJson(ingredient);
                if(ingredientRaw == null || ingredientRaw.equalsIgnoreCase("null")) {
                    ingredientRaw = "<empty>";
                }
                ingredientsRaw.append(ingredientRaw).append("<new_ingredient>");
            }

            dataManager.set("machines", key, "ingredients", ingredientsRaw.toString());

            StringBuilder resultsRaw = new StringBuilder();

            for (ItemStack result : machine.getResults()) {
                String resultRaw = gson.toJson(result);
                if(resultRaw == null || resultRaw.equalsIgnoreCase("null")) {
                    resultRaw = "<empty>";
                }
                resultsRaw.append(resultRaw).append("<new_result>");
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
            return false;
        }
        return true;
    }
    public PlacedMachine getPlacedMachine(Location location) {return placedMachines.get(location);}


}