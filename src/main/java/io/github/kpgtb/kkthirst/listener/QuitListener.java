package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkthirst.ThirstUsefulObjects;
import io.github.kpgtb.kkthirst.User;
import io.github.kpgtb.kkthirst.manager.UserManager;
import io.github.kpgtb.kkui.KKui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QuitListener implements Listener {

    private final UserManager userManager;

    public QuitListener(UsefulObjects usefulObjects) {
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating QuitListener!");
            Bukkit.shutdown();
        }

        this.userManager = thirstUsefulObjects.getUserManager();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(userManager.hasUser(uuid)) {
            User user = userManager.getUser(uuid);
            user.save();
            KKui.getUiManager().removeUI(uuid, user.getBaseUI());
            userManager.removeUser(uuid);
        }
    }
}
