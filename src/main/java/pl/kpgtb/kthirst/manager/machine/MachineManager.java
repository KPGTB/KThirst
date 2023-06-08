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

package pl.kpgtb.kthirst.manager.machine;

import com.github.kpgtb.ktools.manager.data.DataManager;
import com.github.kpgtb.ktools.manager.item.ItemManager;
import com.github.kpgtb.ktools.manager.item.builder.KitemBuilder;
import com.github.kpgtb.ktools.manager.language.LanguageManager;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.kpgtb.kthirst.data.DbMachine;
import pl.kpgtb.kthirst.nms.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class MachineManager {
    private final HashMap<String, BaseMachine> machines;
    private final HashMap<Location, PlacedMachine> placedMachines;

    private ToolsObjectWrapper wrapper;
    private final JavaPlugin plugin;
    private final Dao<DbMachine, Integer> machinesDAO;

    public MachineManager(DataManager dataManager, LanguageManager language, JavaPlugin plugin) {
        this.plugin = plugin;

        machines = new HashMap<>();
        placedMachines = new HashMap<>();
        this.machinesDAO = dataManager.getDao(DbMachine.class, Integer.class);

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Location location : getMachinesLocation()) {
                    saveMachine(getPlacedMachine(location));
                }
            }
        }.runTaskTimerAsynchronously(plugin,1200,1200);
    }

    public void setWrapper(ToolsObjectWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public BaseMachine registerMachine(String machineType, String inventoryTitle, int inventorySize, int[] ingredientSlots, int[] resultSlots, Character customInventoryChar, String progressBarChars, int progressBarCharSize, int progressBarLength, int progressBarOffset, ItemStack machineItemStack, boolean replace) {
        BaseMachine baseMachine = new BaseMachine(machineType,inventoryTitle, inventorySize, ingredientSlots, resultSlots,customInventoryChar,progressBarChars, progressBarCharSize,progressBarLength, progressBarOffset, machineItemStack.getType());

        if(machines.containsKey(machineType)) {
            if(replace) {
                machines.remove(machineType);
                wrapper.getItemManager().unregisterItem("kthirst:"+machineType);
            } else {
                return null;
            }
        }

        KitemBuilder builder = new KitemBuilder(wrapper,"kthirst", machineType, machineItemStack);
        builder.register();

        machines.put(machineType, baseMachine);
        return baseMachine;
    }
    public void unregisterMachine(String machineType) {
        machines.remove(machineType);
    }
    public BaseMachine getMachine(String machineType) {
        return machines.get(machineType);
    }
    public Set<String> getMachinesName() {return machines.keySet();}

    public void loadPlacedMachines() throws SQLException {
        for(DbMachine machine : machinesDAO.queryForAll()) {
            loadPlacedMachine(machine);
        }
    }
    public void loadPlacedMachine(DbMachine dbMachine) {
        try {
            Location location = dbMachine.getLocation();

            BaseMachine machine = getMachine(dbMachine.getType());

            if(machine == null) {
                throw new IllegalArgumentException();
            }

            if(location.getBlock().getType() != machine.getMaterial()) {
                machinesDAO.delete(dbMachine);
                return;
            }

            ItemStack[] ingredients = dbMachine.getIngredients().toArray(new ItemStack[0]);
            ItemStack[] results = dbMachine.getResults().toArray(new ItemStack[0]);

            MachineRecipe actualRecipe = machine.getRecipe(dbMachine.getActualRecipeName());

            int progressTime = dbMachine.getProgressTime();
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

            PlacedMachine placedMachine = new PlacedMachine(location, dbMachine.getType(), inventory,ingredients,results,actualRecipe,progressTime,progressBarLength, this);
            placedMachines.put(location,placedMachine);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public boolean placeMachine(String machineType, Location location) {
        BaseMachine machine = getMachine(machineType);

        if(machine == null) {
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
    public boolean destroyMachine(Location location) throws SQLException {
        if(!placedMachines.containsKey(location)) {
            return false;
        }
        placedMachines.get(location).getBaseInventory().getViewers().forEach(HumanEntity::closeInventory);
        location.getBlock().setType(Material.AIR);
        placedMachines.remove(location);
        DeleteBuilder<?,?> deleteBuilder = machinesDAO.deleteBuilder();
        deleteBuilder.where().eq("location", location);
        deleteBuilder.delete();
        return true;
    }
    public boolean saveMachine(PlacedMachine machine) {
        try {
            Location location = machine.getLocation();

            Optional<DbMachine> dbMachineOptional = machinesDAO.queryForEq("location", location).stream().findFirst();
            DbMachine dbMachine = new DbMachine();
            boolean newMachine = true;
            if(dbMachineOptional.isPresent()) {
                dbMachine = dbMachineOptional.get();
                newMachine = false;
            }

            dbMachine.setType(machine.getType());
            dbMachine.setLocation(location);
            dbMachine.setIngredients(Arrays.asList(machine.getIngredients()));
            dbMachine.setResults(Arrays.asList(machine.getResults()));
            String actualRecipeName;

            if(machine.getActualRecipe() == null) {
                actualRecipeName = "";
            } else {
                actualRecipeName = machine.getActualRecipe().getRecipeName();
            }

            dbMachine.setActualRecipeName(actualRecipeName);
            dbMachine.setProgressTime(machine.getProgressTime());

            if(newMachine) {
                machinesDAO.create(dbMachine);
            } else {
                machinesDAO.update(dbMachine);
            }

        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public PlacedMachine getPlacedMachine(Location location) {return placedMachines.get(location);}
    public Set<Location> getMachinesLocation() {return placedMachines.keySet();}

    public IInventoryHelper getInventoryHelper() {
        IInventoryHelper result;
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        switch (version) {
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
            case "v1_19_R2":
                result = new InventoryHelper_1_19_2();
                break;
            case "v1_19_R3":
                result = new InventoryHelper_1_19_3();
                break;
            case "v1_20_R1":
                result = new InventoryHelper_1_20();
                break;
            default:
                result =  null;
                break;
        }
        if(result == null && version.startsWith("v1_2")) {
            result = new InventoryHelper_1_20();
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
    public BaseMachine getMachineFromItemStack(ItemStack itemStack) {
        for(BaseMachine machine : machines.values()) {
            String tag = "kthirst:"+machine.getType();
            ItemStack item = wrapper.getItemManager().getCustomItem(tag);
            if(item == null) {
                continue;
            }
            if(item.isSimilar(itemStack)) {
                return machine;
            }
        }
        return null;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
