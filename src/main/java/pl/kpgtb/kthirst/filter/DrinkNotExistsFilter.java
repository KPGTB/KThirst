package pl.kpgtb.kthirst.filter;

import com.github.kpgtb.ktools.manager.command.filter.IFilter;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.util.List;

public class DrinkNotExistsFilter implements IFilter<String> {
    @Override
    public boolean filter(String obj, ToolsObjectWrapper wrapper, CommandSender sender) {
        return !((ThirstWrapper) wrapper).getDrinkManager().getDrinks().containsKey(obj);
    }

    @Override
    public List<Component> notPassMessage(String obj, ToolsObjectWrapper wrapper, CommandSender sender) {
        return wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "drinkExists");
    }

    @Override
    public int weight() {
        return 0;
    }
}
