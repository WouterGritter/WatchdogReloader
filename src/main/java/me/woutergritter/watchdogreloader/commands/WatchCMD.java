package me.woutergritter.watchdogreloader.commands;

import me.woutergritter.watchdogreloader.Main;
import me.woutergritter.watchdogreloader.commands.internal.CommandContext;
import me.woutergritter.watchdogreloader.commands.internal.CommandInterrupt;
import me.woutergritter.watchdogreloader.commands.internal.WCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WatchCMD extends WCommand {
    public WatchCMD(Main plugin) {
        super(plugin, "watch");
    }

    @Override
    public void execute(CommandContext ctx) {
        ctx.checkNumArgs(1, "/watch <filename>");

        String pluginName = ctx.arg(0);
        if(plugin.getWatchdogManager().isPluginWatched(pluginName)) {
            throw new CommandInterrupt("already-watched", pluginName);
        }

        plugin.getWatchdogManager().setPluginWatched(pluginName, true);

        ctx.send("success", pluginName);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if(args.length == 0) {
            return Collections.emptyList();
        }

        List<String> res = new ArrayList<>();

        for(Plugin other : Bukkit.getPluginManager().getPlugins()) {
            String pluginName = other.getName();

            if(pluginName.toLowerCase().startsWith(args[0].toLowerCase()) &&
                    !plugin.getWatchdogManager().isPluginWatched(pluginName)) {
                res.add(pluginName);
            }
        }

        return res;
    }
}
