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

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.manager.command.CommandInfo;
import io.github.kpgtb.kkcore.manager.command.KKcommand;
import io.github.kpgtb.kkcore.util.MessageUtil;
import io.github.kpgtb.kkthirst.manager.DrinkManager;
import io.github.kpgtb.kkthirst.object.Drink;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@CommandInfo(name = "edit_drink",description = "With this command you can edit drink from KKthirst", permission = "kkthirst.editdrink", requiredArgs = true, argsCount = 1, usage = "/edit_drink <codeName>")
public class EditDrinkCommand extends KKcommand {
    private final DrinkManager drinkManager;
    private final DataManager dataManager;
    private final LanguageManager languageManager;
    private final MessageUtil messageUtil;

    public EditDrinkCommand(UsefulObjects usefulObjects) {
        super(usefulObjects);
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating EditDrinkCommand!");
            Bukkit.shutdown();
        }

        drinkManager = thirstUsefulObjects.getDrinkManager();
        dataManager = thirstUsefulObjects.getDataManager();
        languageManager = thirstUsefulObjects.getLanguageManager();
        messageUtil = thirstUsefulObjects.getMessageUtil();
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        String codeName = args[0];
        Drink drink = drinkManager.getDrink(codeName);

        if(drink == null) {
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
        }


        if(args.length == 1) {
            HashMap<String,String> replaces = new HashMap<>();

            replaces.put("CODE_NAME", messageUtil.color(codeName));
            replaces.put("DRINK_NAME", messageUtil.color(drink.getDrinkName()));

            StringBuilder lore = new StringBuilder();
            for(String line : drink.getDrinkLore()) {
                lore.append("\n&7- &r").append(line);
            }

            replaces.put("DRINK_LORE", messageUtil.color(lore.toString()));
            replaces.put("DRINK_POINTS", messageUtil.color(String.valueOf(drink.getThirstPoints())));
            replaces.put("DRINK_COLOR_R", messageUtil.color(String.valueOf(drink.getDrinkColor().getRed())));
            replaces.put("DRINK_COLOR_G", messageUtil.color(String.valueOf(drink.getDrinkColor().getGreen())));
            replaces.put("DRINK_COLOR_B", messageUtil.color(String.valueOf(drink.getDrinkColor().getBlue())));
            replaces.put("DRINK_CMD", messageUtil.color(String.valueOf(drink.getDrinkCustomModelData())));

            StringBuilder effects = new StringBuilder();
            for(PotionEffect effect : drink.getDrinkEffects()) {
                effects.append("\n&7- &r").append(effect.getType().getName()).append(" ").append(effect.getDuration()).append(" ticks ").append(effect.getAmplifier() + 1);
            }
            replaces.put("DRINK_EFFECTS", messageUtil.color(effects.toString()));

            sender.sendMessage(
                    languageManager.getMessage(
                            "editDrinkInfo",
                            replaces
                    )
            );
            return;
        }

        if(args.length > 1) {
            switch (args[1]) {
                case "name":
                    if(args.length <= 2) {
                        HashMap<String, String> usageReplaces = new HashMap<>();
                        usageReplaces.put("USAGE", "/edit_drink "+codeName+" name <new_name>");
                        sender.sendMessage(
                                languageManager.getMessage("wrongUsage", usageReplaces)
                        );
                        return;
                    }
                    StringBuilder drinkName = new StringBuilder(args[2]);
                    for(int i = 3; i<args.length; i++) {
                        drinkName.append(" ").append(args[i]);
                    }
                    boolean edited = dataManager.set("drinks", codeName, "drinkName", drinkName.toString());
                    if(edited) {
                        sender.sendMessage(
                                languageManager.getMessage("editedDrink")
                        );
                        drinkManager.reloadDrink(codeName);
                    } else {
                        sender.sendMessage(
                                languageManager.getMessage("notEditedDrink")
                        );
                    }
                    break;
                case "lore":
                    if(args.length <= 3) {
                        HashMap<String, String> usageReplaces = new HashMap<>();
                        usageReplaces.put("USAGE", "/edit_drink "+codeName+" lore <add/remove> <line/lineNumber>");
                        sender.sendMessage(
                                languageManager.getMessage("wrongUsage", usageReplaces)
                        );
                        return;
                    }

                    ArrayList<String> lore = drink.getDrinkLore();

                    switch (args[2]) {
                        case "add":

                            StringBuilder loreLine = new StringBuilder(args[3]);
                            for(int i = 4; i<args.length; i++) {
                                loreLine.append(" ").append(args[i]);
                            }
                            lore.add(loreLine.toString());

                            StringBuilder finalLore = new StringBuilder();
                            for(String line : lore) {
                                finalLore.append(line).append("\n");
                            }

                            boolean editedLore = dataManager.set("drinks", codeName, "drinkLore", finalLore.toString());
                            if(editedLore) {
                                sender.sendMessage(
                                        languageManager.getMessage("editedDrink")
                                );
                                drinkManager.reloadDrink(codeName);
                            } else {
                                sender.sendMessage(
                                        languageManager.getMessage("notEditedDrink")
                                );
                            }
                            break;
                        case "remove":
                            try {
                                int lineNumber = Integer.parseInt(args[3]);
                                lore.remove(lineNumber - 1);
                            } catch (Exception e) {
                                HashMap<String, String> usageReplaces = new HashMap<>();
                                usageReplaces.put("USAGE", "/edit_drink "+codeName+" lore remove <lineNumber>");
                                sender.sendMessage(
                                        languageManager.getMessage("wrongUsage", usageReplaces)
                                );
                                return;
                            }

                            StringBuilder finalLore2 = new StringBuilder();
                            for(String line : lore) {
                                finalLore2.append(line).append("\n");
                            }

                            boolean editedLore2 = dataManager.set("drinks", codeName, "drinkLore", finalLore2.toString());
                            if(editedLore2) {
                                sender.sendMessage(
                                        languageManager.getMessage("editedDrink")
                                );
                                drinkManager.reloadDrink(codeName);
                            } else {
                                sender.sendMessage(
                                        languageManager.getMessage("notEditedDrink")
                                );
                            }

                            break;
                    }

                    break;
                case "points":
                    if(args.length <= 2) {
                        HashMap<String, String> usageReplaces = new HashMap<>();
                        usageReplaces.put("USAGE", "/edit_drink "+codeName+" points <new_points>");
                        sender.sendMessage(
                                languageManager.getMessage("wrongUsage", usageReplaces)
                        );
                        return;
                    }

                    double points;

                    try {
                        points = Double.parseDouble(args[2]);
                    } catch (Exception e) {
                        HashMap<String, String> usageReplaces = new HashMap<>();
                        usageReplaces.put("USAGE", "/edit_drink "+codeName+" points <new_points>");
                        sender.sendMessage(
                                languageManager.getMessage("wrongUsage", usageReplaces)
                        );
                        return;
                    }

                    boolean pointsEdited = dataManager.set("drinks", codeName, "thirstPoints", points);
                    if(pointsEdited) {
                        sender.sendMessage(
                                languageManager.getMessage("editedDrink")
                        );
                        drinkManager.reloadDrink(codeName);
                    } else {
                        sender.sendMessage(
                                languageManager.getMessage("notEditedDrink")
                        );
                    }
                    break;
                case "color":
                    if(args.length < 5) {
                        HashMap<String, String> usageReplaces = new HashMap<>();
                        usageReplaces.put("USAGE", "/edit_drink "+codeName+" color <r 0-255> <g 0-255> <b 0-255>");
                        sender.sendMessage(
                                languageManager.getMessage("wrongUsage", usageReplaces)
                        );
                        return;
                    }

                    int r,g,b;

                    try {
                        r = Integer.parseInt(args[2]);
                        g = Integer.parseInt(args[3]);
                        b = Integer.parseInt(args[4]);

                        if(r < 0 || r > 255) {
                            throw new IllegalArgumentException();
                        }
                        if(g < 0 || g > 255) {
                            throw new IllegalArgumentException();
                        }
                        if(b < 0 || b > 255) {
                            throw new IllegalArgumentException();
                        }
                    } catch (Exception e) {
                        HashMap<String, String> usageReplaces = new HashMap<>();
                        usageReplaces.put("USAGE", "/edit_drink "+codeName+" color <r 0-255> <g 0-255> <b 0-255>");
                        sender.sendMessage(
                                languageManager.getMessage("wrongUsage", usageReplaces)
                        );
                        return;
                    }

                    String finalColor = r+", "+g+", "+b;

                    boolean colorEdited = dataManager.set("drinks", codeName, "drinkColor", finalColor);
                    if(colorEdited) {
                        sender.sendMessage(
                                languageManager.getMessage("editedDrink")
                        );
                        drinkManager.reloadDrink(codeName);
                    } else {
                        sender.sendMessage(
                                languageManager.getMessage("notEditedDrink")
                        );
                    }
                    break;
                case "cmd":
                    if(args.length <= 2) {
                        HashMap<String, String> usageReplaces = new HashMap<>();
                        usageReplaces.put("USAGE", "/edit_drink "+codeName+" cmd <new_cmd>");
                        sender.sendMessage(
                                languageManager.getMessage("wrongUsage", usageReplaces)
                        );
                        return;
                    }

                    int cmd;

                    try {
                        cmd = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                        HashMap<String, String> usageReplaces = new HashMap<>();
                        usageReplaces.put("USAGE", "/edit_drink "+codeName+" cmd <new_cmd>");
                        sender.sendMessage(
                                languageManager.getMessage("wrongUsage", usageReplaces)
                        );
                        return;
                    }

                    boolean cmdEdited = dataManager.set("drinks", codeName, "drinkCMD", cmd);
                    if(cmdEdited) {
                        sender.sendMessage(
                                languageManager.getMessage("editedDrink")
                        );
                        drinkManager.reloadDrink(codeName);
                    } else {
                        sender.sendMessage(
                                languageManager.getMessage("notEditedDrink")
                        );
                    }
                    break;
                case "effects":
                    if(args.length <= 3) {
                        HashMap<String, String> usageReplaces = new HashMap<>();
                        usageReplaces.put("USAGE", "/edit_drink "+codeName+" effects <add/remove> <effect_name> [duration_in_ticks] [strength]");
                        sender.sendMessage(
                                languageManager.getMessage("wrongUsage", usageReplaces)
                        );
                        return;
                    }

                    ArrayList<PotionEffect> potionEffects = drink.getDrinkEffects();

                    switch (args[2]) {
                        case "add":

                            if(args.length < 6) {
                                HashMap<String, String> usageReplaces = new HashMap<>();
                                usageReplaces.put("USAGE", "/edit_drink "+codeName+" effects <add/remove> <effect_name> [duration_in_ticks] [strength]");
                                sender.sendMessage(
                                        languageManager.getMessage("wrongUsage", usageReplaces)
                                );
                                return;
                            }

                            try {
                                PotionEffectType effectType = PotionEffectType.getByName(args[3].toUpperCase());
                                int duration = Integer.parseInt(args[4]);
                                int strength = Integer.parseInt(args[5]) - 1;

                                ArrayList<PotionEffect> toRemove = new ArrayList<>();
                                potionEffects.forEach(pe -> {
                                    if(pe.getType().equals(effectType)) {
                                        toRemove.add(pe);
                                    }
                                });
                                potionEffects.removeAll(toRemove);
                                potionEffects.add(new PotionEffect(effectType, duration, strength));
                            } catch (Exception e) {
                                HashMap<String, String> usageReplaces = new HashMap<>();
                                usageReplaces.put("USAGE", "/edit_drink "+codeName+" effects <add/remove> <effect_name> [duration_in_ticks] [strength]");
                                sender.sendMessage(
                                        languageManager.getMessage("wrongUsage", usageReplaces)
                                );
                                return;
                            }

                            StringBuilder finalEffect = new StringBuilder();
                            for(PotionEffect effect : potionEffects) {
                                finalEffect.append(effect.getType().getName()).append(" ").append(effect.getDuration()).append(" ").append(effect.getAmplifier() + 1).append(", ");
                            }

                            boolean editedEffects = dataManager.set("drinks", codeName, "drinkEffects", finalEffect.toString());
                            if(editedEffects) {
                                sender.sendMessage(
                                        languageManager.getMessage("editedDrink")
                                );
                                drinkManager.reloadDrink(codeName);
                            } else {
                                sender.sendMessage(
                                        languageManager.getMessage("notEditedDrink")
                                );
                            }
                            break;
                        case "remove":
                            try {
                                ArrayList<PotionEffect> toRemove = new ArrayList<>();
                                for(PotionEffect potionEffect : potionEffects) {
                                    if(potionEffect.getType().getName().equalsIgnoreCase(args[3])) {
                                        toRemove.add(potionEffect);
                                    }
                                }
                                potionEffects.removeAll(toRemove);
                            } catch (Exception e) {
                                HashMap<String, String> usageReplaces = new HashMap<>();
                                usageReplaces.put("USAGE", "/edit_drink "+codeName+" lore effects <effect_name>");
                                sender.sendMessage(
                                        languageManager.getMessage("wrongUsage", usageReplaces)
                                );
                                return;
                            }

                            StringBuilder finalEffect2 = new StringBuilder();
                            for(PotionEffect effect : potionEffects) {
                                finalEffect2.append(effect.getType().getName()).append(" ").append(effect.getDuration()).append(" ").append(effect.getAmplifier() + 1).append(", ");
                            }

                            boolean editedEffect2 = dataManager.set("drinks", codeName, "drinkEffects", finalEffect2.toString());
                            if(editedEffect2) {
                                sender.sendMessage(
                                        languageManager.getMessage("editedDrink")
                                );
                                drinkManager.reloadDrink(codeName);
                            } else {
                                sender.sendMessage(
                                        languageManager.getMessage("notEditedDrink")
                                );
                            }

                            break;
                    }

                    break;
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if(args.length == 0) {
            return Arrays.asList((String[]) drinkManager.getDrinksName().toArray());
        }

        ArrayList<String> results = new ArrayList<>();

        if(args.length == 1) {
            for(String drinkName : drinkManager.getDrinksName()) {
                if(!drinkName.equalsIgnoreCase("cleanWater") &&
                        !drinkName.equalsIgnoreCase("dirtyWater") &&
                        !drinkManager.getCustomDrinksNames().contains(drinkName) &&
                        drinkName.startsWith(args[0])) {
                    results.add(drinkName);
                }
            }
        }
        if(args.length == 2) {

            ArrayList<String> types = new ArrayList<>();

            types.add("name");
            types.add("lore");
            types.add("points");
            types.add("color");
            types.add("cmd");
            types.add("effects");

            for(String type : types) {
                if(type.startsWith(args[1])) {
                    results.add(type);
                }
            }
        }
        if(args.length == 3) {
            if(args[1].equalsIgnoreCase("lore") || args[1].equalsIgnoreCase("effects")) {

                ArrayList<String> types = new ArrayList<>();

                types.add("add");
                types.add("remove");

                for(String type : types) {
                    if(type.startsWith(args[2])) {
                        results.add(type);
                    }
                }
            }
        }
        return results;
    }
}
