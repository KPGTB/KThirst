package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.KListener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.kpgtb.kthirst.util.ThirstWrapper;

public class JoinListener extends KListener {
    private final ThirstWrapper wrapper;
    public JoinListener(ToolsObjectWrapper wrapper) {
        super(wrapper);
        this.wrapper = (ThirstWrapper) wrapper;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(wrapper.getConfig().getBoolean("disableBedrockUI")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = event.getPlayer();
                    String prefix = wrapper.getConfig().getString("floodgatePrefix");
                    if(player.getName().startsWith(prefix) || player.getDisplayName().startsWith(prefix)) {
                        wrapper.getBarManager().hideBar(wrapper.getThirstBar(), player);
                    }
                }
            }.runTaskLater(wrapper.getPlugin(), 20L);
        }
    }
}
