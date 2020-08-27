package me.woutergritter.plugintemplate.commands.internal;

import me.woutergritter.plugintemplate.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class WCommand implements CommandExecutor {
    protected final Main plugin;
    protected final String command;

    public WCommand(Main plugin, String command) {
        this.plugin = plugin;
        this.command = command;
    }

    public abstract void execute(CommandContext ctx);

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandContext ctx = new CommandContext(plugin, this, sender, args);

        try{
            this.execute(ctx);
        }catch(CommandInterrupt interrupt) {
            sender.sendMessage(plugin.getLang().getMessage(interrupt.getPath(), interrupt.getArgs()));
        }

        return true;
    }

    public void register() {
        plugin.getCommand(this.command).setExecutor(this);
    }

    public String getCommand() {
        return command;
    }
}
