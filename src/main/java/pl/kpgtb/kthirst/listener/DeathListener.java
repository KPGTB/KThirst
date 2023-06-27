package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.KListener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.kpgtb.kthirst.util.ThirstWrapper;

public class DeathListener extends KListener {
    private final ThirstWrapper wrapper;

    public DeathListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        wrapper.getBarManager().setValue(
                wrapper.getThirstBar(),
                player,
                wrapper.getThirstBar().getDefaultValue()
        );
    }
}
