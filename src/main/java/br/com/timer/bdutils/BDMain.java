package br.com.timer.bdutils;

import org.bukkit.plugin.java.JavaPlugin;

public final class BDMain extends JavaPlugin {

    public static BDMain getInstance() {
        return BDMain.getPlugin(BDMain.class);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
