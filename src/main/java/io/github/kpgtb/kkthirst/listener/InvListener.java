package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkthirst.manager.MachineManager;
import io.github.kpgtb.kkthirst.object.BaseMachine;
import io.github.kpgtb.kkthirst.object.PlacedMachine;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class InvListener implements Listener {
    private final MachineManager machineManager;

    public InvListener(UsefulObjects usefulObjects) {
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch (ClassCastException e) {
            System.out.println("KKthirst >> Error while creating InvListener!");
            Bukkit.shutdown();
        }

        machineManager = thirstUsefulObjects.getMachineManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrag(InventoryDragEvent event) {

        for(String machineName : machineManager.getMachinesName()) {
            BaseMachine machine = machineManager.getMachine(machineName);
            if(machine==null) {continue;}
            Character invChar = machine.getCustomInventoryChar();

            if(event.getView().getTitle().contains(invChar.toString())) {
                Set<Integer> blockedSlots = new HashSet<>();

                for (int i = 0; i < 27; i++) {
                    boolean blocked = true;
                    for(int ingredientSlot : machine.getIngredientSlots()) {
                        if(i == ingredientSlot) {
                            blocked = false;
                        }
                    }
                    for(int resultSlot : machine.getResultSlots()) {
                        if(i == resultSlot) {
                            blocked = false;
                        }
                    }

                    if(blocked) {
                        blockedSlots.add(i);
                    }
                }

                Set<Integer> dragSlots = event.getRawSlots();
                for(int slot : dragSlots) {
                    if(blockedSlots.contains(slot)) {
                        event.setCancelled(true);
                    }

                    for(int resultSlot : machine.getResultSlots()) {
                        if(slot == resultSlot) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event) {
        InventoryView inventoryView = event.getView();

        for(String machineName : machineManager.getMachinesName()) {
            BaseMachine machine = machineManager.getMachine(machineName);
            if (machine == null) {
                continue;
            }
            Character invChar = machine.getCustomInventoryChar();

            if (inventoryView.getTitle().contains(invChar.toString())) {
                Set<Integer> blockedSlots = new HashSet<>();

                for (int i = 0; i < 27; i++) {
                    boolean blocked = true;
                    for (int ingredientSlot : machine.getIngredientSlots()) {
                        if (i == ingredientSlot) {
                            blocked = false;
                        }
                    }
                    for (int resultSlot : machine.getResultSlots()) {
                        if (i == resultSlot) {
                            blocked = false;
                        }
                    }

                    if (blocked) {
                        blockedSlots.add(i);
                    }
                }

                int slot = event.getRawSlot();
                if (!event.isShiftClick()) {
                    if (blockedSlots.contains(slot)) {
                        event.setCancelled(true);
                        return;
                    }
                    for (int resultSlot : machine.getResultSlots()) {
                        if (slot == resultSlot) {
                            if (event.getCursor() != null && !event.getCursor().getType().equals(Material.AIR)) {
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                }

                if (event.isShiftClick()) {
                    for (int ingredientSlot : machine.getIngredientSlots()) {
                        if (slot == ingredientSlot) {
                            event.setCancelled(false);
                            return;
                        }
                        for (int resultSlot : machine.getResultSlots()) {
                            if (slot == resultSlot) {
                                event.setCancelled(false);
                                return;
                            }
                        }
                    }

                    if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) {
                        event.setCancelled(true);
                        return;
                    }

                    int currentItemAmount = event.getCurrentItem().getAmount();
                    for (int ingredientSlot : machine.getIngredientSlots()) {
                        if(currentItemAmount <= 0) {break;}
                        if (inventoryView.getItem(ingredientSlot) == null || inventoryView.getItem(ingredientSlot).getType().equals(Material.AIR)) {
                            inventoryView.setItem(ingredientSlot, event.getCurrentItem());
                            event.getCurrentItem().setAmount(0);
                            break;
                        }

                        if(inventoryView.getItem(ingredientSlot).isSimilar(event.getCurrentItem())) {
                            int ingredientAmount =inventoryView.getItem(ingredientSlot).getAmount();
                            int howMuch = 64 - ingredientAmount;
                            if(currentItemAmount <= howMuch) {
                                howMuch = currentItemAmount;
                            }
                            inventoryView.getItem(ingredientSlot).setAmount(ingredientAmount + howMuch);
                            event.getCurrentItem().setAmount(currentItemAmount-howMuch);
                            currentItemAmount = currentItemAmount-howMuch;
                            continue;
                        }
                    }

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void onInvUpdate(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        for(Location machineLocation : machineManager.getMachinesLocation()) {
            PlacedMachine placedMachine = machineManager.getPlacedMachine(machineLocation);
            Inventory machineInv = placedMachine.getBaseInventory();
            if(machineInv.equals(inv)) {
                machineManager.saveItems(inv, placedMachine);
            }
        }
    }
}
