package pl.kpgtb.kthirst.data.persister;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import pl.kpgtb.kthirst.data.type.DrinkEffect;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EffectsPersister extends StringType {
    private static final EffectsPersister singleton = new EffectsPersister();

    protected EffectsPersister() {
        super(SqlType.STRING, new Class<?>[]{List.class});
    }

    public static EffectsPersister getSingleton() {
        return singleton;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        return new Gson().toJson(javaObject);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        Gson gson = new Gson();
        List<DrinkEffect> result = new ArrayList<>();
        JsonElement json = new JsonParser().parse((String) sqlArg);
        for(JsonElement el : json.getAsJsonArray()) {
            result.add(
                    gson.fromJson(el, DrinkEffect.class)
            );
        }
        return result;
    }
}
