package pl.kpgtb.kthirst.data.persister;

import com.github.kpgtb.ktools.util.item.ItemUtil;
import com.google.gson.Gson;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
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
            try {
                serializedItems.add(ItemUtil.serializeItem(item));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return new Gson().toJson(serializedItems);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        List<ItemStack> result = new ArrayList<>();
        new Gson().fromJson((String) sqlArg, List.class).forEach(si -> {
            try {
                result.add(ItemUtil.deserializeItem((String) sqlArg));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }
}
