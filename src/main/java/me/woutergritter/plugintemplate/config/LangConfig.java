package me.woutergritter.plugintemplate.config;

import me.woutergritter.plugintemplate.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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

    public void sendMessage(CommandSender player, String path, Object... args) {
        player.sendMessage(getMessage(path, args));
    }
}
