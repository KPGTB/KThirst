package io.github.kpgtb.kkthirst.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class InventoryHelper_1_17 implements IInventoryHelper{
    @Override
    public void updateInventoryTitle(Player player, String title) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        // 1.17
        //PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.bV.j,ep.bV.getType(), new ChatMessage("Ciekawee"));
        //ep.b.sendPacket(packet);
        //ep.bV.updateInventory();

        Object entityPlayer = getCraftBukkitClass("entity.CraftPlayer")
                .getMethod("getHandle")
                .invoke(player);

        Object activeContainer = entityPlayer.getClass()
                .getField("bV")
                .get(entityPlayer);
        Object windowId = activeContainer.getClass()
                .getField("j")
                .get(activeContainer);
        Object type = activeContainer.getClass()
                .getMethod("getType")
                .invoke(activeContainer);
        Object chatMessage = getNMClass("network.chat.ChatMessage")
                .getDeclaredConstructor(String.class, Object[].class)
                .newInstance(title, new Object[0]);

        Object packet = getNMClass("network.protocol.game.PacketPlayOutOpenWindow")
                .getDeclaredConstructor(int.class, getNMClass("world.inventory.Containers"), getNMClass("network.chat.IChatBaseComponent"))
                .newInstance(windowId, type, chatMessage);
        sendPacket(entityPlayer, packet);

        activeContainer.getClass()
                .getMethod("updateInventory")
                .invoke(activeContainer);
    }

    private void sendPacket(Object entityPlayer, Object packet) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        Object playerConnection = entityPlayer.getClass()
                .getField("b")
                .get(entityPlayer);
        playerConnection.getClass()
                .getMethod("sendPacket", getNMClass("network.protocol.Packet"))
                .invoke(playerConnection, packet);
    }


    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit."+getVersion()+"."+name);
    }

    private Class<?> getNMClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft."+name);
    }
}
