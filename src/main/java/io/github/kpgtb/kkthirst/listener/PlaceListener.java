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
