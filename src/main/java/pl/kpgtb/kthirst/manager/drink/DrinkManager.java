package pl.kpgtb.kthirst.manager.drink;

import com.github.kpgtb.ktools.manager.item.builder.KItemBuilder;
import com.github.kpgtb.ktools.manager.ui.bar.BarManager;
import com.github.kpgtb.ktools.manager.ui.bar.KBar;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.kpgtb.kthirst.data.DbDrink;
import pl.kpgtb.kthirst.data.type.DrinkEffect;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DrinkManager {
    private final HashMap<String, DbDrink> drinks;
    private final ArrayList<String> addonDrinks;

    private ThirstWrapper wrapper;

    public DrinkManager() {
        this.drinks = new HashMap<>();
        this.addonDrinks = new ArrayList<>();
    }

    public void setWrapper(ThirstWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public void registerAddon(DbDrink drink) {
        createDrink(drink);
        addonDrinks.add(drink.getCode());
    }
    public void registerServerDrinks() throws SQLException {
        wrapper.getDataManager().getDao(DbDrink.class, String.class).queryForAll().forEach(this::createDrink);
    }

    public void createDrink(DbDrink drink) {
        reloadDrink(drink);
    }
    public void removeDrink(DbDrink drink) {
        wrapper.getItemManager().unregisterItem("kthirst:"+drink.getCode());
        drinks.remove(drink.getCode());
    }
    public void reloadDrink(DbDrink drink) {
        removeDrink(drink);
        drinks.put(drink.getCode(), drink);
        KItemBuilder builder = new KItemBuilder(wrapper, "kthirst", drink.getCode(), prepareDrinkItem(drink));
        builder.setOnConsumeAction(e -> handleDrink(drink,e.getPlayer()));
        builder.register();
    }

    private ItemStack prepareDrinkItem(DbDrink drink) {
        ItemStack item = new ItemStack(Material.POTION);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(drink.getLore());
        meta.setDisplayName(drink.getName());
        meta.setCustomModelData(drink.getCustomModelData());
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        PotionMeta potion = (PotionMeta) meta;
        potion.setColor(Color.fromRGB(drink.getColor().getRed(), drink.getColor().getGreen(), drink.getColor().getBlue()));
        item.setItemMeta(potion);
        return item;
    }
    private void handleDrink(DbDrink drink, Player player) {
        BarManager barManager = wrapper.getBarManager();
        KBar bar = wrapper.getThirstBar();
        barManager.setValue(
                bar,
                player,
                barManager.getValue(bar,player) + drink.getPoints()
        );
        for(DrinkEffect effect : drink.getEffects()) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.getByName(effect.getType()),
                    effect.getDuration(),
                    effect.getAmplifier()
            ));
        }
    }

    public HashMap<String, DbDrink> getDrinks() {
        return drinks;
    }
    public ArrayList<String> getAddonDrinks() {
        return addonDrinks;
    }
}
