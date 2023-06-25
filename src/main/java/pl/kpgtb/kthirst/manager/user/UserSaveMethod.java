package pl.kpgtb.kthirst.manager.user;

import com.github.kpgtb.ktools.manager.ui.bar.KBar;
import com.github.kpgtb.ktools.manager.ui.bar.save.IBarSaveMethod;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.entity.Player;
import pl.kpgtb.kthirst.Kthirst;

import java.util.UUID;

public class UserSaveMethod implements IBarSaveMethod {
    private final UserManager userManager;

    public UserSaveMethod(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void set(ToolsObjectWrapper wrapper, KBar bar, Player player, double value) {
        UUID uuid = player.getUniqueId();

        if(!userManager.hasUser(uuid)) {
            return;
        }

        userManager.getUser(uuid).setThirst(value);
    }

    @Override
    public double get(ToolsObjectWrapper wrapper, KBar bar, Player player) {
        UUID uuid = player.getUniqueId();

        if(!userManager.hasUser(uuid)) {
            return bar.getDefaultValue();
        }

        return userManager.getUser(uuid).getThirst();
    }
}
