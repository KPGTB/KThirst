package pl.kpgtb.kthirst.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

public class MmUtil {
    public static String parse(String string) {
        Component component = MiniMessage.miniMessage().deserialize(string);
        boolean isHexSupport = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]) >= 16;
        if (isHexSupport) {
            return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(component);
        } else {
            return LegacyComponentSerializer.builder().build().serialize(component);
        }
    }
}
