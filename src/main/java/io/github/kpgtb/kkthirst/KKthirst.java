package io.github.kpgtb.kkthirst;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.DataType;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.listener.ListenerManager;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.manager.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

//TODO:
// ActionBar
// Req TXT
// Machines
// Drinks

public final class KKthirst extends JavaPlugin {

    private MessageUtil messageUtil;
    private DataManager dataManager;

    public static HashMap<UUID, User> users = new HashMap<>();

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("KKcore") == null) {
            System.out.println("KKthirst >> This plugin requires plugin KKcore");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        saveDefaultConfig();

        messageUtil = new MessageUtil("&2KKthirst&r");
        messageUtil.sendInfoToConsole("Enabling plugin KKthirst created by KPG-TB");

        Plugin core = Bukkit.getPluginManager().getPlugin("KKcore");

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
                getTextResource("default.txt"),
                this,
                core.getConfig()
        );

        ListenerManager listenerManager = new ListenerManager(
                messageUtil,
                languageManager,
                dataManager,
                getFile(),
                this,
                getConfig()
        );
        listenerManager.registerListeners("io.github.kpgtb.kkthirst.listener");

        UserManager userManager = new UserManager(this);
    }

    @Override
    public void onDisable() {
        messageUtil.sendInfoToConsole("Disabling plugin KKthirst created by KPG-TB");

        for(User user : users.values()) {
            user.save();
        }
        dataManager.closeConnection();
    }
}
