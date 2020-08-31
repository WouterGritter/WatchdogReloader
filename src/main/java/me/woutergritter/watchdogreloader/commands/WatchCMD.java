package me.woutergritter.watchdogreloader.commands;

import me.woutergritter.watchdogreloader.Main;
import me.woutergritter.watchdogreloader.commands.internal.CommandContext;
import me.woutergritter.watchdogreloader.commands.internal.CommandInterrupt;
import me.woutergritter.watchdogreloader.commands.internal.WCommand;
import org.bukkit.command.CommandSender;

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

        String filename = ctx.arg(0);
        if(plugin.getWatchdogManager().isFileWatched(filename)) {
            throw new CommandInterrupt("already-watched", filename);
        }

        plugin.getWatchdogManager().setFileWatched(filename, true);

        ctx.send("success", filename);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if(args.length == 0) {
            return Collections.emptyList();
        }

        List<String> res = new ArrayList<>();

        plugin.getWatchdogManager().getChangedFiles().forEach(filename -> {
            if(plugin.getWatchdogManager().isFileWatched(filename)) {
                // The file is already being watched..
                return;
            }

            if(filename.toLowerCase().startsWith(args[0].toLowerCase())) {
                res.add(filename);
            }
        });

        return res;
    }
}
