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

    /**
     * Actual path that will be used:
     * COMMAND-cmd.PATH
     */
    public void send(String path, Object... args) {
        sender.sendMessage(plugin.getLang().getMessage(command.getCommand() + "-cmd." + path, args));
    }

    /**
     * Uses the actual path that is given in the arguments.
     */
    public void sendAbsolute(String absolutePath, Object... args) {
        sender.sendMessage(plugin.getLang().getMessage(absolutePath, args));
    }

    public Player checkPlayer() {
        if(!(sender instanceof Player)) {
            throw new CommandInterrupt("common.need-player");
        }

        return (Player) sender;
    }
}
