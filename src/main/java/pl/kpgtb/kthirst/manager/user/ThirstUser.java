package pl.kpgtb.kthirst.manager.user;

import com.github.kpgtb.ktools.manager.data.DataManager;
import com.github.kpgtb.ktools.manager.language.LanguageManager;
import com.github.kpgtb.ktools.manager.ui.Alignment;
import com.github.kpgtb.ktools.manager.ui.BaseUiObject;
import com.github.kpgtb.ktools.manager.ui.UiManager;
import com.github.kpgtb.ktools.util.ui.NoShadow;
import com.j256.ormlite.dao.Dao;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.kpgtb.kthirst.data.DbUser;

import java.sql.SQLException;
import java.util.UUID;

public class ThirstUser {
    private final UUID uuid;
    private double thirst;
    private final double maxThirst;

    private final DataManager dataManager;

    private boolean damaging;

    public ThirstUser(UUID uuid, double thirst, double maxThirst, DataManager dataManager) {
        this.uuid = uuid;
        this.thirst = thirst;
        this.maxThirst = maxThirst;

        this.dataManager = dataManager;
        this.damaging = false;
    }

    public void save() throws SQLException {
        Dao<DbUser, UUID> usersDAO = dataManager.getDao(DbUser.class, UUID.class);
        DbUser dbUser = usersDAO.queryForId(uuid);
        dbUser.setThirst(thirst);
        usersDAO.update(dbUser);
    }

    public void setThirst(double thirst) {
        if(thirst > maxThirst) {
            this.thirst = maxThirst;
        } else {
            this.thirst = thirst;
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getThirst() {
        return thirst;
    }

    public boolean isDamaging() {
        return damaging;
    }

    public double getMaxThirst() {
        return maxThirst;
    }

    public void setDamaging(boolean damaging) {
        this.damaging = damaging;
    }

}

