package pl.kpgtb.kthirst.nms;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class InventoryHelper_1_20 implements IInventoryHelper{
    @Override
    public void updateInventoryTitle(Player player, String title) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        player.getOpenInventory().setTitle(title);
    }
}
