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

@CommandInfo(name = "add_drink", description = "Add new drink from KKthirst", permission = "kkthirst.adddrink", requiredArgs = true, argsCount = 1, usage = "/add_drink <codeName>")
public class AddDrinkCommand extends KKcommand {
    private final DrinkManager drinkManager;
    private final DataManager dataManager;
    private final LanguageManager languageManager;

    public AddDrinkCommand(UsefulObjects usefulObjects) {
        super(usefulObjects);
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating GetDrinkCommand!");
            Bukkit.shutdown();
        }

        drinkManager = thirstUsefulObjects.getDrinkManager();
        dataManager = thirstUsefulObjects.getDataManager();
        languageManager = thirstUsefulObjects.getLanguageManager();
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        String codeName = args[0];
        if(drinkManager.getDrinksName().contains(codeName)) {
            sender.sendMessage(
                    languageManager.getMessage("drinkExists")
            );
        }

        boolean created = dataManager.set("drinks", codeName, "id", codeName);
        if(created) {
            sender.sendMessage(
                    languageManager.getMessage("createdDrink")
            );
        } else {
            sender.sendMessage(
                    languageManager.getMessage("notCreatedDrink")
            );
        }
    }
}
