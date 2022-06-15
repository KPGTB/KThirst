package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkthirst.manager.UserManager;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import io.github.kpgtb.kkthirst.object.User;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class DeathListener implements Listener {

    private final UserManager userManager;

    public DeathListener(UsefulObjects usefulObjects){
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating DeathListener!");
            Bukkit.shutdown();
        }
        this.userManager = thirstUsefulObjects.getUserManager();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player =event.getEntity();
        UUID uuid = player.getUniqueId();
        User user = userManager.getUser(uuid);
        if(user == null) {
            return;
        }
        user.setThirst(user.getMaxThirst());
        user.save();
        user.setDamaging(false);
    }
}
