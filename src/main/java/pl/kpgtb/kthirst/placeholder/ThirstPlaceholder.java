package pl.kpgtb.kthirst.placeholder;

import com.github.kpgtb.ktools.manager.data.DataManager;
import com.j256.ormlite.dao.Dao;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.kpgtb.kthirst.data.DbUser;
import pl.kpgtb.kthirst.manager.user.ThirstUser;
import pl.kpgtb.kthirst.manager.user.UserManager;

import java.sql.SQLException;
import java.util.UUID;

public class ThirstPlaceholder extends PlaceholderExpansion {
    private final UserManager userManager;
    private final DataManager dataManager;

    public ThirstPlaceholder(UserManager userManager, DataManager dataManager) {
        this.userManager = userManager;
        this.dataManager = dataManager;
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
        OfflinePlayer target = player;
        if(!params.isEmpty()) {
            target = Bukkit.getOfflinePlayer(params);
        }
        UUID targetUUID = target.getUniqueId();
        ThirstUser user = userManager.getUser(targetUUID);
        double thirst = 0.0;
        if(user == null) {
            Dao<DbUser, UUID> usersDAO = dataManager.getDao(DbUser.class, UUID.class);
            try {
                if(!usersDAO.idExists(targetUUID)) {
                    return "";
                }
                thirst = usersDAO.queryForId(targetUUID).getThirst();
            } catch (SQLException e) {
                return "";
            }
        } else {
            thirst = user.getThirst();
        }

        return String.valueOf(thirst);
    }
}
