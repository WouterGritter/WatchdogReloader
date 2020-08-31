package me.woutergritter.plugintemplate.config;

import me.woutergritter.plugintemplate.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;

public class LangConfig extends Config {
    public LangConfig(Main plugin, String name) {
        super(plugin, name);
    }

    public String getMessage(String path, Object... args) {
        String message;
        if(!isSet(path) && (getDefaults() == null || !getDefaults().isSet(path))) {
            message = path;
            if(args.length > 0) {
                message += " [" + StringUtils.join(args, ", ") + "]";
            }
        }else if(isList(path)) {
            List<String> messages = getStringList(path);
            message = StringUtils.join(messages.iterator(), '\n');
        }else{
            message = getString(path);
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        return String.format(Locale.ENGLISH, message, args);
    }

    public void sendMessage(CommandSender player, String path, Object... args) {
        String message = getMessage(path, args);
        if(!message.isEmpty()) {
            player.sendMessage(StringUtils.split(message, '\n'));
        }
    }
}
