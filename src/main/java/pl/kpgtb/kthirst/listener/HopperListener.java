package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.KListener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.HopperInventorySearchEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.kpgtb.kthirst.manager.machine.BaseMachine;
import pl.kpgtb.kthirst.manager.machine.PlacedMachine;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class HopperListener extends KListener {
    private final ThirstWrapper wrapper;
    private final Map<Location, Map.Entry<HopperInventorySearchEvent.ContainerType, Location>> requests;

    public HopperListener(ToolsObjectWrapper wrapper) {
        super(wrapper);
        this.wrapper = (ThirstWrapper) wrapper;
        this.requests = new HashMap<>();
    }

    @EventHandler
    public void onSearch(HopperInventorySearchEvent event) {
        Block block = event.getSearchBlock();

        if(!(block.getType() == Material.CAULDRON)) {
            return;
        }

        Location location = block.getLocation();
        PlacedMachine machine = wrapper.getMachineManager().getPlacedMachine(location);

        if(machine == null) {
            return;
        }

        HopperInventorySearchEvent.ContainerType type = event.getContainerType();
        Inventory inv = Bukkit.createInventory(null,9);

        if(type == HopperInventorySearchEvent.ContainerType.SOURCE) {
            inv.setItem(0, machine.getBaseInventory().getItem(14));
        }

        event.setInventory(inv);
        requests.put(event.getBlock().getLocation(), new AbstractMap.SimpleEntry<>(type, location));
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        BaseMachine baseMachine = wrapper.getMachineManager().getMachine("filter_machine");
        if (baseMachine == null) {
            return;
        }

        Inventory initiator = event.getInitiator();
        Location loc = initiator.getLocation();

        if (loc == null) {
            return;
        }

        Map.Entry<HopperInventorySearchEvent.ContainerType, Location> req = requests.get(loc);
        if (req == null) {
            return;
        }
        requests.remove(loc);

        Location location = req.getValue();
        PlacedMachine machine = wrapper.getMachineManager().getPlacedMachine(location);

        if (machine == null) {
            return;
        }

        if (req.getKey() == HopperInventorySearchEvent.ContainerType.SOURCE) {
            event.setItem(machine.getResults()[0]);
            machine.setResults(new ItemStack[1]);
        } else {
            if (machine.getIngredients()[0] != null && machine.getIngredients()[0].getType() != Material.AIR) {
                event.setCancelled(true);
                return;
            }
            ItemStack[] content = new ItemStack[1];
            content[0] = event.getItem();
            machine.setIngredients(content);
        }
    }
}
