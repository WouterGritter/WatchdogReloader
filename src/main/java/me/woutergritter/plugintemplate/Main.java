package me.woutergritter.plugintemplate;

import me.woutergritter.plugintemplate.commands.ExampleCMD;
import me.woutergritter.plugintemplate.config.Config;
import me.woutergritter.plugintemplate.config.LangConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    // -- Global configuration files -- //
    private Config config;
    private LangConfig langConfig;
    // -- //

    @Override
    public void onEnable() {
        // Load global configs
        config = new Config(this, "config.yml");
        langConfig = new LangConfig(this, "lang.yml");

        // Register commands
        new ExampleCMD(this).register();
    }

    @Override
    public void onDisable() {
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
