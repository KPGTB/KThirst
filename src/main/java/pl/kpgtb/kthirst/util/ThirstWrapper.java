package pl.kpgtb.kthirst.util;

import com.github.kpgtb.ktools.manager.language.LanguageManager;
import com.github.kpgtb.ktools.util.wrapper.GlobalManagersWrapper;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import pl.kpgtb.kthirst.manager.drink.DrinkManager;
import pl.kpgtb.kthirst.manager.user.UserManager;

public class ThirstWrapper extends ToolsObjectWrapper {
    private final DrinkManager drinkManager;
    private final UserManager userManager;

    public ThirstWrapper(GlobalManagersWrapper globalManagersWrapper, LanguageManager languageManager, JavaPlugin plugin, BukkitAudiences adventure, DrinkManager drinkManager, UserManager userManager) {
        super(globalManagersWrapper, languageManager, plugin, adventure);
        this.drinkManager = drinkManager;
        this.userManager = userManager;
    }

    public DrinkManager getDrinkManager() {
        return drinkManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}
