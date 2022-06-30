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

package io.github.kpgtb.kkthirst;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.DataType;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.command.CommandManager;
import io.github.kpgtb.kkcore.manager.listener.ListenerManager;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.manager.DrinkManager;
import io.github.kpgtb.kkthirst.manager.MachineManager;
import io.github.kpgtb.kkthirst.manager.UserManager;
import io.github.kpgtb.kkthirst.object.BaseMachine;
import io.github.kpgtb.kkthirst.object.MachineRecipe;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import io.github.kpgtb.kkthirst.object.User;
import io.github.kpgtb.kkui.ui.FontWidth;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class KKthirst extends JavaPlugin {

    private MessageUtil messageUtil;
    private DataManager dataManager;
    private UserManager userManager;
    private DrinkManager drinkManager;
    private MachineManager machineManager;


    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("KKcore") == null) {
            System.out.println("KKthirst >> This plugin requires plugin KKcore");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if(Bukkit.getPluginManager().getPlugin("KKui") == null) {
            System.out.println("KKthirst >> This plugin requires plugin KKui");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        saveDefaultConfig();

        messageUtil = new MessageUtil("&2KKthirst&r");
        messageUtil.sendInfoToConsole("Enabling plugin KKthirst created by KPG-TB");

        Plugin core = Bukkit.getPluginManager().getPlugin("KKcore");

        //Register font
        FontWidth.registerCustomChar('\uA001', 9);
        FontWidth.registerCustomChar('\uA002', 9);
        FontWidth.registerCustomChar('\uA003', 9);
        FontWidth.registerCustomChar('\uA004', 9);
        FontWidth.registerCustomChar('\uA005', 9);
        FontWidth.registerCustomChar('\uA006', 9);

        LanguageManager languageManager = new LanguageManager(
                "KKthirst",
                core.getConfig().getString("language"),
                core.getDataFolder().getAbsolutePath(),
                getResource("en.yml"),
                messageUtil
        );
        languageManager.reloadMessages();


        dataManager = new DataManager(
                "KKthirst",
                DataType.valueOf(core.getConfig().getString("data.type").toUpperCase()),
                core.getDataFolder().getAbsolutePath(),
                messageUtil,
                getFile(),
                "defaultData/flat",
                getTextResource("defaultData/sql/default.txt"),
                this,
                core.getConfig()
        );

        userManager = new UserManager(this);

        drinkManager = new DrinkManager(messageUtil,dataManager,getConfig());
        drinkManager.registerDefaultDrinks();
        drinkManager.registerDrinksFromDB();

        machineManager = new MachineManager(dataManager,messageUtil, this);

        if(getConfig().getBoolean("registerFilterMachine")) {
            try {
                ItemStack filterMachineItemStack = new ItemStack(Material.valueOf(getConfig().getString("filterMachineItem.material").toUpperCase()));
                ItemMeta filterMachineMeta = filterMachineItemStack.getItemMeta();
                filterMachineMeta.setDisplayName(messageUtil.color(getConfig().getString("filterMachineItem.name")));

                ArrayList<String> lore = new ArrayList<>();
                getConfig().getStringList("filterMachineItem.lore").forEach(line -> {
                    lore.add(messageUtil.color(line));
                });
                filterMachineMeta.setLore(lore);

                filterMachineItemStack.setItemMeta(filterMachineMeta);


                // Inv title = Filter machine
                // pbOffset + pb - pbOffset - pbSize - 8 + gui image - gui image size + 8 &rTitle

                BaseMachine filterMachine = machineManager.registerMachine(
                        "filterMachine",
                        "Filter machine",
                        27,
                        new int[]{12},
                        new int[]{14},
                        '\uF901',
                        "\uF902\uF801",
                        1,
                        9,
                        75,
                        filterMachineItemStack,
                        true
                );

                if (getConfig().getBoolean("registerDefaultDrinks")) {
                    filterMachine.registerRecipe(
                            "dirty2cleanWater",
                            new MachineRecipe(
                                    "dirty2cleanWater",
                                    new ItemStack[]{drinkManager.getDrink("dirtyWater").getFinalDrink()},
                                    new ItemStack[]{drinkManager.getDrink("cleanWater").getFinalDrink()},
                                    100)
                    );
                }

                if(getConfig().getBoolean("registerFilterMachineCrafting")) {
                    ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(this, "filterMachineRecipe"), filterMachineItemStack);
                    shapedRecipe.shape("iwi", "ifi", "ili");
                    shapedRecipe.setIngredient('i', Material.IRON_INGOT);
                    shapedRecipe.setIngredient('f', Material.FLINT_AND_STEEL);
                    shapedRecipe.setIngredient('l', Material.OAK_LOG);
                    shapedRecipe.setIngredient('w', new RecipeChoice.ExactChoice(drinkManager.getDrink("dirtyWater").getFinalDrink()));
                    Bukkit.addRecipe(shapedRecipe);
                }
            } catch (Exception e) {
                messageUtil.sendErrorToConsole("Error while creating filter machine");
            }
        }

        machineManager.loadPlacedMachines();

        ThirstUsefulObjects thirstUsefulObjects = new ThirstUsefulObjects(
                messageUtil,
                languageManager,
                dataManager,
                getConfig(),
                userManager,
                drinkManager,
                machineManager
        );

        ListenerManager listenerManager = new ListenerManager(
                getFile(),
                this,
                thirstUsefulObjects
        );
        listenerManager.registerListeners("io.github.kpgtb.kkthirst.listener");

        CommandManager commandManager = new CommandManager(
                getFile(),
                "KKthirst",
                thirstUsefulObjects
        );
        commandManager.registerCommands("io.github.kpgtb.kkthirst.command");
    }

    @Override
    public void onDisable() {
        messageUtil.sendInfoToConsole("Disabling plugin KKthirst created by KPG-TB");

        for(User user : userManager.getUsers()) {
            user.save();
        }

        for(Location location : machineManager.getMachinesLocation()) {
            machineManager.saveMachine(machineManager.getPlacedMachine(location));
        }
        dataManager.closeConnection();
    }

    public DrinkManager getDrinkManager() {
        return drinkManager;
    }

    public MachineManager getMachineManager() {
        return machineManager;
    }

}
