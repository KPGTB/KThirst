package pl.kpgtb.kkthirst;

import com.github.kpgtb.ktools.Ktools;
import com.github.kpgtb.ktools.manager.command.CommandManager;
import com.github.kpgtb.ktools.manager.data.DataManager;
import com.github.kpgtb.ktools.manager.language.LanguageManager;
import com.github.kpgtb.ktools.manager.listener.ListenerManager;
import com.github.kpgtb.ktools.manager.resourcepack.ResourcepackManager;
import com.github.kpgtb.ktools.util.GlobalManagersWrapper;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.kpgtb.kkthirst.util.ThirstWrapper;

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
    }

    @Override
    public void onDisable() {
        if(adventure != null) {
            adventure.close();
        }
    }
}
