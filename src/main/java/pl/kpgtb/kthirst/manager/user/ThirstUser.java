package pl.kpgtb.kthirst.manager.user;

import com.github.kpgtb.ktools.manager.data.DataManager;
import com.github.kpgtb.ktools.manager.language.LanguageManager;
import com.github.kpgtb.ktools.manager.ui.Alignment;
import com.github.kpgtb.ktools.manager.ui.BaseUiObject;
import com.github.kpgtb.ktools.manager.ui.UiManager;
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
    private final UiManager uiManager;
    private final LanguageManager languageManager;
    private final BaseUiObject baseUI;

    private boolean damaging;
    private boolean inWater;

    public ThirstUser(UUID uuid, double thirst, double maxThirst, DataManager dataManager, UiManager uiManager, LanguageManager languageManager, int uiOffset) {
        this.uuid = uuid;
        this.thirst = thirst;
        this.maxThirst = maxThirst;

        this.dataManager = dataManager;
        this.uiManager = uiManager;
        this.languageManager = languageManager;
        this.damaging = false;
        this.inWater = false;

        baseUI = new BaseUiObject("", Alignment.LEFT, uiOffset);

        setupUI();
    }

    public void setupUI() {
        // 1 icon = 2 points
        // icons = 10 = 20 points

        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        if(!op.isOnline() || op.getPlayer() == null) {
            return;
        }
        Player player = op.getPlayer();
        GameMode gameMode = player.getGameMode();

        if(gameMode.equals(GameMode.CREATIVE) || gameMode.equals(GameMode.SPECTATOR)) {
            baseUI.update("");
            return;
        }

        double fullIcon = maxThirst / 10.0;
        int fullIconsInUI = (int) Math.floor(thirst / fullIcon);
        boolean hasHalfIconInUI = thirst % fullIcon > 0;

        int emptyIconsInUI = 10 - fullIconsInUI;
        if(hasHalfIconInUI) {
            emptyIconsInUI -= 1;
        }

        String fullIconChar = inWater ? "\uA004\uF802" : "\uA001\uF802";
        String halfIconChar = inWater ? "\uA005\uF802" : "\uA002\uF802";
        String emptyIconChar = inWater ? "\uA006\uF802" : "\uA003\uF802";

        StringBuilder ui = new StringBuilder();
        for(int i = 0; i < emptyIconsInUI; i++) {
            ui.append(emptyIconChar);
        }

        if(hasHalfIconInUI) {
            ui.append(halfIconChar);
        }

        for(int i = 0; i < fullIconsInUI; i++) {
            ui.append(fullIconChar);
        }

        boolean fixShadow = newerVersionThan(19,1);

        if(fixShadow) {
            baseUI.update(languageManager.convertMmToString("<color:#4e5c24>" + ui.toString()));
        } else {
            baseUI.update(ui.toString());
        }
    }

    private boolean newerVersionThan(int minor, int patch) {
        String[] version = Bukkit.getBukkitVersion()
                        .split("-")[0]
                        .split("\\.");
        int mcMinor = Integer.parseInt(version[1]);
        int mcPatch = Integer.parseInt(version[2]);

        if(mcMinor > minor) {
            return true;
        }
        if(mcMinor < minor) {
            return false;
        }

        return mcPatch >= patch;
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
        setupUI();
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getThirst() {
        return thirst;
    }

    public BaseUiObject getBaseUI() {
        return baseUI;
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

    public boolean isInWater() {
        return inWater;
    }

    public void setInWater(boolean inWater) {
        this.inWater = inWater;
    }

}

