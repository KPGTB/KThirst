/*
 * Copyright 2022 KPG-TB
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.manager.DrinkManager;
import io.github.kpgtb.kkthirst.manager.MachineManager;
import io.github.kpgtb.kkthirst.object.BaseMachine;
import io.github.kpgtb.kkthirst.object.PlacedMachine;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.lang.reflect.InvocationTargetException;

public class InteractEvent implements Listener {
    private final DrinkManager drinkManager;
    private final MachineManager machineManager;
    private final MessageUtil messageUtil;
    private final FileConfiguration config;

    public InteractEvent(UsefulObjects usefulObjects){
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating InteractEvent!");
            Bukkit.shutdown();
        }
        this.machineManager = thirstUsefulObjects.getMachineManager();
        this.drinkManager = thirstUsefulObjects.getDrinkManager();
        this.messageUtil = thirstUsefulObjects.getMessageUtil();
        this.config = thirstUsefulObjects.getConfig();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            for(Location location : machineManager.getMachinesLocation()) {
                Location clickedLocation = event.getClickedBlock().getLocation();
                if(location.equals(clickedLocation)) {
                    PlacedMachine machine = machineManager.getPlacedMachine(location);
                    BaseMachine baseMachine = machineManager.getMachine(machine.getType());
                    if(baseMachine == null) {return;}

                    if(!event.getClickedBlock().getType().equals(baseMachine.getMachineItemStack().getType())) {return;}

                    player.openInventory(machine.getBaseInventory());

                    StringBuilder invTitle = new StringBuilder(baseMachine.getInventoryTitle());

                    if(machine.getProgress() > 0) {
                        for (int i = 0; i < machine.getProgress(); i++) {
                            invTitle.append(baseMachine.getProgressBarChars());
                        }
                    }

                    try {
                        machineManager.getInventoryHelper().updateInventoryTitle(player, invTitle.toString());
                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                             IllegalAccessException | NoSuchFieldException | InstantiationException e) {

                        player.closeInventory();
                        messageUtil.sendErrorToConsole("Error while updating gui title! " + clickedLocation);
                        throw new RuntimeException(e);
                    }

                    event.setCancelled(true);
                    break;
                }
            }

            if(event.useInteractedBlock() != Event.Result.DENY) {
                if(event.getClickedBlock().getRelative(event.getBlockFace()).getType().equals(Material.WATER)) {
                    if(event.getItem() != null && event.getItem().getType().equals(Material.GLASS_BOTTLE)) {
                        if(config.getBoolean("getDirtyWaterFromWaterSource")) {
                            event.setCancelled(true);
                            event.getItem().setAmount(event.getItem().getAmount() - 1);
                            player.getInventory().addItem(drinkManager.getDrink("dirtyWater").getFinalDrink());
                        }
                    }
                }
            }
        }
    }
}
