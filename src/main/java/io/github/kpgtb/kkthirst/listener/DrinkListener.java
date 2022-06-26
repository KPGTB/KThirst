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
import io.github.kpgtb.kkthirst.manager.DrinkManager;
import io.github.kpgtb.kkthirst.manager.UserManager;
import io.github.kpgtb.kkthirst.object.Drink;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import io.github.kpgtb.kkthirst.object.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

public class DrinkListener implements Listener {
    private final UserManager userManager;
    private final DrinkManager drinkManager;

    public DrinkListener(UsefulObjects usefulObjects){
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating DrinkListener!");
            Bukkit.shutdown();
        }
        this.userManager = thirstUsefulObjects.getUserManager();
        this.drinkManager = thirstUsefulObjects.getDrinkManager();
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        if(event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        User user = userManager.getUser(uuid);
        if(user == null) {
            return;
        }

        ItemStack item = event.getItem();
        if(!item.getType().equals(Material.POTION)) {
            return;
        }

        Drink drink = drinkManager.getDrinkFromItemStack(item);
        if(drink == null) {
            return;
        }

        user.setThirst(user.getThirst() + drink.getThirstPoints());

        for(PotionEffect effect : drink.getDrinkEffects()) {
            player.addPotionEffect(effect);
        }
    }
}
