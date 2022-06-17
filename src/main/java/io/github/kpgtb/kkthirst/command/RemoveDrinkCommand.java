package io.github.kpgtb.kkthirst.command;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.manager.command.CommandInfo;
import io.github.kpgtb.kkcore.manager.command.KKcommand;
import io.github.kpgtb.kkthirst.manager.DrinkManager;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandInfo(name = "remove_drink", description = "With this command you can remove drink from KKthirst", permission = "kkthirst.removedrink", requiredArgs = true,argsCount = 1,usage = "/remove_drink <codeName>")
public class RemoveDrinkCommand extends KKcommand {
    private final DrinkManager drinkManager;
    private final DataManager dataManager;
    private final LanguageManager languageManager;

    public RemoveDrinkCommand(UsefulObjects usefulObjects) {
        super(usefulObjects);
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating RemoveDrinkCommand!");
            Bukkit.shutdown();
        }

        drinkManager = thirstUsefulObjects.getDrinkManager();
        dataManager = thirstUsefulObjects.getDataManager();
        languageManager = thirstUsefulObjects.getLanguageManager();
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        String codeName = args[0];
        if(!drinkManager.getDrinksName().contains(codeName)) {
            sender.sendMessage(
                    languageManager.getMessage("drinkNotFound")
            );
            return;
        }

        if(drinkManager.getCustomDrinksNames().contains(codeName) ||
                codeName.equalsIgnoreCase("cleanWater") ||
                codeName.equalsIgnoreCase("dirtyWater")) {
            sender.sendMessage(
                    languageManager.getMessage("cantDoThat")
            );
            return;
        }

        boolean removed = dataManager.delete("drinks", codeName);
        if(removed) {
            sender.sendMessage(
                    languageManager.getMessage("removedDrink")
            );
            drinkManager.unregisterDrink(codeName);
        } else {
            sender.sendMessage(
                    languageManager.getMessage("notRemovedDrink")
            );
        }


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
