package me.woutergritter.watchdogreloader.watchdog;

import me.woutergritter.watchdogreloader.Main;
import me.woutergritter.watchdogreloader.config.Config;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WatchdogManager {
    private final Main plugin;

    private File pluginsFolder;
    private WatchService watcher;

    private Config watchedConfig;

    private List<String> changedFiles = new ArrayList<>();

    public WatchdogManager(Main plugin) {
        this.plugin = plugin;
        this.pluginsFolder = plugin.getDataFolder().getParentFile();

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
        if(!changedFiles.contains(filename)) {
            changedFiles.add(filename);

            if(!isFileWatched(filename)) {
                plugin.broadcast(plugin.getLang().getMessage("watchdog.unwatched-file-changed", filename));
            }
        }

        if(isFileWatched(filename)) {
            // Reload the server!
            plugin.broadcast(plugin.getLang().getMessage("watchdog.watched-file-changed", filename));

            Bukkit.reload();
        }
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
