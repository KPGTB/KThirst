package io.github.kpgtb.kkthirst.command;

import io.github.kpgtb.kkcore.manager.LanguageManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkcore.manager.command.CommandInfo;
import io.github.kpgtb.kkcore.manager.command.KKcommand;
import io.github.kpgtb.kkthirst.manager.MachineManager;
import io.github.kpgtb.kkthirst.object.BaseMachine;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@CommandInfo(name = "get_machine", description = "With this command you can get any machine from KKthirst", permission = "kkthirst.getmachine", requiredPlayer = true,requiredArgs = true,argsCount = 1,usage = "/get_machine <machine_type>")
public class GetMachineCommand extends KKcommand {
    private final MachineManager machineManager;
    private final LanguageManager languageManager;

    public GetMachineCommand(UsefulObjects usefulObjects) {
        super(usefulObjects);
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating GetMachineCommand!");
            Bukkit.shutdown();
        }

        machineManager = thirstUsefulObjects.getMachineManager();
        languageManager = thirstUsefulObjects.getLanguageManager();
    }

    @Override
    public void executeCommand(Player player, String[] args) {
        BaseMachine machine = machineManager.getMachine(args[0]);

        if(machine == null) {
            player.sendMessage(
                    languageManager.getMessage("machineNotFound", player, new HashMap<>())
            );
            return;
        }

        player.getInventory().addItem(machine.getMachineItemStack());
        player.sendMessage(
                languageManager.getMessage("getMachineFromCommand", player, new HashMap<>())
        );
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if(args.length == 0) {
            return Arrays.asList((String[]) machineManager.getMachinesName().toArray());
        }

        if(args.length == 1) {
            ArrayList<String> results = new ArrayList<>();
            for(String machineName : machineManager.getMachinesName()) {
                if(machineName.startsWith(args[0])) {
                    results.add(machineName);
                }
            }
            return results;
        }
        return new ArrayList<>();
    }
}
