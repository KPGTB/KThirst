# KKthirst
[![](https://jitpack.io/v/KPGTB/KKthirst.svg)](https://jitpack.io/#KPGTB/KKthirst)
![Spigot](https://img.shields.io/badge/Spigot-1.13--1.19-yellow)
![License](https://img.shields.io/badge/License-Apache%202.0-orange)
![Author](https://img.shields.io/badge/Author-KPG--TB-green)

Spigot plugin for minecraft 1.13.-1.19. This plugin adds thirst, drinks and machines

---

This plugin requires resource pack!

---

# Instalation
1. Download [KKcore](https://github.com/KPGTB/KKcore/releases/tag/v1.0). [KKui](https://github.com/KPGTB/KKui/releases/tag/v1.1), [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) and [KKthirst](https://github.com/KPGTB/KKthirst/releases/tag/v1.1)
2. Put plugins into /plugins folder
3. Start server to generate files
4. Disable server
5. Generate and download [resource pack](https://kpgtb.github.io/KK_resourcepack_generator_website/)
6. Put resource pack in any file hosting (ex. mediafire) and copy link to download
7. Open /plugins/KKui/config.yml and paste link to resource pack in section `resourcePack:`
8. If you want to change saving method, change file /plugins/KKcore/config.yml
9. If you want to translate plugins, open /plugins/KKcore/config.yml and read instructions
10. Start server

### All players must have this resource pack (auto download)!

# What this plugin adds?

### Thirst

Every one minute you lost thirst points. 0 = damage and death

### UI with points of thirst above food

![UI](https://i.imgur.com/SR2P4zs.png)

### Drinks

There is 2 default drinks
1. Dirty water - you can get it from right click water when you have glass bottle in hand
2. Clean water - when you filter dirty water in Filter Machine

You can create your own drink with commands!

### Machine with custom GUI

##### Crafting:
![machineCrafting](https://i.imgur.com/r2MzZ4C.png)

##### Usage

![machineGUI](https://i.imgur.com/mbc01Yh.png)

### Commands

##### Getting commands
1. `/get_drink` -> Get any drink. Permission: `kkthirst.getdrink`'
2. `/give_drink` -> Give any drink to player. Permission: `kkthirst.givedrink`
3. `/get_machine` -> Get any machine. Permission: `kkthirst.getmachine`

##### Manage commands
1. `/add_drink` -> Add new drink. Permission: `kkthirst.adddrink`
2. `/edit_drink` -> Edit created drink. Permission: `kkthirst.editdrink`
3. `/remove_drink` -> Remove created drink. Permission: `kkthirst.removedrink`

# Maven

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
    <groupId>com.github.KPGTB</groupId>
    <artifactId>KKthirst</artifactId>
    <version>LATEST</version>
</dependency>
```

# API

##### Creating new drink (Without save in database):
```java
Drink drink = new Drink(String codeName, double thirstPoints, String drinkName, ArrayList<String> drinkLore, Color drinkColor, int drinkCustomModelData, ArrayList<PotionEffect> drinkEffects);
KKthirst.getDrinkManager().registerCustomDrink(Drink drink);
```

##### Creating new machine:
```java
MachineManager machineManager = KKthirst.getMachineManager();
BaseMachine baseMachine = machineManager.registerMachine(String machineType, String inventoryTitle, int inventorySize, int[] ingredientSlots, int[] resultSlots, Character customInventoryChar, String progressBarChars, int progressBarLength, ItemStack machineItemStack, boolean replace);
```
You must add custom char with image to resource pack!

Example (Filter Machine):
```java
BaseMachine filterMachine = machineManager.registerMachine(
    "filterMachine",
    "Filter machine",
    27,
    new int[]{12},
    new int[]{14},
    '\uF901',
    "\uF902\uF801",
    1,
    9,
    75,
    filterMachineItemStack,
    true
);
```

##### Adding recipe to machine
```java
MachineManager machineManager = KKthirst.getMachineManager();
BaseMachine baseMachine= machineManager.getMachine(String machineType);
MachineRecipe machineRecipe = new MachineRecipe(String recipeName, ItemStack[] ingredients, ItemStack[] result, int workTime);
baseMachine.registerRecipe(String recipeName, MachineRecipe recipe);
```

# License

Apache 2.0






