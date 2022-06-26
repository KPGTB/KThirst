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

package io.github.kpgtb.kkthirst.command;

import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.manager.command.CommandInfo;
import io.github.kpgtb.kkcore.manager.command.KKcommand;
import io.github.kpgtb.kkthirst.manager.DrinkManager;
import io.github.kpgtb.kkthirst.object.Drink;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@CommandInfo(name="get_drink", description = "With this command you get drink from KKthirst", permission = "kkthirst.getdrink", requiredPlayer = true, requiredArgs = true, argsCount = 1,usage = "/get_drink <drink_name>")
public class GetDrinkCommand extends KKcommand {

    private final DrinkManager drinkManager;
    private final LanguageManager languageManager;

    public GetDrinkCommand(UsefulObjects usefulObjects) {
        super(usefulObjects);
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating GetDrinkCommand!");
            Bukkit.shutdown();
        }

        drinkManager = thirstUsefulObjects.getDrinkManager();
        languageManager = thirstUsefulObjects.getLanguageManager();
    }

    @Override
    public void executeCommand(Player player, String[] args) {
        Drink drink = drinkManager.getDrink(args[0]);

        if(drink==null) {
            player.sendMessage(
                    languageManager.getMessage(
                            "drinkNotFound",
                            player,
                            new HashMap<>()
                    )
            );
            return;
        }

        player.getInventory().addItem(drink.getFinalDrink());

        player.sendMessage(
                languageManager.getMessage(
                        "getDrinkFromCommand",
                        player,
                        new HashMap<>()
                )
        );
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if(args.length == 0) {
            return Arrays.asList((String[]) drinkManager.getDrinksName().toArray());
        }

        if(args.length == 1) {
            ArrayList<String> results = new ArrayList<>();
            for(String drinkName : drinkManager.getDrinksName()) {
                if(drinkName.startsWith(args[0])) {
                    results.add(drinkName);
                }
            }
            return results;
        }
        return new ArrayList<>();
    }
}
