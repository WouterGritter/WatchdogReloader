package me.woutergritter.watchdogreloader.commands;

import me.woutergritter.watchdogreloader.Main;
import me.woutergritter.watchdogreloader.commands.internal.CommandContext;
import me.woutergritter.watchdogreloader.commands.internal.CommandInterrupt;
import me.woutergritter.watchdogreloader.commands.internal.WCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnwatchCMD extends WCommand {
    public UnwatchCMD(Main plugin) {
        super(plugin, "unwatch");
    }

    @Override
    public void execute(CommandContext ctx) {
        ctx.checkNumArgs(1, "/unwatch <filename>");

        String filename = ctx.arg(0);
        if(!plugin.getWatchdogManager().isFileWatched(filename)) {
            throw new CommandInterrupt("not-watched", filename);
        }

        plugin.getWatchdogManager().setFileWatched(filename, false);

        ctx.send("success", filename);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if(args.length == 0) {
            return Collections.emptyList();
        }

        List<String> res = new ArrayList<>();

        plugin.getWatchdogManager().getWatchedFiles().forEach(filename -> {
            if(filename.toLowerCase().startsWith(args[0].toLowerCase())) {
                res.add(filename);
            }
        });

        return res;
    }
}
