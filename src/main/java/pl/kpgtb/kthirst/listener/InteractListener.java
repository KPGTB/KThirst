package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.Klistener;
import com.github.kpgtb.ktools.util.item.ItemUtil;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.util.concurrent.atomic.AtomicBoolean;

public class InteractListener extends Klistener {
    private final ThirstWrapper wrapper;

    public InteractListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
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
