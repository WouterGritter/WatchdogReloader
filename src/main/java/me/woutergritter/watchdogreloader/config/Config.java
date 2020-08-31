package me.woutergritter.watchdogreloader.config;

import me.woutergritter.watchdogreloader.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Config extends YamlConfiguration {
    private final Main plugin;
    private final File file;

    public Config(Main plugin, String name) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), name);

        saveDefault();
        reload();
    }

    public boolean hasDefaults() {
        return plugin.getResource(file.getName()) != null;
    }

    public void reload() {
        if(file.exists()) {
            try {
                load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }

        if(hasDefaults()) {
            InputStreamReader defConfigStream = new InputStreamReader(Objects.requireNonNull(plugin.getResource(file.getName())));
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

            setDefaults(defConfig);
        }
    }

    public void save() {
        try {
            file.getParentFile().mkdirs();

            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefault() {
        if(!file.exists()) {
            file.getParentFile().mkdirs();

            if(hasDefaults()) {
                plugin.saveResource(file.getName(), false);
            }else{
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getName() {
        return file.getName();
    }

    public File getFile() {
        return file;
    }
}
