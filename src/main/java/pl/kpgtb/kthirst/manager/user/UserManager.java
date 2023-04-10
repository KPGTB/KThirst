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

package pl.kpgtb.kthirst.manager.user;

import com.github.kpgtb.ktools.manager.ui.UiManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    private final JavaPlugin plugin;
    private final HashMap<UUID, ThirstUser> users;

    public UserManager(JavaPlugin plugin, UiManager uiManager) {
        this.plugin = plugin;
        users = new HashMap<>();

        final double thirstPerMinute = 0.5;
        final double hpPerSecond = 1.0;

        BukkitTask thirst = new BukkitRunnable() {
            @Override
            public void run() {
                for(ThirstUser user : users.values()) {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(user.getUuid());
                    if(!op.isOnline()) {
                        try {
                            user.save();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        uiManager.removeUI(user.getUuid(), user.getBaseUI());
                        users.remove(user.getUuid());
                        continue;
                    }

                    GameMode gm = op.getPlayer().getGameMode();
                    if(gm.equals(GameMode.CREATIVE) || gm.equals(GameMode.SPECTATOR)) {
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
                for(ThirstUser user : users.values()) {
                    try {
                        user.save();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskTimer(plugin, 1200,1200);
    }

    private void damagePlayer(ThirstUser user, double hpPerSecond) {
        BukkitTask damageTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(user.getThirst() > 0) {
                    user.setDamaging(false);
                    cancel();
                }

                OfflinePlayer op = Bukkit.getOfflinePlayer(user.getUuid());

                if(!op.isOnline()) {
                    user.setDamaging(false);
                    cancel();
                }

                Player player = op.getPlayer();

                if(player == null) {
                    user.setDamaging(false);
                    cancel();
                }

                GameMode gm = player.getGameMode();
                if(gm.equals(GameMode.CREATIVE) || gm.equals(GameMode.SPECTATOR)) {
                    user.setDamaging(false);
                    cancel();
                }

                double playerHP = player.getHealth();

                if(playerHP - hpPerSecond <= 0) {
                    player.setHealth(0.0);
                    user.setThirst(user.getMaxThirst());
                    try {
                        user.save();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    user.setDamaging(false);
                    cancel();
                }

                player.damage(hpPerSecond);
            }
        }.runTaskTimer(plugin, 20,20);
    }

    public void addUser(UUID uuid, ThirstUser user) {
        users.put(uuid, user);
    }

    public void removeUser(UUID uuid) {
        users.remove(uuid);
    }

    public ThirstUser getUser(UUID uuid) {
        if(!users.containsKey(uuid)) {
            return null;
        }
        return users.get(uuid);
    }

    public Collection<ThirstUser> getUsers() {
        return users.values();
    }

    public boolean hasUser(UUID uuid) {
        return users.containsKey(uuid);
    }

}
