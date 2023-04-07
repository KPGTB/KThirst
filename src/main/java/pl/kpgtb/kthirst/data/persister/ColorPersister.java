package pl.kpgtb.kthirst.data.persister;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

import java.awt.*;
import java.sql.SQLException;

public class ColorPersister extends StringType {

    private static final ColorPersister singleTon = new ColorPersister();
    private final Color defaultColor = new Color(0,0,0);

    protected ColorPersister() {
        super(SqlType.STRING, new Class<?>[]{Color.class});
    }

    public static ColorPersister getSingleton() {
        return singleTon;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        Color color = defaultColor;
        if(javaObject instanceof Color) {
            color = (Color) javaObject;
        }

        return color.getRed() + " " + color.getGreen() + " " + color.getBlue();
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        if(!(sqlArg instanceof String)) {
            return defaultColor;
        }
        String[] colors = ((String) sqlArg).split(" ");
        if(colors.length != 3) {
            return defaultColor;
        }

        return new Color(
                Integer.parseInt(colors[0]),
                Integer.parseInt(colors[1]),
                Integer.parseInt(colors[2])
        );
    }
}
