package dev.xfj.engine.core.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationCommandLineArgs {
    public int count;
    public List<String> arguments;

    public ApplicationCommandLineArgs() {
        this.count = 0;
        this.arguments = new ArrayList<>();;
    }

    public ApplicationCommandLineArgs(int count, String[] arguments) {
        this.count = count;
        this.arguments = new ArrayList<>(Arrays.asList(arguments));
    }

    public String get(int index) {
        return arguments.get(index);
    }
}
