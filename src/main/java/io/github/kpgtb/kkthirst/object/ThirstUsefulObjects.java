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

package io.github.kpgtb.kkthirst.object;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.manager.DrinkManager;
import io.github.kpgtb.kkthirst.manager.MachineManager;
import io.github.kpgtb.kkthirst.manager.UserManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ThirstUsefulObjects extends UsefulObjects {
    private final UserManager userManager;
    private final DrinkManager drinkManager;
    private final MachineManager machineManager;
    private final JavaPlugin plugin;

    public ThirstUsefulObjects(MessageUtil messageUtil, LanguageManager languageManager, DataManager dataManager, FileConfiguration config, UserManager userManager, DrinkManager drinkManager, MachineManager machineManager, JavaPlugin plugin) {
        super(messageUtil, languageManager, dataManager, config);
        this.userManager = userManager;
        this.drinkManager = drinkManager;
        this.machineManager = machineManager;
        this.plugin = plugin;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public DrinkManager getDrinkManager() {
        return drinkManager;
    }

    public MachineManager getMachineManager() {
        return machineManager;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
