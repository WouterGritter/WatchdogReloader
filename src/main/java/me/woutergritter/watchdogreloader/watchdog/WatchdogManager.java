package me.woutergritter.watchdogreloader.watchdog;

import me.woutergritter.watchdogreloader.Main;
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

    private List<String> watchedFiles = new ArrayList<>();
    private File watchedFilesFile;

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

        watchedFilesFile = new File(plugin.getDataFolder(), "watched-files.dat");
        loadWatchedFilesFile();

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

            if(!watchedFiles.contains(filename)) {
                plugin.broadcast("------------");
                plugin.broadcast("A plugin file changed:");
                plugin.broadcast(filename);
                plugin.broadcast("If you would like to reload the server");
                plugin.broadcast("every time that jar file changes, type");
                plugin.broadcast("the command /watch " + filename);
                plugin.broadcast("------------");
            }
        }

        if(watchedFiles.contains(filename)) {
            // Reload the server!
            plugin.broadcast("A plugin file that is watched has changed! (" + filename + ")");
            plugin.broadcast("Reloading the server.");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload");
        }
    }

    public boolean isFileWatched(String filename) {
        return watchedFiles.contains(filename);
    }

    public void setFileWatched(String filename, boolean isWatched) {
        if(isWatched && !watchedFiles.contains(filename)) {
            watchedFiles.add(filename);
        }else if(!isWatched && watchedFiles.contains(filename)) {
            watchedFiles.remove(filename);
        }

        saveWatchedFilesFile();
    }

    public List<String> getChangedFiles() {
        return Collections.unmodifiableList(changedFiles);
    }

    public List<String> getWatchedFiles() {
        return Collections.unmodifiableList(watchedFiles);
    }

    private void loadWatchedFilesFile() {
        if(!watchedFilesFile.exists()) {
            saveWatchedFilesFile();
        }

        try{
            DataInputStream dis = new DataInputStream(new FileInputStream(watchedFilesFile));

            int watchedFilesSize = dis.readInt();
            watchedFiles.clear();
            for(int i = 0; i < watchedFilesSize; i++) {
                String watchedFile = dis.readUTF();
                watchedFiles.add(watchedFile);
            }

            dis.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void saveWatchedFilesFile() {
        try{
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(watchedFilesFile));

            dos.writeInt(watchedFiles.size());
            for(String watchedFile : watchedFiles) {
                dos.writeUTF(watchedFile);
            }

            dos.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
