package pl.kpgtb.kthirst.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.kpgtb.kthirst.data.persister.ColorPersister;
import pl.kpgtb.kthirst.data.persister.EffectsPersister;
import pl.kpgtb.kthirst.data.type.DrinkEffect;

import java.awt.*;
import java.util.List;

@DatabaseTable(tableName = "thirst_drinks")
public class DbDrink {
    @DatabaseField(id = true)
    private String code;

    @DatabaseField
    private double points;
    @DatabaseField(persisterClass = EffectsPersister.class)
    private List<DrinkEffect> effects;

    // ITEM
    @DatabaseField
    private String name;
    @DatabaseField
    private List<String> lore;
    @DatabaseField(persisterClass = ColorPersister.class)
    private Color color;
    @DatabaseField
    private int customModelData;

    public DbDrink() {}

    public DbDrink(String code, double points, List<DrinkEffect> effects, String name, List<String> lore, Color color, int customModelData) {
        this.code = code;
        this.points = points;
        this.effects = effects;
        this.name = name;
        this.lore = lore;
        this.color = color;
        this.customModelData = customModelData;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public List<DrinkEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<DrinkEffect> effects) {
        this.effects = effects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }
}
