package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkthirst.manager.MachineManager;
import io.github.kpgtb.kkthirst.object.BaseMachine;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceListener implements Listener {
    private final MachineManager machineManager;

    public PlaceListener(UsefulObjects usefulObjects){
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating PlaceListener!");
            Bukkit.shutdown();
        }
        this.machineManager = thirstUsefulObjects.getMachineManager();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) {return;}

        BaseMachine machine = machineManager.getMachineFromItemStack(event.getItemInHand());

        if(machine != null) {
            machineManager.placeMachine(machine.getType(),event.getBlock().getLocation());
        }
    }
}
