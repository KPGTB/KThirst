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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class UserManager {

    private final JavaPlugin plugin;
    private final KBar thirstBar;
    private final BarManager barManager;

    public UserManager(JavaPlugin plugin, KBar thirstBar, BarManager barManager) {
        this.plugin = plugin;
        this.thirstBar = thirstBar;
        this.barManager = barManager;
    }

    public void prepare() {
        final double thirstPerMinute = 0.5;
        final double hpPerSecond = 1.0;

        new BukkitRunnable() {
            @Override
            public void run() {
               Bukkit.getOnlinePlayers().forEach(p -> {
                   GameMode gm = p.getGameMode();
                   if(gm.equals(GameMode.CREATIVE) || gm.equals(GameMode.SPECTATOR)) {
                       return;
                   }

                   double userThirst = barManager.getValue(thirstBar,p);
                   userThirst -= thirstPerMinute;
                   if(userThirst < 0.0) {
                       userThirst = 0.0;
                       p.damage(hpPerSecond);
                   }
                   barManager.setValue(thirstBar,p,userThirst);
               });
            }
        }.runTaskTimer(plugin, 60 * 20, 60 * 20);
    }

}
