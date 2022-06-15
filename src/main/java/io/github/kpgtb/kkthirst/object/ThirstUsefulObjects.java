package io.github.kpgtb.kkthirst.object;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.manager.DrinkManager;
import io.github.kpgtb.kkthirst.manager.UserManager;
import org.bukkit.configuration.file.FileConfiguration;

public class ThirstUsefulObjects extends UsefulObjects {
    private final UserManager userManager;
    private final DrinkManager drinkManager;

    public ThirstUsefulObjects(MessageUtil messageUtil, LanguageManager languageManager, DataManager dataManager, FileConfiguration config, UserManager userManager, DrinkManager drinkManager) {
        super(messageUtil, languageManager, dataManager, config);
        this.userManager = userManager;
        this.drinkManager = drinkManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public DrinkManager getDrinkManager() {
        return drinkManager;
    }
}
