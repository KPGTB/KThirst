package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.KListener;
import com.github.kpgtb.ktools.util.item.ItemUtil;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.kpgtb.kthirst.manager.user.ThirstUser;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuitListener extends KListener {
    private final ThirstWrapper wrapper;

    public QuitListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws SQLException {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(wrapper.getUserManager().hasUser(uuid)) {
            ThirstUser user = wrapper.getUserManager().getUser(uuid);
            user.save();
            wrapper.getUserManager().removeUser(uuid);
        }
    }
}
