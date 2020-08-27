package me.woutergritter.plugintemplate.config;

import me.woutergritter.plugintemplate.Main;
import org.bukkit.ChatColor;

import java.util.Locale;

public class LangConfig extends Config {
    public LangConfig(Main plugin, String name) {
        super(plugin, name);
    }

    public String getMessage(String path, Object... args) {
        String msg = getString(path, path); // Default message = path
        msg = ChatColor.translateAlternateColorCodes('&', msg);

        return String.format(Locale.ENGLISH, msg, args);
    }
}
