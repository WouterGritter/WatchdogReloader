package me.woutergritter.plugintemplate.commands;

import me.woutergritter.plugintemplate.Main;
import me.woutergritter.plugintemplate.commands.internal.CommandContext;
import me.woutergritter.plugintemplate.commands.internal.WCommand;

public class ExampleCMD extends WCommand {
    public ExampleCMD(Main plugin) {
        super(plugin, "example");
    }

    @Override
    public void execute(CommandContext ctx) {
        ctx.send("output");
    }
}
