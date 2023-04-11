package pl.kpgtb.kthirst.gui;

import com.github.kpgtb.ktools.manager.gui.KCountGui;
import com.github.kpgtb.ktools.manager.gui.KGui;
import com.github.kpgtb.ktools.manager.gui.KWriteGui;
import com.github.kpgtb.ktools.manager.gui.container.GuiContainer;
import com.github.kpgtb.ktools.manager.gui.item.GuiItem;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.item.ItemBuilder;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.kpgtb.kthirst.data.type.DrinkEffect;
import pl.kpgtb.kthirst.gui.response.EffectResponse;

public class EffectCreatorGUI {
    private final ToolsObjectWrapper wrapper;
    private final KGui lastGUI;
    private final EffectResponse response;
    private final Player player;

    private boolean responsed;
    private boolean redirect;
    private DrinkEffect effect;

    public EffectCreatorGUI(ToolsObjectWrapper wrapper, KGui lastGUI, EffectResponse response, Player player) {
        this.wrapper = wrapper;
        this.lastGUI = lastGUI;
        this.response = response;
        this.player = player;
        this.responsed = false;
        this.redirect = false;
        this.effect = new DrinkEffect("",0,0);
    }

    public void open() {
        KGui gui = new KGui(
                wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "effectList"),
                1,
                wrapper
        );

        gui.blockClick();

        gui.setCloseAction(e -> {
            if(redirect) {
                redirect = false;
                return;
            }

            if(!responsed) {
                response.response(null);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        lastGUI.open((Player) e.getPlayer());
                    }
                }.runTaskLater(wrapper.getPlugin(), 3);
            }
        });

        GuiContainer guiContainer = new GuiContainer(gui,0,0,9,1);

        GuiItem setType = new GuiItem(new ItemBuilder(Material.GLASS_BOTTLE)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "setType"))
                .lore(wrapper.getLanguageManager().convertMmToString("<white><b>" + effect.getType()))
        );
        setType.setClickAction(e -> {
            redirect = true;
            new KWriteGui(wrapper,gui,player,text -> {
               String type = text.toUpperCase();
               if(PotionEffectType.getByName(type) == null) {
                   type = "WRONG";
               }
               effect.setType(type);
               setType.setItemBuilder(setType.getItemBuilder().lore(wrapper.getLanguageManager().convertMmToString("<white><b>" + effect.getType()),0));
               gui.update();
            }).open();
        });
        guiContainer.setItem(3,0,setType);

        GuiItem setAmplifier = new GuiItem(new ItemBuilder(Material.GLOWSTONE_DUST)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "setAmplifier"))
                .lore(wrapper.getLanguageManager().convertMmToString("<white><b>" + effect.getAmplifier()))
        );
        setAmplifier.setClickAction(e -> {
            redirect = true;
            new KCountGui(wrapper,gui,value -> {
                effect.setAmplifier((int) value);
                setAmplifier.setItemBuilder(setAmplifier.getItemBuilder().lore(wrapper.getLanguageManager().convertMmToString("<white><b>" + effect.getAmplifier()),0));
                gui.update();
            }, player,effect.getAmplifier(),0,254,false,Material.GLOWSTONE_DUST).open();
        });
        guiContainer.setItem(4,0,setAmplifier);

        GuiItem setDuration = new GuiItem(new ItemBuilder(Material.CLOCK)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "setDuration"))
                .lore(wrapper.getLanguageManager().convertMmToString("<white><b>" + effect.getDuration()))
        );
        setDuration.setClickAction(e -> {
            redirect = true;
            new KCountGui(wrapper,gui,value -> {
                effect.setDuration((int) value);
                setDuration.setItemBuilder(setDuration.getItemBuilder().lore(wrapper.getLanguageManager().convertMmToString("<white><b>" + effect.getDuration()),0));
                gui.update();
            }, player,effect.getDuration(),0,10000000,false,Material.CLOCK).open();
        });
        guiContainer.setItem(5,0,setDuration);

        GuiItem applyEffect = new GuiItem(new ItemBuilder(Material.EMERALD)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "applyEdit"))
        );
        applyEffect.setClickAction(e -> {
            responsed = true;
            response.response(effect.getType().isEmpty() || effect.getType().equalsIgnoreCase("WRONG") ? null : effect);
            lastGUI.open(player);
        });
        guiContainer.setItem(8,0,applyEffect);

        gui.addContainer(guiContainer);

        gui.open(player);
    }
}
