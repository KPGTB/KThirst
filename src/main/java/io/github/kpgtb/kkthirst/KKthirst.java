package io.github.kpgtb.kkthirst;

import com.google.gson.Gson;
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
import io.github.kpgtb.kkthirst.util.ItemStackSaver;
import io.github.kpgtb.kkui.ui.FontWidth;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

//TODO:
// CMD version
// No res request 1.13.2
// Points in water
// License to all UI and Thirst

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

        ItemStack filterMachineItemStack = new ItemStack(Material.CAULDRON);
        ItemMeta filterMachineMeta = filterMachineItemStack.getItemMeta();
        filterMachineMeta.setDisplayName("&cFilter Machine");
        filterMachineItemStack.setItemMeta(filterMachineMeta);

        BaseMachine filterMachine = machineManager.registerMachine(
                "filterMachine",
                "\uF808§f\uF901\uF80C\uF80A\uF808§rFilter machine§f\uF825",
                27,
                new int[]{12},
                new int[]{14},
                '\uF901',
                "\uF902\uF801",
                9,
                filterMachineItemStack,
                true
        );

        if(getConfig().getBoolean("registerDefaultDrinks")) {
            filterMachine.registerRecipe(
                    "dirty2cleanWater",
                    new MachineRecipe(
                            "dirty2cleanWater",
                            new ItemStack[]{drinkManager.getDrink("dirtyWater").getFinalDrink()},
                            new ItemStack[]{drinkManager.getDrink("cleanWater").getFinalDrink()},
                            100)
            );
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
