package pl.kpgtb.kthirst.util;

import com.github.kpgtb.ktools.manager.ui.bar.KBar;
import com.github.kpgtb.ktools.util.wrapper.ToolsInitializer;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import pl.kpgtb.kthirst.manager.drink.DrinkManager;
import pl.kpgtb.kthirst.manager.machine.MachineManager;
import pl.kpgtb.kthirst.manager.user.UserManager;

public class ThirstWrapper extends ToolsObjectWrapper {
    private final DrinkManager drinkManager;
    private final UserManager userManager;
    private final MachineManager machineManager;
    private final KBar thirstBar;

    public ThirstWrapper(ToolsInitializer initializer, DrinkManager drinkManager, UserManager userManager, MachineManager machineManager, KBar thirstBar) {
        super(initializer);
        this.drinkManager = drinkManager;
        this.userManager = userManager;
        this.machineManager = machineManager;
        this.thirstBar = thirstBar;
    }

    public DrinkManager getDrinkManager() {
        return drinkManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public MachineManager getMachineManager() {
        return machineManager;
    }

    public KBar getThirstBar() {
        return thirstBar;
    }
}
