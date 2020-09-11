package me.woutergritter.watchdogreloader.watchdog;

import me.woutergritter.watchdogreloader.Main;
import me.woutergritter.watchdogreloader.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WatchdogManager {
    private final Main plugin;
    private final File pluginsFolder;

    private String cfg_reloadCommand;

    private WatchService watcher;

    private Config watchedConfig;

    private List<String> changedFiles = new ArrayList<>();

    public WatchdogManager(Main plugin) {
        this.plugin = plugin;
        this.pluginsFolder = plugin.getDataFolder().getParentFile();

        cfg_reloadCommand = plugin.getConfig().getString("reload-command");

        try {
            Path path = pluginsFolder.toPath();
            plugin.getLogger().info("Watching " + pluginsFolder.getAbsolutePath());
            this.watcher = FileSystems.getDefault().newWatchService();

            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        watchedConfig = new Config(plugin, "watched-files.yml");

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
        String filename = file.getName();
        Plugin other = getPlugin(filename);

        if(!changedFiles.contains(filename)) {
            changedFiles.add(filename);

            if(other == null) {
                plugin.broadcast("watchdog.non-plugin-file-changed", filename);
            }else if(!isFileWatched(filename)) {
                plugin.broadcast("watchdog.unwatched-file-changed", filename, plugin.getName());
            }
        }

        if(other == null) {
            return;
        }

        if(isFileWatched(filename)) {
            // Execute the reload command.
            String cmd = String.format(cfg_reloadCommand, plugin.getName());
            plugin.broadcast("watchdog.watched-file-changed", filename, plugin.getName(), cmd);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
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

    public boolean isFileWatched(String filename) {
        return getWatchedFiles().contains(filename);
    }

    public void setFileWatched(String filename, boolean isWatched) {
        List<String> watched = watchedConfig.getStringList("watched");

        if(isWatched && !watched.contains(filename)) {
            watched.add(filename);
        }else if(!isWatched) {
            watched.remove(filename);
        }

        watchedConfig.set("watched", watched);
        watchedConfig.save();
    }

    public List<String> getWatchedFiles() {
        return Collections.unmodifiableList(watchedConfig.getStringList("watched"));
    }

    public List<String> getChangedFiles() {
        return Collections.unmodifiableList(changedFiles);
    }
}
