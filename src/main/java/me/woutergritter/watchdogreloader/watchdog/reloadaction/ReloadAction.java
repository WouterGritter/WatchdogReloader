package me.woutergritter.watchdogreloader.watchdog.reloadaction;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.function.BiConsumer;

public interface ReloadAction extends BiConsumer<Plugin, File> {
    static ReloadAction fromConfig(ConfigurationSection conf) {
        switch(conf.getString("action").toUpperCase()) {
            case "RELOAD_SERVER":
                return (plugin, file) -> Bukkit.reload();
            case "RELOAD_PLUGIN":
                return new ReloadPluginAction();
            case "EXECUTE_COMMAND":
                String command = conf.getString("command");
                return (plugin, file) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command, plugin.getName()));
        }

        return null;
    }
}
