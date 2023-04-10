package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.Klistener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.kpgtb.kthirst.manager.user.ThirstUser;
import pl.kpgtb.kthirst.util.ThirstWrapper;

public class GameModeListener extends Klistener {
    private final ThirstWrapper wrapper;

    public GameModeListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
    }

    @EventHandler
    public void onChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        ThirstUser user = wrapper.getUserManager().getUser(player.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                if(user == null) return;
                user.setupUI();
            }
        }.runTaskLaterAsynchronously(wrapper.getPlugin(), 10);
    }
}
