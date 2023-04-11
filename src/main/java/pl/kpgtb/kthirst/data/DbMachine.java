package pl.kpgtb.kthirst.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import pl.kpgtb.kthirst.data.persister.ItemsPersister;

import java.util.List;

@DatabaseTable(tableName = "thirst_machines")
public class DbMachine {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String type;
    @DatabaseField(persisterClass = ItemsPersister.class)
    private List<ItemStack> ingredients;
    @DatabaseField(persisterClass = ItemsPersister.class)
    private List<ItemStack> results;
    @DatabaseField
    private String actualRecipeName;
    @DatabaseField
    private int progressTime;
    @DatabaseField
    private Location location;

    public DbMachine() {}

    public DbMachine(int id, String type, List<ItemStack> ingredients, List<ItemStack> results, String actualRecipeName, int progressTime, Location location) {
        this.id = id;
        this.type = type;
        this.ingredients = ingredients;
        this.results = results;
        this.actualRecipeName = actualRecipeName;
        this.progressTime = progressTime;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ItemStack> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ItemStack> ingredients) {
        this.ingredients = ingredients;
    }

    public List<ItemStack> getResults() {
        return results;
    }

    public void setResults(List<ItemStack> results) {
        this.results = results;
    }

    public String getActualRecipeName() {
        return actualRecipeName;
    }

    public void setActualRecipeName(String actualRecipeName) {
        this.actualRecipeName = actualRecipeName;
    }

    public int getProgressTime() {
        return progressTime;
    }

    public void setProgressTime(int progressTime) {
        this.progressTime = progressTime;
    }
}
