package pl.kpgtb.kkthirst.data.persister;

import com.github.kpgtb.ktools.util.ItemBuilder;
import com.google.gson.Gson;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemsPersister extends StringType {
    private static final ItemsPersister singleton = new ItemsPersister();

    protected ItemsPersister() {
        super(SqlType.STRING, new Class<?>[]{List.class});
    }

    public static ItemsPersister getSingleton() {
        return singleton;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        List<String> serializedItems = new ArrayList<>();
        List<ItemStack> items = (List<ItemStack>) javaObject;
        items.forEach(item -> {
            serializedItems.add(new ItemBuilder(item).toJson());
        });
        return new Gson().toJson(serializedItems);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        List<ItemStack> result = new ArrayList<>();
        new Gson().fromJson((String) sqlArg, List.class).forEach(si -> {
            result.add(ItemBuilder.fromJson((String) si).build());
        });
        return result;
    }
}
