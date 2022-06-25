package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkthirst.manager.MachineManager;
import io.github.kpgtb.kkthirst.object.BaseMachine;
import io.github.kpgtb.kkthirst.object.PlacedMachine;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakListener implements Listener {

    private final MachineManager machineManager;

    public BreakListener(UsefulObjects usefulObjects){
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating BreakListener!");
            Bukkit.shutdown();
        }
        this.machineManager = thirstUsefulObjects.getMachineManager();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        PlacedMachine placedMachine = machineManager.getPlacedMachine(location);

        if(placedMachine != null) {
            BaseMachine machine = machineManager.getMachine(placedMachine.getType());
            if(machine != null) {
                event.setDropItems(false);
                location.getWorld().dropItemNaturally(location, machine.getMachineItemStack());
            }
            machineManager.destroyMachine(location);
        }


    }
}
