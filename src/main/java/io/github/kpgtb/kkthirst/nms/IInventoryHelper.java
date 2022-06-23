package io.github.kpgtb.kkthirst.nms;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public interface IInventoryHelper {
    void updateInventoryTitle(Player player, String title) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException;
}
