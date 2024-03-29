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
import java.util.stream.Collectors;

public class EffectsEditGUI extends KGui{
    private final ToolsObjectWrapper wrapper;
    private final KGui lastGUI;
    private final EffectsResponse response;
    private final Player player;
    private final List<DrinkEffect> defaultEffects;

    private boolean responsed;
    private boolean redirect;
    private List<DrinkEffect> newEffects;

    public EffectsEditGUI(ToolsObjectWrapper wrapper, KGui lastGUI, EffectsResponse response, Player player, List<DrinkEffect> defaultEffects) {
        super(
                wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "effectList"),
                2,
                wrapper
        );
        this.wrapper = wrapper;
        this.lastGUI = lastGUI;
        this.response = response;
        this.player = player;
        this.defaultEffects = defaultEffects;
        this.responsed = false;
        this.redirect = false;
        this.newEffects = new ArrayList<>(defaultEffects);
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
                response.response(defaultEffects);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        lastGUI.open((Player) e.getPlayer());
                    }
                }.runTaskLater(wrapper.getPlugin(), 3);
            }
        });

        PagedGuiContainer pagedGuiContainer = new PagedGuiContainer(this,0,0,9,1);
        pagedGuiContainer.fillWithItems(
                newEffects.stream().map(effect -> {
                    GuiItem item = new GuiItem(new ItemBuilder(Material.GLASS_BOTTLE)
                            .displayname(
                                    wrapper.getLanguageManager().getSingleString(
                                            LanguageLevel.PLUGIN,
                                            "effectListEntry",
                                            Placeholder.parsed("name", effect.getType()),
                                            Placeholder.parsed("amplifier", String.valueOf(effect.getAmplifier())),
                                            Placeholder.parsed("duration", String.valueOf(effect.getDuration()))
                                    )
                            )
                            .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "removeEffect"))
                    );
                    item.setClickAction((e,place) -> {
                        newEffects.remove(effect);
                        prepareGui();
                    });
                    return item;
                }).collect(Collectors.toList())
        );
        this.addContainer(pagedGuiContainer);

        GuiContainer manageContainer = new GuiContainer(this, 0,1,9,1);
        manageContainer.setItem(0,0, LeftItem.get(wrapper,pagedGuiContainer));
        manageContainer.setItem(8,0, RightItem.get(wrapper,pagedGuiContainer));

        GuiItem addEffect = new GuiItem(new ItemBuilder(Material.GLASS_BOTTLE)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "addEffect"))
        );
        addEffect.setClickAction((e,place) -> {
            redirect = true;
            new EffectCreatorGUI(wrapper,this,(effect) -> {
                if(effect == null) {
                    return;
                }
                newEffects.add(effect);
            },player).open(player);
        });
        manageContainer.setItem(3, 0,addEffect);

        GuiItem applyEffects = new GuiItem(new ItemBuilder(Material.EMERALD)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "applyEdit"))
        );
        applyEffects.setClickAction((e,place) -> {
            responsed = true;
            response.response(newEffects);
            lastGUI.open(player);
        });
        manageContainer.setItem(5,0,applyEffects);
        this.addContainer(manageContainer);
    }
}
