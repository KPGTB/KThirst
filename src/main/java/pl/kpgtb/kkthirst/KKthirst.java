package pl.kpgtb.kkthirst;

import com.github.kpgtb.ktools.Ktools;
import com.github.kpgtb.ktools.manager.command.CommandManager;
import com.github.kpgtb.ktools.manager.data.DataManager;
import com.github.kpgtb.ktools.manager.language.LanguageManager;
import com.github.kpgtb.ktools.manager.listener.ListenerManager;
import com.github.kpgtb.ktools.manager.resourcepack.ResourcepackManager;
import com.github.kpgtb.ktools.util.GlobalManagersWrapper;
import com.j256.ormlite.dao.Dao;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.kpgtb.kkthirst.data.DbDrink;
import pl.kpgtb.kkthirst.data.type.DrinkEffect;
import pl.kpgtb.kkthirst.utils.ThirstWrapper;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public final class KKthirst extends JavaPlugin {

    private BukkitAudiences adventure;
    private final String MAIN_PACKAGE = "pl.kpgtb.kkthirst.%s";

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

        LanguageManager language = new LanguageManager(
                getDataFolder(),
                getConfig().getString("lang"),
                apiManagers.getDebugManager(),
                apiManagers.getGlobalLanguageManager()
        );
        language.saveDefaultLanguage("lang/en.yml", this);
        language.refreshMessages();

        ThirstWrapper wrapper = new ThirstWrapper(apiManagers,language,this,adventure);

        ResourcepackManager resourcePack = wrapper.getResourcepackManager();
        resourcePack.setRequired(true);
        resourcePack.registerPlugin("KKthirst", getDescription().getVersion());
        resourcePack.registerCustomChar("kkthirst", "\uA001", "waterfull", getResource("resourcepack/waterfull.png"),9,-16,9);
        resourcePack.registerCustomChar("kkthirst", "\uA002", "waterhalf", getResource("resourcepack/waterhalf.png"),9,-16,9);
        resourcePack.registerCustomChar("kkthirst", "\uA003", "waterempty", getResource("resourcepack/waterempty.png"),9,-16,9);
        resourcePack.registerCustomChar("kkthirst", "\uA004", "waterfull", getResource("resourcepack/waterfull.png"),9,-5,9);
        resourcePack.registerCustomChar("kkthirst", "\uA005", "waterhalf", getResource("resourcepack/waterhalf.png"),9,-5,9);
        resourcePack.registerCustomChar("kkthirst", "\uA006", "waterempty", getResource("resourcepack/waterempty.png"),9,-5,9);
        resourcePack.registerCustomChar("kkthirst", "\uF901", "thirstmachinemenu", getResource("resourcepack/thirstmachinemenu.png"),71,13,176);
        resourcePack.registerCustomChar("kkthirst", "\uF902", "thirstmachineprogress", getResource("resourcepack/thirstmachineprogress.png"),4,-29,1);

        DataManager data = wrapper.getDataManager();
        data.registerTables(String.format(MAIN_PACKAGE, "data"), getFile());

        ListenerManager listener = new ListenerManager(wrapper, getFile());
        listener.registerListeners(String.format(MAIN_PACKAGE, "listener"));

        CommandManager command = new CommandManager(wrapper,getFile(), "kkthirst");
        command.registerCommands(String.format(MAIN_PACKAGE, "command"));

        new BukkitRunnable() {
            @Override
            public void run() {

                Dao<DbDrink, String> drinksDAO = data.getDao(DbDrink.class, String.class);

                DbDrink drink = new DbDrink();
                drink.setCode("test");
                drink.setColor(new Color(182,162,15));
                drink.setLore(Arrays.asList("First line", "Second line"));
                drink.setName("Awesome drink name");
                drink.setEffects(Arrays.asList(
                    new DrinkEffect("TEST_EFFECT", 20, 13),
                    new DrinkEffect("AWESOME_EFFECT", 10, 2)
                ));
                drink.setCustomModelData(10);

                DbDrink drink2 = new DbDrink();
                drink2.setCode("CleanDrink");
                drink2.setColor(new Color(0,0,0));
                drink2.setName("Second name");
                drink2.setLore(new ArrayList<>());
                drink2.setEffects(new ArrayList<>());
                drink2.setCustomModelData(0);

                try {
                    drinksDAO.create(drink);
                    drinksDAO.create(drink2);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskLater(this, 1);
    }

    @Override
    public void onDisable() {
        if(adventure != null) {
            adventure.close();
        }
    }
}
