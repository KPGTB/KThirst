package pl.kpgtb.kthirst.util;

import org.bukkit.configuration.ConfigurationSection;
import pl.kpgtb.kthirst.data.type.DrinkEffect;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtil {
    public static Color getColor(ConfigurationSection section) {
        return new Color(section.getInt("r"), section.getInt("g"), section.getInt("b"));
    }
    public static List<DrinkEffect> getEffects(List<String> effects) {
        List<DrinkEffect> result = new ArrayList<>();
        effects.forEach(line -> {
            String[] elements = line.split(" ");
            result.add(new DrinkEffect(
                    elements[0].toUpperCase(),
                    Integer.parseInt(elements[1]),
                    Integer.parseInt(elements[2])
            ));
        });
        return  result;
    }
}
