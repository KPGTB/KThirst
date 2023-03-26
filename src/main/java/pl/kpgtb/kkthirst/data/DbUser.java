package pl.kpgtb.kkthirst.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "thirst_users")
public class DbUser {
    @DatabaseField(id = true)
    private UUID uuid;

    @DatabaseField
    private double thirst;

    public DbUser() {}

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public double getThirst() {
        return thirst;
    }

    public void setThirst(double thirst) {
        this.thirst = thirst;
    }
}
