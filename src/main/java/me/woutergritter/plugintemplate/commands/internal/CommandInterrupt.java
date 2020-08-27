package me.woutergritter.plugintemplate.commands.internal;

public class CommandInterrupt extends RuntimeException {
    private final String path;
    private final Object[] args;

    public CommandInterrupt(String path, Object... args) {
        super(path);

        this.path = path;
        this.args = args;
    }

    public String getPath() {
        return path;
    }

    public Object[] getArgs() {
        return args;
    }
}
