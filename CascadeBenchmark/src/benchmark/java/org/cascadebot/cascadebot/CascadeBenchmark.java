package org.cascadebot.cascadebot;

import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;

public class CascadeBenchmark {

    public static void main(String... args) {
        try {
            org.openjdk.jmh.Main.main(args);
        } catch (RunnerException | IOException e) {
            e.printStackTrace();
        }
    }

}
