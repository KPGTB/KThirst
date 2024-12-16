package pl.kpgtb.kthirst;

import com.github.kpgtb.ktools.manager.command.CommandManager;
import com.github.kpgtb.ktools.manager.data.DataManager;
import com.github.kpgtb.ktools.manager.debug.DebugType;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.manager.language.LanguageManager;
import com.github.kpgtb.ktools.manager.listener.ListenerManager;
import com.github.kpgtb.ktools.manager.recipe.RecipeManager;
import com.github.kpgtb.ktools.manager.resourcepack.ResourcePackManager;
import com.github.kpgtb.ktools.manager.ui.bar.BarIcons;
import com.github.kpgtb.ktools.manager.ui.bar.BarManager;
import com.github.kpgtb.ktools.manager.ui.bar.KBar;
import com.github.kpgtb.ktools.manager.ui.bar.save.PlayerCacheMethod;
import com.github.kpgtb.ktools.manager.updater.SpigotUpdater;
import com.github.kpgtb.ktools.manager.updater.UpdaterManager;
import com.github.kpgtb.ktools.util.bstats.Metrics;
import com.github.kpgtb.ktools.util.file.PackageUtil;
import com.github.kpgtb.ktools.util.item.ItemBuilder;
import com.github.kpgtb.ktools.util.wrapper.ToolsInitializer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import pl.kpgtb.kthirst.data.DbDrink;
import pl.kpgtb.kthirst.data.type.DrinkEffect;
import pl.kpgtb.kthirst.manager.drink.DrinkManager;
import pl.kpgtb.kthirst.manager.machine.BaseMachine;
import pl.kpgtb.kthirst.manager.machine.MachineManager;
import pl.kpgtb.kthirst.manager.machine.MachineRecipe;
import pl.kpgtb.kthirst.manager.user.UserManager;
import pl.kpgtb.kthirst.placeholder.ThirstPlaceholder;
import pl.kpgtb.kthirst.util.ConfigUtil;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public final class KThirst extends JavaPlugin {

    private BukkitAudiences adventure;
    private MachineManager machineManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ToolsInitializer initializer = new ToolsInitializer(this)
                .prepareLanguage(getConfig().getString("lang"), "en");

        adventure = initializer.getAdventure();
        PackageUtil packageUtil = initializer.getPackageUtil();
        LanguageManager language = initializer.getLanguageManager();

        DataManager data = initializer.getGlobalManagersWrapper().getDataManager();
        data.registerTables(packageUtil.get("data"), getFile());

        ResourcePackManager resourcePack = initializer.getGlobalManagersWrapper().getResourcePackManager();
        BarManager barManager = initializer.getGlobalManagersWrapper().getBarManager();

        KBar thirstBar = new KBar(
                "thirst",
                new PlayerCacheMethod(),
                Arrays.asList(new BarIcons(
                        0.0,
                        Double.MAX_VALUE,
                        this,
                        "resourcepack/waterfull.png",
                        "resourcepack/waterhalf.png",
                        "resourcepack/waterempty.png",
                        9,
                        10
                )),
                640614385,
                20.0,
                20.0,
                true,
                true,
                true
        );
        barManager.registerPlugin(packageUtil.getTag(), getDescription().getVersion());
        barManager.registerBar(thirstBar);

        resourcePack.setRequired(true);
        resourcePack.registerCustomChar(packageUtil.getTag(), "\uF901", "thirstmachinemenu.png", getResource("resourcepack/thirstmachinemenu.png"), 71, 13, 176.0);
        resourcePack.registerCustomChar(packageUtil.getTag(), "\uF902", "thirstmachineprogress.png", getResource("resourcepack/thirstmachineprogress.png"), 4, -29, 1.0);

        DrinkManager drinkManager = new DrinkManager();
        machineManager = new MachineManager(data,language,this);
        UserManager userManager = new UserManager(this, thirstBar, barManager);
        userManager.prepare();

        ThirstWrapper wrapper = new ThirstWrapper( initializer, drinkManager, userManager, machineManager, thirstBar);

        {
            drinkManager.setWrapper(wrapper);
            registerThirstDrinks(drinkManager, language);
            try {
                drinkManager.registerServerDrinks();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        {
            machineManager.setWrapper(wrapper);
            try {
                ItemStack filterMachineItemStack = new ItemBuilder(Material.CAULDRON)
                        .displayname(language.getSingleString(LanguageLevel.PLUGIN, "filterItemName"))
                        .model(153)
                        .lore(language.getString(LanguageLevel.PLUGIN, "filterItemLore"))
                        .build();

                BaseMachine filterMachine = machineManager.registerMachine(
                        "filter_machine",
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

                filterMachine.registerRecipe(
                        "dirty2cleanWater",
                        new MachineRecipe(
                                "dirty2cleanWater",
                                new ItemStack[]{wrapper.getItemManager().getCustomItem(packageUtil.getTag(), "dirty_water")},
                                new ItemStack[]{wrapper.getItemManager().getCustomItem(packageUtil.getTag(), "clean_water")},
                                100)
                );

                machineManager.loadPlacedMachines();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ListenerManager listener = new ListenerManager(wrapper, getFile());
        listener.registerListeners(packageUtil.get("listener"));

        wrapper.getParamParserManager().registerParsers(packageUtil.get("parser"),getFile());
        CommandManager command = new CommandManager(wrapper,getFile(), packageUtil.getTag());
        command.registerCommands(packageUtil.get("command"));

        RecipeManager recipeManager = new RecipeManager(wrapper,getFile(),packageUtil.getTag());
        recipeManager.registerRecipes(packageUtil.get("recipe"));

        if(machineManager.getInventoryHelper() == null) {
            wrapper.getDebugManager().sendWarning(DebugType.START, "This version is not supported by KThirst!", true);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ThirstPlaceholder(thirstBar, barManager).register();
        }

        UpdaterManager updater = new UpdaterManager(getDescription(), new SpigotUpdater("103387"), wrapper.getDebugManager());
        updater.checkUpdate();

        new Metrics(this,18407);
    }

    private void registerThirstDrinks(DrinkManager drinkManager, LanguageManager language) {
        ConfigurationSection waterSection = getConfig().getConfigurationSection("default_drinks.clean");
        DbDrink water = new DbDrink(
                "clean_water",
                waterSection.getDouble("points"),
                ConfigUtil.getEffects(waterSection.getStringList("effects")),
                language.getSingleString(LanguageLevel.PLUGIN, "cleanWaterName"),
                language.getString(LanguageLevel.PLUGIN, "cleanWaterLore"),
                ConfigUtil.getColor(waterSection.getConfigurationSection("color")),
                150
        );
        drinkManager.registerAddon(water);

        ConfigurationSection dirtySection = getConfig().getConfigurationSection("default_drinks.dirty");
        DbDrink dirty = new DbDrink(
                "dirty_water",
                dirtySection.getDouble("points"),
                ConfigUtil.getEffects(dirtySection.getStringList("effects")),
                language.getSingleString(LanguageLevel.PLUGIN, "dirtyWaterName"),
                language.getString(LanguageLevel.PLUGIN, "dirtyWaterLore"),
                ConfigUtil.getColor(dirtySection.getConfigurationSection("color")),
                151
        );
        drinkManager.registerAddon(dirty);
    }

    @Override
    public void onDisable() {
        if(adventure != null) {
            adventure.close();
        }

        if(machineManager != null) {
            for(Location location : machineManager.getMachinesLocation()) {
                machineManager.saveMachine(machineManager.getPlacedMachine(location));
            }
        }
    }
}
