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

package io.github.kpgtb.kkthirst.manager;

import io.github.kpgtb.kkthirst.KKthirst;
import io.github.kpgtb.kkthirst.object.User;
import io.github.kpgtb.kkui.KKui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class UserManager {

    private final KKthirst plugin;
    private final HashMap<UUID, User> users;

    public UserManager(KKthirst plugin) {
        this.plugin = plugin;
        users = new HashMap<>();

        final double thirstPerMinute = plugin.getConfig().getDouble("minusThirstPerMinute");
        final double hpPerSecond = plugin.getConfig().getDouble("hpPerSecond");

        BukkitTask thirst = new BukkitRunnable() {
            @Override
            public void run() {
                for(User user : users.values()) {
                    if(!Bukkit.getOfflinePlayer(user.getUuid()).isOnline()) {
                        user.save();
                        KKui.getUiManager().removeUI(user.getUuid(), user.getBaseUI());
                        users.remove(user.getUuid());
                        continue;
                    }

                    double userThirst = user.getThirst();
                    if(userThirst - thirstPerMinute < 0 && !user.isDamaging()) {
                        user.setThirst(0.0);
                        damagePlayer(user, hpPerSecond);
                        user.setDamaging(true);
                        continue;
                    }
                    user.setThirst(userThirst - thirstPerMinute);
                }
            }
        }.runTaskTimer(plugin, 60 * 20, 60 * 20);

        BukkitTask autoSaver = new BukkitRunnable() {
            @Override
            public void run() {
                for(User user : users.values()) {
                    user.save();
                }
            }
        }.runTaskTimer(plugin, plugin.getConfig().getInt("saveTimeTicks"), plugin.getConfig().getInt("saveTimeTicks"));
    }

    private void damagePlayer(User user, double hpPerSecond) {
        BukkitTask damageTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(user.getThirst() > 0) {
                    user.setDamaging(false);
                    cancel();
                }

                if(!Bukkit.getOfflinePlayer(user.getUuid()).isOnline()) {
                    user.setDamaging(false);
                    cancel();
                }

                Player player = Bukkit.getPlayer(user.getUuid());

                if(player == null) {
                    user.setDamaging(false);
                    cancel();
                }

                double playerHP = player.getHealth();

                if(playerHP - hpPerSecond <= 0) {
                    player.setHealth(0.0);
                    user.setThirst(user.getMaxThirst());
                    user.save();
                    user.setDamaging(false);
                    cancel();
                }

                player.damage(hpPerSecond);
            }
        }.runTaskTimer(plugin, 20,20);
    }

    public void addUser(UUID uuid, User user) {
        users.put(uuid, user);
    }

    public void removeUser(UUID uuid) {
        users.remove(uuid);
    }

    public User getUser(UUID uuid) {
        if(!users.containsKey(uuid)) {
            return null;
        }
        return users.get(uuid);
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public boolean hasUser(UUID uuid) {
        return users.containsKey(uuid);
    }

}
