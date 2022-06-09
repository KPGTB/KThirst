package io.github.kpgtb.kkthirst;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.DataType;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.listener.ListenerManager;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.manager.UserManager;
import io.github.kpgtb.kkui.ui.FontWidth;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

//TODO:
// ActionBar
// Req TXT
// Machines
// Drinks

public final class KKthirst extends JavaPlugin {

    private MessageUtil messageUtil;
    private DataManager dataManager;
    private UserManager userManager;

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
        FontWidth.registerCustomChar('\uA001', 8);
        FontWidth.registerCustomChar('\uA002', 8);
        FontWidth.registerCustomChar('\uA003', 8);

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

        ThirstUsefulObjects thirstUsefulObjects = new ThirstUsefulObjects(
                messageUtil,
                languageManager,
                dataManager,
                getConfig(),
                userManager
        );

        ListenerManager listenerManager = new ListenerManager(
                getFile(),
                this,
                thirstUsefulObjects
        );
        listenerManager.registerListeners("io.github.kpgtb.kkthirst.listener");
    }

    @Override
    public void onDisable() {
        messageUtil.sendInfoToConsole("Disabling plugin KKthirst created by KPG-TB");

        for(User user : userManager.getUsers()) {
            user.save();
        }
        dataManager.closeConnection();
    }
}
