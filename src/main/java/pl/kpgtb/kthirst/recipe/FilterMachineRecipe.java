package pl.kpgtb.kthirst.recipe;

import com.github.kpgtb.ktools.manager.recipe.KRecipe;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class FilterMachineRecipe extends KRecipe {
    private final NamespacedKey key;
    private final ToolsObjectWrapper wrapper;

    public FilterMachineRecipe(NamespacedKey recipeKey, ToolsObjectWrapper toolsObjectWrapper) {
        super(recipeKey, toolsObjectWrapper);
        this.key = recipeKey;
        this.wrapper = toolsObjectWrapper;
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, wrapper.getItemManager().getCustomItem("kthirst", "filter_machine"));
        shapedRecipe.shape("iwi", "ifi", "ili");
        shapedRecipe.setIngredient('i', Material.IRON_INGOT);
        shapedRecipe.setIngredient('f', Material.FLINT_AND_STEEL);
        shapedRecipe.setIngredient('l', Material.OAK_LOG);
        shapedRecipe.setIngredient('w', new RecipeChoice.ExactChoice(
                wrapper.getItemManager().getCustomItem("kthirst", "dirty_water")
        ));
        return shapedRecipe;
    }

    @Override
    public boolean autoDiscover() {
        return true;
    }

}
