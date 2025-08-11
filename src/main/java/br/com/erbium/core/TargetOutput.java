package br.com.erbium.core;

public final class TargetOutput {
    public static final int NONE = 0;
    public static final int CONSOLE = 1;
    public static final int REPORT = 2;
    public static final int CONSOLE_REPORT = 3;

    private TargetOutput() {
    }

    public static boolean includes(int destination, int flag) {
        return (destination & flag) == flag;
    }
}
