package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.Klistener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.kpgtb.kthirst.manager.user.ThirstUser;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.sql.SQLException;
import java.util.UUID;

public class DeathListener extends Klistener {
    private final ThirstWrapper wrapper;

    public DeathListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) throws SQLException {
        Player player =event.getEntity();
        UUID uuid = player.getUniqueId();
        ThirstUser user = wrapper.getUserManager().getUser(uuid);
        if(user == null) {
            return;
        }
        user.setThirst(user.getMaxThirst());
        user.save();
        user.setDamaging(false);
    }
}
