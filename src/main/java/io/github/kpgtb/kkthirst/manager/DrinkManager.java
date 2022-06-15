package io.github.kpgtb.kkthirst.manager;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.object.Drink;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class DrinkManager {
    private final HashMap<String, Drink> drinks;
    private final ArrayList<String> customDrinksNames;

    private final MessageUtil messageUtil;
    private final DataManager dataManager;
    private final FileConfiguration config;

    public DrinkManager(MessageUtil messageUtil, DataManager dataManager, FileConfiguration config) {
        this.messageUtil = messageUtil;
        this.dataManager = dataManager;
        this.config = config;
        drinks = new HashMap<>();
        customDrinksNames = new ArrayList<>();
    }

    public void registerDefaultDrinks() {
        if(!config.getBoolean("registerDefaultDrinks")) {
            return;
        }

        drinks.put("dirtyWater", getDefaultDrink(config,"dirtyWater"));
        drinks.put("cleanWater", getDefaultDrink(config,"cleanWater"));
    }
    public void registerDrinksFromDB() {
        for(Object drinkCodeName : dataManager.getKeys("drinks")) {
            registerDrink((String) drinkCodeName);
        }
    }

    public void registerDrink(String drinkCodeName) {
        if(!dataManager.getKeys("drinks").contains(drinkCodeName)) {
            messageUtil.sendErrorToConsole("Error while registering drink "+drinkCodeName + ". Drink doesn't exists!");
            return;
        }

        double thirstPoints = (double) dataManager.get("drinks", drinkCodeName, "thirstPoints");
        String drinkName = messageUtil.color((String) dataManager.get("drinks", drinkCodeName, "drinkName")) ;
        String drinkLoreRaw = (String) dataManager.get("drinks", drinkCodeName, "drinkLore");
        String drinkColorRaw  = (String) dataManager.get("drinks", drinkCodeName, "drinkColor");
        int drinkCustomDataModel = (int) dataManager.get("drinks", drinkCodeName, "drinkCMD");
        String drinkEffectsRaw = (String) dataManager.get("drinks", drinkCodeName, "drinkEffects");

        ArrayList<String> drinkLore = new ArrayList<>();
        if(!drinkLoreRaw.isEmpty()) {
            String[] loreLines = drinkLoreRaw.split("\n");
            for(String line : loreLines) {
                drinkLore.add(messageUtil.color(line));
            }
        }

        Color drinkColor = Color.fromRGB(0,0,0);
        if(!drinkColorRaw.isEmpty()) {
            try {
                String[] colors = drinkColorRaw.split(", ");
                int r = Integer.parseInt(colors[0]);
                int g = Integer.parseInt(colors[1]);
                int b = Integer.parseInt(colors[2]);
                drinkColor = Color.fromRGB(r,g,b);
            } catch (Exception e) {
                drinkColor = Color.fromRGB(0,0,0);
                messageUtil.sendErrorToConsole("Invalid color " + drinkColorRaw + " in drink " + drinkCodeName);
            }
        }

        ArrayList<PotionEffect> drinkEffects = new ArrayList<>();
        if(!drinkEffectsRaw.isEmpty()) {
            String[] effectsLines = drinkEffectsRaw.split(", ");
            for(String effectLine : effectsLines) {
                try {
                    String[] effectRaw = effectLine.split(" ");
                    String effectName = effectRaw[0];
                    int effectDuration = Integer.parseInt(effectRaw[1]);
                    int effectStrength = Integer.parseInt(effectRaw[2]) - 1;

                    PotionEffect effect = new PotionEffect(PotionEffectType.getByName(effectName),effectDuration,effectStrength);
                    drinkEffects.add(effect);
                } catch(Exception e) {
                    messageUtil.sendErrorToConsole("Invalid effect " + effectLine + " in drink " + drinkCodeName);
                }
            }
        }

        Drink drink = new Drink(drinkCodeName, thirstPoints,drinkName,drinkLore,drinkColor,drinkCustomDataModel,drinkEffects);
        drinks.put(drinkCodeName, drink);
    }
    public void registerCustomDrink(Drink drink) {
        drinks.put(drink.getCodeName(), drink);
        customDrinksNames.add(drink.getCodeName());
    }

    private Drink getDefaultDrink(FileConfiguration config, String codeName) {

        String name = messageUtil.color(config.getString(codeName+".name"));
        ArrayList<String> lore = new ArrayList<>();

        for(String loreLine : config.getStringList(codeName + ".lore")) {
            lore.add(messageUtil.color(loreLine));
        }

        double thirstPoints = config.getDouble(codeName+".thirstPoints");
        int customModelData = config.getInt(codeName+".customModelData");
        int r =config.getInt(codeName+".color.r");
        int g =config.getInt(codeName+".color.g");
        int b =config.getInt(codeName+".color.b");

        Color color = Color.fromRGB(r,g,b);
        ArrayList<PotionEffect> potionEffects = new ArrayList<>();

        for(String effect : config.getStringList(codeName+".effects")) {
            try {
                String[] effectValues = effect.split(" ");

                String effectName = effectValues[0];
                int effectDuration = Integer.parseInt(effectValues[1]);
                int effectStrngth = Integer.parseInt(effectValues[2]) - 1;

                PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(effectName), effectDuration, effectStrngth);
                potionEffects.add(potionEffect);
            } catch (Exception e) {
                messageUtil.sendErrorToConsole("Invalid effect " + effect + " in drink " + codeName);
            }
        }


        return new Drink(codeName,thirstPoints,name,lore,color,customModelData,potionEffects);
    }


    public Set<String> getDrinksName() {
        return drinks.keySet();
    }
    public Drink getDrink(String codeName) {
        return drinks.get(codeName);
    }
    public Drink getDrinkFromItemStack(ItemStack itemStack) {
        for(Drink drink : drinks.values()) {
            if(drink.getFinalDrink().isSimilar(itemStack)) {
                return drink;
            }
        }
        return null;
    }
    public void unregisterDrink(String codeName) {
        drinks.remove(codeName);
        if(customDrinksNames.contains(codeName)) {
            customDrinksNames.remove(codeName);
        }
    }
    public void reloadDrink(String codeName) {
        if(customDrinksNames.contains(codeName)) {
            messageUtil.sendErrorToConsole("You can't reload custom drink!");
            return;
        }

        if(codeName.equalsIgnoreCase("dirtyWater") || codeName.equalsIgnoreCase("cleanWater")) {
            unregisterDrink("dirtyWater");
            unregisterDrink("cleanWater");
            registerDefaultDrinks();
            return;
        }

        unregisterDrink(codeName);
        registerDrink(codeName);
    }
}
