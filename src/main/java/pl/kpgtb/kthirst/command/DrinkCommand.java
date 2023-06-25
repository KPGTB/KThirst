package pl.kpgtb.kthirst.command;

import com.github.kpgtb.ktools.manager.command.KCommand;
import com.github.kpgtb.ktools.manager.command.annotation.Description;
import com.github.kpgtb.ktools.manager.command.annotation.Filter;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import com.j256.ormlite.dao.Dao;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.kpgtb.kthirst.data.DbDrink;
import pl.kpgtb.kthirst.filter.DrinkNotExistsFilter;
import pl.kpgtb.kthirst.gui.DrinkEditGUI;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

@Description("Manage drinks on server")
public class DrinkCommand extends KCommand {
    private final ThirstWrapper wrapper;

    public DrinkCommand(ToolsObjectWrapper toolsObjectWrapper, String groupPath) {
        super(toolsObjectWrapper, groupPath);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
    }


    @Description("Create new drink")
    public void create(CommandSender sender, @Filter(andFilters = {DrinkNotExistsFilter.class}) String name) throws SQLException {
        Dao<DbDrink, String> drinksDAO = wrapper.getDataManager().getDao(DbDrink.class, String.class);

        DbDrink drink = new DbDrink(
                name,
                0.0,
                new ArrayList<>(),
                name,
                new ArrayList<>(),
                Color.BLUE,
                0
        );
        drinksDAO.create(drink);

        wrapper.getDrinkManager().createDrink(drink);

        Audience audience = wrapper.getAdventure().sender(sender);
        wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "drinkCreated").forEach(audience::sendMessage);
    }

    @Description("Edit drink")
    public void edit(Player player, DbDrink drink) {
        new DrinkEditGUI(wrapper,drink,player).open(player);
    }

    @Description("Remove drink")
    public void remove(CommandSender sender, DbDrink drink) throws SQLException {
        Dao<DbDrink, String> drinksDAO = wrapper.getDataManager().getDao(DbDrink.class, String.class);
        drinksDAO.delete(drink);

        wrapper.getDrinkManager().removeDrink(drink);

        Audience audience = wrapper.getAdventure().sender(sender);
        wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "drinkRemoved").forEach(audience::sendMessage);
    }

}
