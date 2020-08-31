package me.woutergritter.plugintemplate.commands.internal;

import me.woutergritter.plugintemplate.Main;
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

        for(int i = 0; i < s.length; i++) {
            if(args[index].equalsIgnoreCase(s[i])) {
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
            throw new CommandInterrupt("common.need-player");
        }

        return (Player) sender;
    }

    public void checkPermission(String permission) {
        if(!sender.hasPermission(permission)) {
            throw new CommandInterrupt("common.no-permission");
        }
    }

    public void checkNumArgs(int minArgs, String usage) {
        if(args.length < minArgs) {
            throw new CommandInterrupt("common.usage", usage);
        }
    }
}
