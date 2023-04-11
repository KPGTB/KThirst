package pl.kpgtb.kthirst.gui;

import com.github.kpgtb.ktools.manager.gui.KGui;
import com.github.kpgtb.ktools.manager.gui.container.GuiContainer;
import com.github.kpgtb.ktools.manager.gui.container.PagedGuiContainer;
import com.github.kpgtb.ktools.manager.gui.item.GuiItem;
import com.github.kpgtb.ktools.manager.gui.item.common.LeftItem;
import com.github.kpgtb.ktools.manager.gui.item.common.RightItem;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.item.ItemBuilder;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.kpgtb.kthirst.data.type.DrinkEffect;
import pl.kpgtb.kthirst.gui.response.EffectsResponse;

import java.util.ArrayList;
import java.util.List;

public class EffectsEditGUI {
    private final ToolsObjectWrapper wrapper;
    private final KGui lastGUI;
    private final EffectsResponse response;
    private final Player player;
    private final List<DrinkEffect> defaultEffects;

    private boolean responsed;
    private boolean redirect;
    private List<DrinkEffect> newEffects;
    private List<GuiContainer> pages;

    public EffectsEditGUI(ToolsObjectWrapper wrapper, KGui lastGUI, EffectsResponse response, Player player, List<DrinkEffect> defaultEffects) {
        this.wrapper = wrapper;
        this.lastGUI = lastGUI;
        this.response = response;
        this.player = player;
        this.defaultEffects = defaultEffects;
        this.responsed = false;
        this.redirect = false;
        this.newEffects = new ArrayList<>(defaultEffects);
        this.pages = new ArrayList<>();
    }

    public void open() {
        KGui gui = new KGui(
                wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "effectList"),
                2,
                wrapper
        );

        gui.blockClick();

        gui.setCloseAction(e -> {
            if(redirect) {
                redirect = false;
                return;
            }

            if(!responsed) {
                response.response(defaultEffects);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        lastGUI.open((Player) e.getPlayer());
                    }
                }.runTaskLater(wrapper.getPlugin(), 3);
            }
        });

        PagedGuiContainer pagedGuiContainer = new PagedGuiContainer(gui,0,0,9,1);

        generateEffectsItems(gui,pagedGuiContainer);
        gui.addContainer(pagedGuiContainer);

        GuiContainer manageContainer = new GuiContainer(gui, 0,1,9,1);
        manageContainer.setItem(0,0, LeftItem.get(wrapper,pagedGuiContainer));
        manageContainer.setItem(8,0, RightItem.get(wrapper,pagedGuiContainer));

        GuiItem addEffect = new GuiItem(new ItemBuilder(Material.GLASS_BOTTLE)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "addEffect"))
        );
        addEffect.setClickAction(e -> {
            redirect = true;
            new EffectCreatorGUI(wrapper,gui,(effect) -> {
                if(effect == null) {
                    return;
                }
                newEffects.add(effect);
                generateEffectsItems(gui,pagedGuiContainer);
            },player).open();
        });
        manageContainer.setItem(3, 0,addEffect);

        GuiItem applyEffects = new GuiItem(new ItemBuilder(Material.EMERALD)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "applyEdit"))
        );
        applyEffects.setClickAction(e -> {
            responsed = true;
            response.response(newEffects);
            lastGUI.open(player);
        });
        manageContainer.setItem(5,0,applyEffects);
        gui.addContainer(manageContainer);

        gui.open(player);
    }

    private void generateEffectsItems(KGui gui, PagedGuiContainer pagedGuiContainer) {
        pagedGuiContainer.clearPages();
        pages.clear();
        pages.add(new GuiContainer(pagedGuiContainer));

        int x = 0;
        for(DrinkEffect effect : newEffects) {
            if(x >= 9) {
                x = 0;
                pages.add(new GuiContainer(pagedGuiContainer));
            }
            GuiContainer page = pages.get(pages.size() - 1);
            GuiItem item = new GuiItem(new ItemBuilder(Material.GLASS_BOTTLE)
                    .displayname(
                            wrapper.getLanguageManager().getSingleString(
                                    LanguageLevel.PLUGIN,
                                    "effectListEntry",
                                    Placeholder.parsed("name", effect.getType()),
                                    Placeholder.parsed("amplifier", effect.getAmplifier() + ""),
                                    Placeholder.parsed("duration", effect.getDuration() + "")
                            )
                    )
                    .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "removeEffect"))
            );
            item.setClickAction(e -> {
                newEffects.remove(effect);
                generateEffectsItems(gui,pagedGuiContainer);
            });
            page.setItem(x,0,item);
            x++;
        }
        pages.forEach(pagedGuiContainer::addPage);
        gui.update();
    }
}
