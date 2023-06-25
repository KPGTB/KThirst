package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.KListener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.kpgtb.kthirst.manager.machine.BaseMachine;
import pl.kpgtb.kthirst.manager.machine.PlacedMachine;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.sql.SQLException;

public class BreakListener extends KListener {
    private final ThirstWrapper wrapper;

    public BreakListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) throws SQLException {
        Location location = event.getBlock().getLocation();
        PlacedMachine placedMachine = wrapper.getMachineManager().getPlacedMachine(location);

        if(placedMachine != null) {
            BaseMachine machine = wrapper.getMachineManager().getMachine(placedMachine.getType());
            if(machine != null) {
                event.setDropItems(false);
                location.getWorld().dropItemNaturally(location, wrapper.getItemManager().getCustomItem("kthirst", machine.getType()));
            }
            wrapper.getMachineManager().destroyMachine(location);
        }
    }
}
