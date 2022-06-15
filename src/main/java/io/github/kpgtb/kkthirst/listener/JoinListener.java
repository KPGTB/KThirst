package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import io.github.kpgtb.kkthirst.object.User;
import io.github.kpgtb.kkthirst.manager.UserManager;
import io.github.kpgtb.kkui.KKui;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {
    private final DataManager dataManager;
    private final FileConfiguration config;
    private final UserManager userManager;

    public JoinListener(UsefulObjects usefulObjects){
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating JoinListener!");
            Bukkit.shutdown();
        }

        this.dataManager = thirstUsefulObjects.getDataManager();
        this.config = thirstUsefulObjects.getConfig();
        this.userManager = thirstUsefulObjects.getUserManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(userManager.hasUser(uuid)) {
            KKui.getUiManager().removeUI(uuid, userManager.getUser(uuid).getBaseUI());
            userManager.removeUser(uuid);
        }

        double maxThirst = config.getDouble("maxThirst");
        // If user do not exists in database
        if(!dataManager.getKeys("users").contains(uuid.toString())) {
            dataManager.set("users", uuid.toString(), "thirst", maxThirst);
        }

        double playerThirst = (double) dataManager.get("users", uuid.toString(), "thirst");
        User user = new User(uuid, playerThirst, maxThirst,dataManager, config.getInt("uiOffset"));

        userManager.addUser(uuid, user);

        KKui.getUiManager().addUI(uuid, user.getBaseUI());
    }
}
