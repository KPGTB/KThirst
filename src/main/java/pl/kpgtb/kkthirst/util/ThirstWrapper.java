package pl.kpgtb.kkthirst.util;

import com.github.kpgtb.ktools.manager.language.LanguageManager;
import com.github.kpgtb.ktools.util.GlobalManagersWrapper;
import com.github.kpgtb.ktools.util.ToolsObjectWrapper;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public class ThirstWrapper extends ToolsObjectWrapper {
    public ThirstWrapper(GlobalManagersWrapper globalManagersWrapper, LanguageManager languageManager, JavaPlugin plugin, BukkitAudiences adventure) {
        super(globalManagersWrapper, languageManager, plugin, adventure);
    }
}
