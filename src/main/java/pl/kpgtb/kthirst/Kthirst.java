package pl.kpgtb.kthirst;

import com.github.kpgtb.ktools.Ktools;
import com.github.kpgtb.ktools.manager.command.CommandManager;
import com.github.kpgtb.ktools.manager.data.DataManager;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.manager.language.LanguageManager;
import com.github.kpgtb.ktools.manager.listener.ListenerManager;
import com.github.kpgtb.ktools.manager.resourcepack.ResourcepackManager;
import com.github.kpgtb.ktools.manager.ui.UiManager;
import com.github.kpgtb.ktools.manager.updater.SpigotUpdater;
import com.github.kpgtb.ktools.manager.updater.UpdaterManager;
import com.github.kpgtb.ktools.util.file.PackageUtil;
import com.github.kpgtb.ktools.util.wrapper.GlobalManagersWrapper;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import pl.kpgtb.kthirst.data.DbDrink;
import pl.kpgtb.kthirst.data.type.DrinkEffect;
import pl.kpgtb.kthirst.manager.drink.DrinkManager;
import pl.kpgtb.kthirst.manager.user.UserManager;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public final class Kthirst extends JavaPlugin {

    private BukkitAudiences adventure;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Ktools api = (Ktools) Bukkit.getPluginManager().getPlugin("Ktools");
        if(api == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        adventure = BukkitAudiences.create(this);
        GlobalManagersWrapper apiManagers = api.getGlobalManagersWrapper();

        PackageUtil packageUtil = new PackageUtil("pl.kpgtb.kthirst");

        LanguageManager language = new LanguageManager(
                getDataFolder(),
                getConfig().getString("lang"),
                apiManagers.getDebugManager(),
                apiManagers.getGlobalLanguageManager()
        );
        language.saveDefaultLanguage("lang/en.yml", this);
        language.refreshMessages();

        DataManager data = apiManagers.getDataManager();
        data.registerTables(packageUtil.get("data"), getFile());

        UiManager uiManager = apiManagers.getUiManager();
        uiManager.setRequired(true);

        UserManager userManager = new UserManager(this,uiManager);

        DrinkManager drinkManager = new DrinkManager();
        ThirstWrapper wrapper = new ThirstWrapper(apiManagers,language,this,adventure, drinkManager, userManager);
        drinkManager.setWrapper(wrapper);
        registerThirstDrinks(drinkManager,language);
        try {
            drinkManager.registerServerDrinks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ResourcepackManager resourcePack = wrapper.getResourcepackManager();
        resourcePack.setRequired(true);
        resourcePack.registerPlugin("Kthirst", getDescription().getVersion());
        {
            resourcePack.registerCustomChar("kthirst", "\uA001", "waterfull", getResource("resourcepack/waterfull.png"), 9, -16, 9);
            resourcePack.registerCustomChar("kthirst", "\uA002", "waterhalf", getResource("resourcepack/waterhalf.png"), 9, -16, 9);
            resourcePack.registerCustomChar("kthirst", "\uA003", "waterempty", getResource("resourcepack/waterempty.png"), 9, -16, 9);
            resourcePack.registerCustomChar("kthirst", "\uA004", "waterfull", getResource("resourcepack/waterfull.png"), 9, -5, 9);
            resourcePack.registerCustomChar("kthirst", "\uA005", "waterhalf", getResource("resourcepack/waterhalf.png"), 9, -5, 9);
            resourcePack.registerCustomChar("kthirst", "\uA006", "waterempty", getResource("resourcepack/waterempty.png"), 9, -5, 9);
            resourcePack.registerCustomChar("kthirst", "\uF901", "thirstmachinemenu", getResource("resourcepack/thirstmachinemenu.png"), 71, 13, 176);
            resourcePack.registerCustomChar("kthirst", "\uF902", "thirstmachineprogress", getResource("resourcepack/thirstmachineprogress.png"), 4, -29, 1);
        }

        ListenerManager listener = new ListenerManager(wrapper, getFile());
        listener.registerListeners(packageUtil.get("listener"));

        wrapper.getParamParserManager().registerParsers(packageUtil.get("parser"),getFile());
        CommandManager command = new CommandManager(wrapper,getFile(), "kthirst");
        command.registerCommands(packageUtil.get("command"));

        UpdaterManager updater = new UpdaterManager(getDescription(), new SpigotUpdater("103387"), wrapper.getDebugManager());
        updater.checkUpdate();
    }

    private void registerThirstDrinks(DrinkManager drinkManager, LanguageManager language) {
        DbDrink water = new DbDrink(
                "clean_water",
                10.0,
                new ArrayList<>(),
                language.getSingleString(LanguageLevel.PLUGIN, "cleanWaterName"),
                language.getString(LanguageLevel.PLUGIN, "cleanWaterLore"),
                Color.BLUE,
                150
        );
        drinkManager.registerAddon(water);

        DbDrink dirty = new DbDrink(
                "dirty_water",
                5.0,
                Arrays.asList(
                        new DrinkEffect(
                                PotionEffectType.WEAKNESS.getName(),
                                1200,
                                1
                        ),
                        new DrinkEffect(
                                PotionEffectType.POISON.getName(),
                                200,
                                0
                        )
                ),
                language.getSingleString(LanguageLevel.PLUGIN, "dirtyWaterName"),
                language.getString(LanguageLevel.PLUGIN, "dirtyWaterLore"),
                new Color(44, 18, 2),
                151
        );
        drinkManager.registerAddon(dirty);
    }

    @Override
    public void onDisable() {
        if(adventure != null) {
            adventure.close();
        }
    }
}
