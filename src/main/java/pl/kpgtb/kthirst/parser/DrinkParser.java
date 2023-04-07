package pl.kpgtb.kthirst.parser;

import com.github.kpgtb.ktools.manager.command.parser.IParamParser;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pl.kpgtb.kthirst.data.DbDrink;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DrinkParser implements IParamParser<DbDrink> {
    @Override
    public DbDrink convert(String param, ToolsObjectWrapper wrapper) {
        return ((ThirstWrapper)wrapper).getDrinkManager().getDrinks().get(param);
    }

    @Override
    public boolean canConvert(String param, ToolsObjectWrapper wrapper) {
        try {
            return wrapper.getDataManager().getDao(DbDrink.class, String.class).idExists(param);
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public @NotNull List<String> complete(String arg, CommandSender sender, ToolsObjectWrapper wrapper) {
        try {
            return wrapper.getDataManager().getDao(DbDrink.class, String.class).queryForAll()
                    .stream()
                    .map(DbDrink::getCode)
                    .filter(name -> name.startsWith(arg))
                    .limit(30)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
}
