package me.woutergritter.watchdogreloader.watchdog;

import me.woutergritter.watchdogreloader.Main;
import me.woutergritter.watchdogreloader.config.Config;
import me.woutergritter.watchdogreloader.watchdog.reloadaction.ReloadAction;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WatchdogManager {
    private final Main plugin;
    private final File pluginsFolder;

    private final ReloadAction cfg_reloadAction;
    private final int cfg_actionDelay;

    private WatchService watcher;

    private final Config watchedPluginsConfig;

    private final Map<String, BukkitTask> delayedActions = new HashMap<>(); // <File name, Task>

    public WatchdogManager(Main plugin) {
        this.plugin = plugin;
        this.pluginsFolder = plugin.getDataFolder().getParentFile();

        cfg_reloadAction = ReloadAction.fromConfig(plugin.getConfig().getConfigurationSection("reload-action"));
        cfg_actionDelay = plugin.getConfig().getInt("action-delay");

        try {
            Path path = pluginsFolder.toPath();
            plugin.getLogger().info("Watching " + pluginsFolder.getAbsolutePath());
            this.watcher = FileSystems.getDefault().newWatchService();

            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        watchedPluginsConfig = new Config(plugin, "watched-plugins.yml");

        Bukkit.getScheduler().runTaskTimer(plugin, this::poll, 10, 10);
    }

    private void poll() {
        WatchKey key = watcher.poll();
        if(key == null) {
            return;
        }

        for(WatchEvent<?> _event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = _event.kind();

            if(kind == StandardWatchEventKinds.OVERFLOW) {
                continue;
            }

            WatchEvent<Path> event = (WatchEvent<Path>) _event;
            String filename = event.context().toString();

            if(filename.endsWith(".jar")) {
                onJarFileChange(new File(pluginsFolder, filename));
            }
        }

        key.reset();
    }

    private void onJarFileChange(File file) {
        final String filename = file.getName();

        BukkitTask currentTask = delayedActions.get(filename);
        if(currentTask != null) {
            currentTask.cancel();
            delayedActions.remove(filename);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Plugin other = getPlugin(filename);

            if(other == null) {
                plugin.broadcast("watchdog.non-plugin-file-changed", filename);
                return;
            }

            String pluginName = other.getName();

            if(isPluginWatched(pluginName)) {
                plugin.broadcast("watchdog.watched-plugin-changed", filename, other.getName());

                // Execute the reload action.
                cfg_reloadAction.accept(other, file);

                plugin.broadcast("watchdog.reload-action-success");
            }else {
                plugin.broadcast("watchdog.unwatched-plugin-changed", filename, other.getName());
            }
        }, cfg_actionDelay);
        delayedActions.put(filename, task);
    }

    private Plugin getPlugin(String filename) {
        for(Plugin other : Bukkit.getPluginManager().getPlugins()) {
            if(other == null) {
                continue;
            }

            String pluginFilename = new java.io.File(other.getClass().getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();

            if(pluginFilename.equals(filename)) {
                return other;
            }
        }

        return null;
    }

    public boolean isPluginWatched(String pluginName) {
        return getWatchedPlugins().contains(pluginName);
    }

    public void setPluginWatched(String pluginName, boolean isWatched) {
        List<String> watched = watchedPluginsConfig.getStringList("watched");

        if(isWatched && !watched.contains(pluginName)) {
            watched.add(pluginName);
        }else if(!isWatched) {
            watched.remove(pluginName);
        }

        watchedPluginsConfig.set("watched", watched);
        watchedPluginsConfig.save();
    }

    public List<String> getWatchedPlugins() {
        return Collections.unmodifiableList(watchedPluginsConfig.getStringList("watched"));
    }
}
