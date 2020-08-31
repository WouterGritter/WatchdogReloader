package me.woutergritter.plugintemplate.commands.internal;

import me.woutergritter.plugintemplate.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;

public abstract class WCommand extends Command {
    protected final Main plugin;
    protected final String command;

    public WCommand(Main plugin, String command) {
        super(command);
        this.plugin = plugin;
        this.command = command;
    }

    public abstract void execute(CommandContext ctx);

    @Override
    public final boolean execute(CommandSender sender, String label, String[] args) {
        CommandContext ctx = new CommandContext(plugin, this, sender, args);

        try{
            this.execute(ctx);
        }catch(CommandInterrupt interrupt) {
            sender.sendMessage(plugin.getLang().getMessage(interrupt.getPath(), interrupt.getArgs()));
        }

        return true;
    }

    public void register() {
        CommandMap commandMap = getCommandMap();
        if(commandMap != null) {
            commandMap.register(command, this);
        }
    }

    public String getCommand() {
        return command;
    }

    private static CommandMap getCommandMap() {
        try{
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            return (CommandMap) commandMapField.get(Bukkit.getServer());
        }catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
