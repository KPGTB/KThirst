package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.KListener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.kpgtb.kthirst.manager.machine.BaseMachine;
import pl.kpgtb.kthirst.util.ThirstWrapper;

public class PlaceListener extends KListener {
    private final ThirstWrapper wrapper;

    public PlaceListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) {return;}

        BaseMachine machine = wrapper.getMachineManager().getMachineFromItemStack(event.getItemInHand());

        if(machine != null) {
            wrapper.getMachineManager().placeMachine(machine.getType(),event.getBlock().getLocation());
        }
    }
}
