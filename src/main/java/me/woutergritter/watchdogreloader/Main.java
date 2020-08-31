package me.woutergritter.watchdogreloader;

import me.woutergritter.watchdogreloader.commands.UnwatchCMD;
import me.woutergritter.watchdogreloader.commands.WatchCMD;
import me.woutergritter.watchdogreloader.config.Config;
import me.woutergritter.watchdogreloader.config.LangConfig;
import me.woutergritter.watchdogreloader.watchdog.WatchdogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    // -- Global configuration files -- //
    private Config config;
    private LangConfig langConfig;

    // -- Managers -- //
    private WatchdogManager watchdogManager;

    @Override
    public void onEnable() {
        // Load global configs
        config = new Config(this, "config.yml");
        langConfig = new LangConfig(this, "lang.yml");

        // Managers
        watchdogManager = new WatchdogManager(this);

        // Commands
        new WatchCMD(this).register();
        new UnwatchCMD(this).register();
    }

    @Override
    public void onDisable() {
    }

    public void broadcast(String msg) {
        getLogger().info(msg);

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(msg);
        });
    }

    public WatchdogManager getWatchdogManager() {
        return watchdogManager;
    }

    public LangConfig getLang() {
        return langConfig;
    }

    // -- Override config methods to use our own implementation -- //
    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void reloadConfig() {
        config.reload();
    }

    @Override
    public void saveConfig() {
        config.save();
    }

    @Override
    public void saveDefaultConfig() {
        config.saveDefault();
    }
    // -- //
}
