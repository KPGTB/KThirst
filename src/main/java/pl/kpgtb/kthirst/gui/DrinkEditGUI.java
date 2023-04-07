package pl.kpgtb.kthirst.gui;

import com.github.kpgtb.ktools.manager.gui.KCountGui;
import com.github.kpgtb.ktools.manager.gui.KGui;
import com.github.kpgtb.ktools.manager.gui.KWriteGui;
import com.github.kpgtb.ktools.manager.gui.container.GuiContainer;
import com.github.kpgtb.ktools.manager.gui.item.GuiItem;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.item.ItemBuilder;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.kpgtb.kthirst.data.DbDrink;
import pl.kpgtb.kthirst.util.ThirstWrapper;

public class DrinkEditGUI {
    private final ThirstWrapper wrapper;
    private final DbDrink drink;
    private final Player player;

    public DrinkEditGUI(ThirstWrapper wrapper, DbDrink drink, Player player) {
        this.wrapper = wrapper;
        this.drink = drink;
        this.player = player;
    }

    public void open() {
        KGui gui = new KGui(
            wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "editGuiName", Placeholder.unparsed("drink", drink.getCode())),
            5,
                wrapper
        );

        gui.blockClick();

        GuiItem borderItem = new GuiItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayname(" ").build());
        borderItem.setClickAction(e -> e.getWhoClicked().closeInventory());

        GuiContainer leftBorder = new GuiContainer(gui,0,0,1,5);
        leftBorder.fill(borderItem);
        gui.addContainer(leftBorder);

        GuiContainer rightBorder = new GuiContainer(gui,8,0,1,5);
        rightBorder.fill(borderItem);
        gui.addContainer(rightBorder);

        GuiContainer mainContainer = new GuiContainer(gui, 1,0,7,5);

        GuiItem changeName = new GuiItem(
                new ItemBuilder(Material.PAPER)
                        .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "changeName"))
                        .lore(drink.getName())
                        .build()
        );
        changeName.setClickAction(e -> {
            new KWriteGui(wrapper,gui, (Player) e.getWhoClicked(),(text) -> {

                GuiItem newChangeName = new GuiItem(
                        new ItemBuilder(Material.PAPER)
                                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "changeName"))
                                .lore(text)
                                .build()
                );
                mainContainer.setItem(2,1,newChangeName);
                gui.update();

            }).open();
        });
        mainContainer.setItem(2,1,changeName);

        GuiItem changeCustomModelData = new GuiItem(
                new ItemBuilder(Material.ITEM_FRAME)
                        .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "changeCustomModelData"))
                        .lore(drink.getCustomModelData()+"")
                        .build()
        );
        changeCustomModelData.setClickAction(e -> {
            new KCountGui(wrapper,gui,(value) -> {

                GuiItem newChangeCustomModelData = new GuiItem(
                        new ItemBuilder(Material.ITEM_FRAME)
                                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "changeCustomModelData"))
                                .lore(value+"")
                                .build()
                );
                mainContainer.setItem(4,1,newChangeCustomModelData);
                gui.update();

            }, player,drink.getCustomModelData(), 0, 10000,false,Material.ITEM_FRAME).open();
        });
        mainContainer.setItem(4,1,changeCustomModelData);

        gui.addContainer(mainContainer);
    }
}
