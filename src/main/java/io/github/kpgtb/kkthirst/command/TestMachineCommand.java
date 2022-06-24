package io.github.kpgtb.kkthirst.command;

import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.manager.command.CommandInfo;
import io.github.kpgtb.kkcore.manager.command.KKcommand;
import io.github.kpgtb.kkthirst.KKthirst;
import io.github.kpgtb.kkthirst.nms.InventoryHelper_1_18;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;

@CommandInfo(name = "testMachine")
public class TestMachineCommand extends KKcommand {
    public TestMachineCommand(UsefulObjects usefulObjects) {
        super(usefulObjects);
    }

    String invName =  "\uF808§f\uF901\uF80C\uF80A\uF808§rFiltering machine§f\uF808\uF801";
    Inventory inv = Bukkit.createInventory(null, 27, invName);


    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        Player player = Bukkit.getPlayer(args[1]);
        if(args[0].equalsIgnoreCase("1")) {
            player.openInventory(inv);

            final int[] count = {0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(count[0] < 9) {
                        count[0]++;
                        StringBuilder newName = new StringBuilder(invName);
                        for (int i = 0; i < count[0]; i++) {
                            newName.append("\uF902\uF801");
                        }
                        try {
                            if(!inv.getViewers().contains(player)) {
                                cancel();
                                return;
                            }
                            new InventoryHelper_1_18().updateInventoryTitle(player, newName.toString());
                        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                                 IllegalAccessException | NoSuchFieldException | InstantiationException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimerAsynchronously(JavaPlugin.getPlugin(KKthirst.class),20,20);

        } else {

        }
    }
}
