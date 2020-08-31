package me.woutergritter.plugintemplate.commands.internal;

public class CommandInterrupt extends RuntimeException {
    private final boolean absolutePath;
    private final String path;
    private final Object[] args;

    public CommandInterrupt(boolean absolutePath, String path, Object... args) {
        super(path);

        this.absolutePath = absolutePath;
        this.path = path;
        this.args = args;
    }

    public CommandInterrupt(String path, Object... args) {
        this(false, path, args);
    }

    public boolean isAbsolutePath() {
        return absolutePath;
    }

    public String getPath() {
        return path;
    }

    public Object[] getArgs() {
        return args;
    }
}
