package pl.kpgtb.kthirst.gui;

import com.github.kpgtb.ktools.manager.gui.KCountGui;
import com.github.kpgtb.ktools.manager.gui.KGui;
import com.github.kpgtb.ktools.manager.gui.KWriteGui;
import com.github.kpgtb.ktools.manager.gui.container.GuiContainer;
import com.github.kpgtb.ktools.manager.gui.item.GuiItem;
import com.github.kpgtb.ktools.manager.gui.item.common.CloseItem;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.item.ItemBuilder;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.kpgtb.kthirst.data.DbDrink;
import pl.kpgtb.kthirst.data.type.DrinkEffect;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class DrinkEditGUI {
    private final ThirstWrapper wrapper;
    private final DbDrink drink;
    private final Player player;


    private String newName;
    private List<String> newLore;
    private int newCMD;
    private Color newColor;
    private List<DrinkEffect> newEffects;
    private double newPoints;

    public DrinkEditGUI(ThirstWrapper wrapper, DbDrink drink, Player player) {
        this.wrapper = wrapper;
        this.drink = drink;
        this.player = player;

        this.newName = drink.getName();
        this.newLore = drink.getLore();
        this.newCMD = drink.getCustomModelData();
        this.newColor = drink.getColor();
        this.newEffects = drink.getEffects();
        this.newPoints = drink.getPoints();
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

        {
            GuiContainer leftBorder = new GuiContainer(gui, 0, 0, 1, 5);
            leftBorder.setItem(0,4, CloseItem.get(wrapper));
            leftBorder.fillEmptySlots(borderItem);
            gui.addContainer(leftBorder);
        } // Left Border

        {
            GuiContainer rightBorder = new GuiContainer(gui, 8, 0, 1, 5);

            GuiItem saveItem = new GuiItem(
                    new ItemBuilder(Material.EMERALD)
                            .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "applyEdit"))
                            .build()
            );
            saveItem.setClickAction(e -> {
                e.getWhoClicked().closeInventory();
                wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "drinkEdited")
                            .forEach(msg -> wrapper.getAdventure().player((Player) e.getWhoClicked()).sendMessage(msg));

                drink.setColor(newColor);
                drink.setLore(newLore);
                drink.setEffects(newEffects);
                drink.setName(newName);
                drink.setCustomModelData(newCMD);
                drink.setPoints(newPoints);

                try {
                    wrapper.getDataManager().getDao(DbDrink.class, String.class).update(drink);
                    wrapper.getDrinkManager().reloadDrink(drink);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
            rightBorder.setItem(0,4,saveItem);

            rightBorder.fillEmptySlots(borderItem);
            gui.addContainer(rightBorder);
        } // Right Border

        {
            GuiContainer mainContainer = new GuiContainer(gui, 1, 0, 7, 5);

            {
                GuiItem changeName = new GuiItem(
                        new ItemBuilder(Material.PAPER)
                                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "changeName"))
                                .lore(newName)
                );
                changeName.setClickAction(e -> {
                    new KWriteGui(wrapper, gui, (Player) e.getWhoClicked(), (text) -> {
                        newName = wrapper.getLanguageManager().convertMmToString(text);
                        changeName.setItemBuilder(
                                changeName.getItemBuilder().lore(newName,0)
                        );
                        gui.update();
                    }).open();
                });
                mainContainer.setItem(1, 1, changeName);
            } // Name

            {
                GuiItem changePoints = new GuiItem(
                        new ItemBuilder(Material.GLASS_BOTTLE)
                                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "changePoints"))
                                .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "currentPoints", Placeholder.unparsed("value", newPoints+"")))
                );
                changePoints.setClickAction(e -> {
                    new KCountGui(wrapper, gui, (value) -> {
                        newPoints = value;
                        changePoints.setItemBuilder(
                                changePoints.getItemBuilder()
                                        .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "currentPoints", Placeholder.unparsed("value", newPoints+"")))
                        );
                        gui.update();
                    }, player, newPoints, 0, 1000, true, Material.GLASS_BOTTLE).open();
                });
                mainContainer.setItem(1, 2, changePoints);
            } // Points

            {
                GuiItem changeCustomModelData = new GuiItem(
                        new ItemBuilder(Material.ITEM_FRAME)
                                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "changeCustomModelData"))
                                .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "currentCustomModelData", Placeholder.unparsed("value", newCMD+"")))
                );
                changeCustomModelData.setClickAction(e -> {
                    new KCountGui(wrapper, gui, (value) -> {
                        newCMD = (int) value;
                        changeCustomModelData.setItemBuilder(
                                changeCustomModelData.getItemBuilder()
                                        .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "currentCustomModelData", Placeholder.unparsed("value", newCMD+"")))
                        );
                        gui.update();
                    }, player, drink.getCustomModelData(), 0, 10000, false, Material.ITEM_FRAME).open();
                });
                mainContainer.setItem(1, 3, changeCustomModelData);
            } // Custom Model Data

            {
                GuiItem changeRed = new GuiItem(new ItemBuilder(Material.RED_DYE)
                        .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "changeColorRed"))
                        .lore(ChatColor.RED + "" + newColor.getRed())
                );
                changeRed.setClickAction(e -> {
                    new KCountGui(wrapper, gui, (value) -> {
                        newColor = new Color((int) value, newColor.getGreen(), newColor.getBlue());
                        changeRed.setItemBuilder(
                                changeRed.getItemBuilder()
                                        .lore(ChatColor.RED + "" + newColor.getRed(),0)
                        );
                        gui.update();
                    }, player, drink.getColor().getRed(), 0, 255, false, Material.RED_DYE).open();
                });
                mainContainer.setItem(3, 1, changeRed);
            } // R

            {
                GuiItem changeGreen = new GuiItem(new ItemBuilder(Material.GREEN_DYE)
                        .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "changeColorGreen"))
                        .lore(ChatColor.GREEN + "" + newColor.getGreen())
                );
                changeGreen.setClickAction(e -> {
                    new KCountGui(wrapper, gui, (value) -> {
                        newColor = new Color(newColor.getRed(), (int) value, newColor.getBlue());
                        changeGreen.setItemBuilder(
                                changeGreen.getItemBuilder()
                                        .lore(ChatColor.GREEN + "" + newColor.getGreen(),0)
                        );
                        gui.update();
                    }, player, drink.getColor().getGreen(), 0, 255, false, Material.GREEN_DYE).open();
                });
                mainContainer.setItem(3, 2, changeGreen);
            } // G

            {
                GuiItem changeBlue = new GuiItem(new ItemBuilder(Material.BLUE_DYE)
                        .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "changeColorBlue"))
                        .lore(ChatColor.BLUE + "" + newColor.getBlue())
                        .build()
                );
                changeBlue.setClickAction(e -> {
                    new KCountGui(wrapper, gui, (value) -> {
                        newColor = new Color(newColor.getRed(), newColor.getGreen(), (int) value);
                        changeBlue.setItemBuilder(
                                changeBlue.getItemBuilder()
                                        .lore(ChatColor.BLUE + "" + newColor.getBlue(),0)
                        );
                        gui.update();

                    }, player, drink.getColor().getBlue(), 0, 255, false, Material.BLUE_DYE).open();
                });
                mainContainer.setItem(3, 3, changeBlue);
            } // B

            {
                GuiItem changeLore = new GuiItem(new ItemBuilder(Material.PAPER)
                        .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "drinkLore"))
                        .lore(newLore)
                        .build()
                );
                changeLore.setClickAction(e -> {
                    new LoreEditGUI(wrapper,gui,(lore) -> {
                        newLore = lore;
                        changeLore.setItemBuilder(changeLore.getItemBuilder()
                                .lore(newLore)
                        );
                        gui.update();
                    },player,newLore).open();
                });
                mainContainer.setItem(5, 1, changeLore);
            } // Lore

            {
                GuiItem changeEffects = new GuiItem(new ItemBuilder(Material.POTION)
                        .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "effectList"))
                        .lore(getEffectsAsEntries())
                        .build()
                );
                changeEffects.setClickAction(e -> {
                    new EffectsEditGUI(wrapper,gui,(effects) -> {
                        newEffects = effects;
                        changeEffects.setItemBuilder(changeEffects.getItemBuilder()
                                .lore(getEffectsAsEntries())
                        );
                        gui.update();
                    },player,newEffects).open();
                });
                mainContainer.setItem(5, 3, changeEffects);
            } // Effects

            gui.addContainer(mainContainer);
        } // Main Container

        gui.open(player);
    }

    private List<String> getEffectsAsEntries() {
        return newEffects.stream()
                .map(effect -> wrapper.getLanguageManager().getSingleString(
                        LanguageLevel.PLUGIN,
                        "effectListEntry",
                        Placeholder.unparsed("name", effect.getType()),
                        Placeholder.unparsed("amplifier", effect.getAmplifier()+""),
                        Placeholder.unparsed("duration", effect.getDuration()+"")
                ))
                .collect(Collectors.toList());
    }
}
