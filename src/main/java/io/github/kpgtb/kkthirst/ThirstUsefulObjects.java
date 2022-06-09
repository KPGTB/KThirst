package io.github.kpgtb.kkthirst;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.manager.UserManager;
import org.bukkit.configuration.file.FileConfiguration;

public class ThirstUsefulObjects extends UsefulObjects {
    private final UserManager userManager;

    public ThirstUsefulObjects(MessageUtil messageUtil, LanguageManager languageManager, DataManager dataManager, FileConfiguration config, UserManager userManager) {
        super(messageUtil, languageManager, dataManager, config);
        this.userManager = userManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}
