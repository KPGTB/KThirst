package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.KListener;
import com.github.kpgtb.ktools.util.item.ItemUtil;
import com.github.kpgtb.ktools.util.ui.FontWidth;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.kpgtb.kthirst.manager.machine.BaseMachine;
import pl.kpgtb.kthirst.manager.machine.PlacedMachine;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

public class InteractListener extends KListener {
    private final ThirstWrapper wrapper;

    public InteractListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            for(Location location : wrapper.getMachineManager().getMachinesLocation()) {
                Location clickedLocation = event.getClickedBlock().getLocation();
                if(location.equals(clickedLocation)) {
                    PlacedMachine machine = wrapper.getMachineManager().getPlacedMachine(location);
                    BaseMachine baseMachine = wrapper.getMachineManager().getMachine(machine.getType());
                    if(baseMachine == null) {return;}

                    if(!event.getClickedBlock().getType().equals(baseMachine.getMaterial())) {return;}

                    player.openInventory(machine.getBaseInventory());

                    StringBuilder invTitle = new StringBuilder(baseMachine.getInventoryTitle());

                    if(machine.getProgress() > 0) {
                        for (int i = 0; i < machine.getProgress(); i++) {
                            invTitle.insert(0, baseMachine.getProgressBarChars());
                        }
                        invTitle.insert(machine.getProgress() * baseMachine.getProgressBarChars().length(), FontWidth.getSpaces(-baseMachine.getProgressBarOffset() - (machine.getProgress() * baseMachine.getProgressBarCharSize())));
                        invTitle.insert(0, FontWidth.getSpaces(baseMachine.getProgressBarOffset()));
                        invTitle.insert(0, ChatColor.WHITE);
                    }

                    try {
                        wrapper.getMachineManager().getInventoryHelper().updateInventoryTitle(player, invTitle.toString());
                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                             IllegalAccessException | NoSuchFieldException | InstantiationException e) {

                        player.closeInventory();
                        e.printStackTrace();
                    }

                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if(event.useItemInHand() != Event.Result.DENY) {
            if(action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                AtomicBoolean isWater = new AtomicBoolean(false);

                player.getLineOfSight(null, 9).forEach(block -> {
                    if(block.getType().equals(Material.WATER)) {
                        isWater.set(true);
                    }
                });

                if(isWater.get()) {
                    if (event.getItem() != null && event.getItem().getType().equals(Material.GLASS_BOTTLE)) {
                        event.setCancelled(true);
                        event.getItem().setAmount(event.getItem().getAmount() - 1);
                        ItemUtil.giveItemToPlayer(player, wrapper.getItemManager().getCustomItem("kthirst", "dirty_water"));
                    }
                }
            }
        }
    }
}
