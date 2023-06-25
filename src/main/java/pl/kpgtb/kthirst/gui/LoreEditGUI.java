package pl.kpgtb.kthirst.gui;

import com.github.kpgtb.ktools.manager.gui.KGui;
import com.github.kpgtb.ktools.manager.gui.KWriteGui;
import com.github.kpgtb.ktools.manager.gui.container.GuiContainer;
import com.github.kpgtb.ktools.manager.gui.container.PagedGuiContainer;
import com.github.kpgtb.ktools.manager.gui.item.GuiItem;
import com.github.kpgtb.ktools.manager.gui.item.common.LeftItem;
import com.github.kpgtb.ktools.manager.gui.item.common.RightItem;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.item.ItemBuilder;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.kpgtb.kthirst.gui.response.LoreResponse;

import java.util.ArrayList;
import java.util.List;

public class LoreEditGUI extends KGui{
    private final ToolsObjectWrapper wrapper;
    private final KGui lastGUI;
    private final LoreResponse response;
    private final Player player;
    private final List<String> defaultLore;

    private boolean responsed;
    private boolean redirect;
    private List<String> newLore;
    private List<GuiContainer> pages;

    public LoreEditGUI(ToolsObjectWrapper wrapper, KGui lastGUI, LoreResponse response, Player player, List<String> defaultLore) {
        super(
                wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "drinkLore"),
                2,
                wrapper
        );
        this.wrapper = wrapper;
        this.lastGUI = lastGUI;
        this.response = response;
        this.player = player;
        this.defaultLore = defaultLore;
        this.responsed = false;
        this.redirect = false;
        this.newLore = new ArrayList<>(defaultLore);
        this.pages = new ArrayList<>();
    }

    @Override
    public void prepareGui() {
        this.blockClick();
        resetContainers();

        this.setCloseAction(e -> {
            if(redirect) {
                redirect = false;
                return;
            }

            if(!responsed) {
                response.response(defaultLore);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        lastGUI.open((Player) e.getPlayer());
                    }
                }.runTaskLater(wrapper.getPlugin(), 3);
            }
        });

        PagedGuiContainer pagedGuiContainer = new PagedGuiContainer(this,0,0,9,1);

        generateLoreItems(this,pagedGuiContainer);
        this.addContainer(pagedGuiContainer);

        GuiContainer manageContainer = new GuiContainer(this, 0,1,9,1);
        manageContainer.setItem(0,0, LeftItem.get(wrapper,pagedGuiContainer));
        manageContainer.setItem(8,0, RightItem.get(wrapper,pagedGuiContainer));

        GuiItem addLoreLine = new GuiItem(new ItemBuilder(Material.PAPER)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "addLoreLine"))
        );
        addLoreLine.setClickAction((e,place) -> {
            redirect = true;
            new KWriteGui(wrapper,this,player,(text) -> {
                if(text.isEmpty()) {
                    return;
                }
                newLore.add(wrapper.getLanguageManager().convertMmToString(text));
                generateLoreItems(this,pagedGuiContainer);
            }).open();
        });
        manageContainer.setItem(3, 0,addLoreLine);

        GuiItem applyLore = new GuiItem(new ItemBuilder(Material.EMERALD)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "applyEdit"))
        );
        applyLore.setClickAction((e,place) -> {
            responsed = true;
            response.response(newLore);
            lastGUI.open(player);
        });
        manageContainer.setItem(5,0,applyLore);
        this.addContainer(manageContainer);
    }

    private void generateLoreItems(KGui gui, PagedGuiContainer pagedGuiContainer) {
        pagedGuiContainer.clearPages();
        pages.clear();
        pages.add(new GuiContainer(pagedGuiContainer));

        int x = 0;
        for(String loreLine : newLore) {
            if(x >= 9) {
                x = 0;
                pages.add(new GuiContainer(pagedGuiContainer));
            }
            GuiContainer page = pages.get(pages.size() - 1);
            GuiItem item = new GuiItem(new ItemBuilder(Material.PAPER)
                    .displayname(loreLine)
                    .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "removeLoreLine"))
            );
            item.setClickAction((e,place) -> {
                newLore.remove(loreLine);
                generateLoreItems(gui,pagedGuiContainer);
            });
            page.setItem(x,0,item);
            x++;
        }
        pages.forEach(pagedGuiContainer::addPage);
        gui.update();
    }
}
