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
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import io.github.kpgtb.kkthirst.object.User;
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
