/*
 * Copyright 2022 KPG-TB
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkthirst.manager.UserManager;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import io.github.kpgtb.kkthirst.object.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GameModeChangeListener implements Listener {
    private final UserManager userManager;
    private final JavaPlugin plugin;

    public GameModeChangeListener(UsefulObjects usefulObjects){
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating GameModeChangeListener!");
            Bukkit.shutdown();
        }
        this.userManager = thirstUsefulObjects.getUserManager();
        this.plugin = thirstUsefulObjects.getPlugin();
    }

    @EventHandler
    public void onChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        User user = userManager.getUser(player.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                if(user == null) return;
                user.setupUI();
            }
        }.runTaskLaterAsynchronously(plugin, 10);
    }
}
