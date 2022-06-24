package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashSet;
import java.util.Set;

public class InvListener implements Listener {
    public InvListener(UsefulObjects usefulObjects) {
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch (ClassCastException e) {
            System.out.println("KKthirst >> Error while creating InvListener!");
            Bukkit.shutdown();
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {

        if(event.getView().getTitle().contains("\uF901")) {
            Set<Integer> blockedSlots = new HashSet<>();
            int ingradientSlot = 12;
            int resultSlot = 14;

            for (int i = 0; i < 27; i++) {
                if(ingradientSlot != i && resultSlot != i ) {
                    blockedSlots.add(i);
                }
            }


            Set<Integer> dragSlots = event.getRawSlots();
            for(int slot : dragSlots) {
                if(blockedSlots.contains(slot)) {
                    event.setCancelled(true);
                }
                if(slot == resultSlot) {
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if(event.getView().getTitle().contains("\uF901")) {
            Set<Integer> blockedSlots = new HashSet<>();
            int ingradientSlot = 12;
            int resultSlot = 14;

            for (int i = 0; i < 27; i++) {
                if(ingradientSlot != i && resultSlot != i ) {
                    blockedSlots.add(i);
                }
            }

            int slot = event.getRawSlot();
            if(!event.isShiftClick()) {
                if(blockedSlots.contains(slot)) {
                    event.setCancelled(true);
                }
                if(slot == resultSlot) {
                   if(event.getCursor() != null && !event.getCursor().getType().equals(Material.AIR)) {
                       event.setCancelled(true);
                   }
                }
            }

            if(event.isShiftClick()) {
                // Shift logic
                event.setCancelled(true);
            }
        }
    }
}
