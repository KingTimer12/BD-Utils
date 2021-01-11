package br.com.timer.bdutils;

import br.com.timer.bdutils.models.Params;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class BDMain extends JavaPlugin {

    public static BDMain getInstance() {
        return BDMain.getPlugin(BDMain.class);
    }

    private ConnectionManager manager;

    @Override
    public void onEnable() {
        manager = new ConnectionManager();

        manager.setup(ConnectionManager.DBType.MARIADB, "Account"
                , "UUID varchar(32)", "Nickname varchar(16)", "Money int", "Kills int");

        //?
        //MongoDB example
        manager.getConnectionBase().delete(new Params("UUID", "123042i049242"));
        
        manager.getConnectionBase().async().insert(Arrays.asList(
                new Params("UUID", "123042i049242"),
                new Params("Nickname", "KingoZ_"),
                new Params("Money", "0"),
                new Params("Kills", "0")), Exception::new);
        
        manager.getConnectionBase().insert(Arrays.asList(
                new Params("UUID", "123042i049242"),
                new Params("Nickname", "KingoZ_"),
                new Params("Money", "0"),
                new Params("Kills", "0")));
        
        manager.getConnectionBase().select(Arrays.asList(new Params("", "")));
    }

    @Override
    public void onDisable() {
        manager.getConnectionBase().closeConnection();
    }
}
