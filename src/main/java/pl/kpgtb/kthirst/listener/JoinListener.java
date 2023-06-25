package pl.kpgtb.kthirst.listener;

import com.github.kpgtb.ktools.manager.listener.KListener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import com.j256.ormlite.dao.Dao;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.kpgtb.kthirst.data.DbUser;
import pl.kpgtb.kthirst.manager.user.ThirstUser;
import pl.kpgtb.kthirst.util.ThirstWrapper;

import java.sql.SQLException;
import java.util.UUID;

public class JoinListener extends KListener {
    private final ThirstWrapper wrapper;

    public JoinListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = (ThirstWrapper) toolsObjectWrapper;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(wrapper.getUserManager().hasUser(uuid)) {
            wrapper.getUserManager().removeUser(uuid);
        }

        double maxThirst = 20.0;
        Dao<DbUser, UUID> usersDAO = wrapper.getDataManager().getDao(DbUser.class, UUID.class);
        if(!usersDAO.idExists(uuid)) {
            DbUser dbUser = new DbUser(uuid,maxThirst);
            usersDAO.create(dbUser);
        }

        double playerThirst = (double) usersDAO.queryForId(uuid).getThirst();
        ThirstUser user = new ThirstUser(uuid, playerThirst, maxThirst, wrapper.getDataManager());
        wrapper.getUserManager().addUser(uuid, user);
        wrapper.getBarManager().updateBars(player);
    }
}
