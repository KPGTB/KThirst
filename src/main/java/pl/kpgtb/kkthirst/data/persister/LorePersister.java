package pl.kpgtb.kkthirst.data.persister;

import com.google.gson.Gson;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

import java.sql.SQLException;
import java.util.List;

public class LorePersister extends StringType {
    private static final LorePersister singleton = new LorePersister();

    protected LorePersister() {
        super(SqlType.STRING, new Class<?>[]{List.class});
    }

    public static LorePersister getSingleton() {
        return singleton;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        return new Gson().toJson(javaObject);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        return new Gson().fromJson((String) sqlArg,List.class);
    }
}
