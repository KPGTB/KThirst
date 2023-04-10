package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.Klistener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityAirChangeEvent;
import pl.kpgtb.kthirst.manager.user.ThirstUser;
import pl.kpgtb.kthirst.util.ThirstWrapper;

public class AirChangeListener extends Klistener {
    private final ThirstWrapper wrapper;

    public AirChangeListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
    }

    @EventHandler
    public void onChange(EntityAirChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ThirstUser user = wrapper.getUserManager().getUser(player.getUniqueId());
            if(user == null) {
                return;
            }

            boolean inWater = event.getAmount() != player.getMaximumAir();
            user.setInWater(inWater);
            user.setupUI();
        }
    }
}
