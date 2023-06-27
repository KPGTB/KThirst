package pl.kpgtb.kthirst.placeholder;

import com.github.kpgtb.ktools.manager.ui.bar.BarManager;
import com.github.kpgtb.ktools.manager.ui.bar.KBar;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThirstPlaceholder extends PlaceholderExpansion {
    private final KBar thirstBar;
    private final BarManager barManager;

    public ThirstPlaceholder(KBar thirstBar, BarManager barManager) {
        this.thirstBar = thirstBar;
        this.barManager = barManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "kthirst";
    }

    @Override
    public @NotNull String getAuthor() {
        return "KPG_TB";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        Player target = null;
        if(player != null && player.isOnline()) {
            target = player.getPlayer();
        }
        if(!params.isEmpty()) {
            target = Bukkit.getPlayer(params);
        }
        if(target == null) {
            return "0.0";
        }
        return String.valueOf(barManager.getValue(thirstBar,target));
    }
}
