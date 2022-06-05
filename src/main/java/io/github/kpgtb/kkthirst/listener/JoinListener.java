package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.KKthirst;
import io.github.kpgtb.kkthirst.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {
    private final MessageUtil messageUtil;
    private final LanguageManager languageManager;
    private final DataManager dataManager;
    private final FileConfiguration config;

    public JoinListener(MessageUtil messageUtil, LanguageManager languageManager, DataManager dataManager, FileConfiguration config) {
        this.messageUtil = messageUtil;
        this.languageManager = languageManager;
        this.dataManager = dataManager;
        this.config = config;
    }

    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(KKthirst.users.containsKey(uuid)) {
            KKthirst.users.get(uuid).save();
            KKthirst.users.remove(uuid);
        }

        // If user do not exists in database
        if(!dataManager.getKeys("users").contains(uuid.toString())) {
            double defaultThirst = config.getDouble("maxThirst");
            dataManager.set("users", uuid.toString(), "thirst", defaultThirst);
        }

        double playerThirst = (double) dataManager.get("users", uuid.toString(), "thirst");
        User user = new User(uuid, playerThirst, dataManager);

        KKthirst.users.put(uuid, user);
    }
}
