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

import com.github.kpgtb.ktools.manager.ui.bar.BarManager;
import com.github.kpgtb.ktools.manager.ui.bar.KBar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManager {

    private final JavaPlugin plugin;
    private final KBar thirstBar;
    private final BarManager barManager;
    private final List<UUID> damaging;

    public UserManager(JavaPlugin plugin, KBar thirstBar, BarManager barManager) {
        this.plugin = plugin;
        this.thirstBar = thirstBar;
        this.barManager = barManager;
        this.damaging = new ArrayList<>();
    }

    public void prepare() {
        final double thirstPerMinute = plugin.getConfig().getDouble("thirstPerMinute");
        final double hpPerSecond = plugin.getConfig().getDouble("hpPerSecond");
        final double minHP = plugin.getConfig().getDouble("minHP");
        final List<String> disabledWorlds = plugin.getConfig().getStringList("disabledWorlds");

        new BukkitRunnable() {
            @Override
            public void run() {
               Bukkit.getOnlinePlayers().forEach(p -> {
                   GameMode gm = p.getGameMode();
                   if(gm.equals(GameMode.CREATIVE) || gm.equals(GameMode.SPECTATOR)) {
                       return;
                   }
                   if(disabledWorlds.contains(p.getWorld().getName())) {
                       return;
                   }

                   double userThirst = barManager.getValue(thirstBar,p);
                   userThirst -= thirstPerMinute;
                   if(userThirst <= 0.0) {
                       userThirst = 0.0;
                        if(!damaging.contains(p.getUniqueId())) {
                            damaging.add(p.getUniqueId());
                        }
                   }
                   barManager.setValue(thirstBar,p,userThirst);
               });
            }
        }.runTaskTimer(plugin, 60 * 20, 60 * 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                new ArrayList<>(damaging).forEach(uuid -> {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                    if(!op.isOnline()) {
                        damaging.remove(uuid);
                        return;
                    }
                    Player p = op.getPlayer();
                    GameMode gm = p.getGameMode();
                    if(gm.equals(GameMode.CREATIVE) || gm.equals(GameMode.SPECTATOR)) {
                        damaging.remove(uuid);
                        return;
                    }
                    double userThirst = barManager.getValue(thirstBar,p);
                    if(userThirst > 0.0) {
                        damaging.remove(uuid);
                        return;
                    }
                    if(p.getHealth() - hpPerSecond < minHP) {
                        double hpToDeal = p.getHealth() - minHP;
                        if(hpToDeal > 0.0) {
                            p.damage(hpToDeal);
                        }
                    } else {
                        p.damage(hpPerSecond);
                    }
                });
            }
        }.runTaskTimer(plugin,20,20);
    }

}
