package pl.kpgtb.kthirst.recipe;

import com.github.kpgtb.ktools.manager.recipe.Krecipe;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class VanillaWaterRecipe extends Krecipe {
    private final NamespacedKey key;
    private final ToolsObjectWrapper wrapper;

    public VanillaWaterRecipe(NamespacedKey recipeKey, ToolsObjectWrapper toolsObjectWrapper) {
        super(recipeKey, toolsObjectWrapper);
        this.key = recipeKey;
        this.wrapper = toolsObjectWrapper;
    }

    @Override
    public Recipe getRecipe() {
        ItemStack waterBottle = new ItemStack(Material.POTION, 1);
        PotionMeta potionMeta = (PotionMeta) waterBottle.getItemMeta();
        potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
        waterBottle.setItemMeta(potionMeta);

        ShapelessRecipe recipe = new ShapelessRecipe(key,waterBottle);
        recipe.addIngredient(new RecipeChoice.ExactChoice(
                wrapper.getItemManager().getCustomItem("kthirst", "dirty_water")
        ));

        return recipe;
    }

    @Override
    public boolean autoDiscover() {
        return true;
    }
}
