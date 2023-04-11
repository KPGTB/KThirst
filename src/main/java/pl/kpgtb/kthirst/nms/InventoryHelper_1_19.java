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

package pl.kpgtb.kthirst.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class InventoryHelper_1_19 implements IInventoryHelper{
    @Override
    public void updateInventoryTitle(Player player, String title) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {

        Object entityPlayer = getCraftBukkitClass("entity.CraftPlayer")
                .getMethod("getHandle")
                .invoke(player);

        Object activeContainer = entityPlayer.getClass()
                .getField("bU")
                .get(entityPlayer);
        Object windowId = activeContainer.getClass()
                .getField("j")
                .get(activeContainer);
        Object type = activeContainer.getClass()
                .getMethod("a")
                .invoke(activeContainer);
        Object chatMessage = getNMClass("network.chat.IChatBaseComponent")
                .getMethod("a", String.class)
                .invoke(getNMClass("network.chat.IChatBaseComponent"), title);

        Object packet = getNMClass("network.protocol.game.PacketPlayOutOpenWindow")
                .getDeclaredConstructor(int.class, getNMClass("world.inventory.Containers"), getNMClass("network.chat.IChatBaseComponent"))
                .newInstance(windowId, type, chatMessage);
        sendPacket(entityPlayer, packet);

        activeContainer.getClass()
                .getMethod("b")
                .invoke(activeContainer);
    }

    private void sendPacket(Object entityPlayer, Object packet) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        Object playerConnection = entityPlayer.getClass()
                .getField("b")
                .get(entityPlayer);
        playerConnection.getClass()
                .getMethod("a", getNMClass("network.protocol.Packet"))
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
