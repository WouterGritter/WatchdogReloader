package me.woutergritter.watchdogreloader.commands.internal;

import me.woutergritter.watchdogreloader.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandContext {
    private final Main plugin;
    private final WCommand command;

    private final CommandSender sender;
    private final String[] args;

    protected CommandContext(Main plugin, WCommand command, CommandSender sender, String[] args) {
        this.plugin = plugin;
        this.command = command;

        this.sender = sender;
        this.args = args;
    }

    public CommandSender sender() {
        return sender;
    }

    public String[] args() {
        return args;
    }

    public String arg(int index) {
        if(index < 0 || index >= args.length) {
            return null;
        }

        return args[index];
    }

    public boolean argEquals(int index, String... s) {
        if(index < 0 || index >= args.length) {
            return false;
        }

        for (String value : s) {
            if (args[index].equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }

    public int argsLen() {
        return args.length;
    }

    /**
     * Actual path that will be used:
     * COMMAND-cmd.PATH
     */
    public void send(String path, Object... args) {
        plugin.getLang().sendMessage(sender, command.getCommand() + "-cmd." + path, args);
    }

    /**
     * Uses the actual path that is given in the arguments.
     */
    public void sendAbsolute(String absolutePath, Object... args) {
        plugin.getLang().sendMessage(sender, absolutePath, args);
    }

    public Player checkPlayer() {
        if(!(sender instanceof Player)) {
            throw new CommandInterrupt(true, "common.need-player");
        }

        return (Player) sender;
    }

    public Player checkOnlinePlayer(int argIndex) {
        Player player = Bukkit.getPlayer(args[argIndex]);

        if(player == null) {
            throw new CommandInterrupt(true, "common.no-online-player-found", args[argIndex]);
        }

        return player;
    }

    public OfflinePlayer checkOfflinePlayer(int argIndex) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[argIndex]);

        if(!(player.hasPlayedBefore() || player.isOnline())) {
            throw new CommandInterrupt(true, "common.no-offline-player-found", args[argIndex]);
        }

        return player;
    }

    public void checkPermission(String permission) {
        if(!sender.hasPermission(permission)) {
            throw new CommandInterrupt(true, "common.no-permission");
        }
    }

    public void checkNumArgs(int minArgs, String usage) {
        if(args.length < minArgs) {
            throw new CommandInterrupt(true, "common.usage", usage);
        }
    }

    public int checkInteger(int argIndex) {
        try{
            return Integer.parseInt(args[argIndex]);
        }catch(NumberFormatException e) {
            throw new CommandInterrupt(true, "common.invalid-number", args[argIndex]);
        }
    }

    public double checkDouble(int argIndex) {
        try{
            return Double.parseDouble(args[argIndex]);
        }catch(NumberFormatException e) {
            throw new CommandInterrupt(true, "common.invalid-decimal-number", args[argIndex]);
        }
    }
}
