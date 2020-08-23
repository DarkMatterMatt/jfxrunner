package win.mattm.jfxrunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static int EXIT_INVALID_ARGS = 1;
    public static int EXIT_JAVA_NOT_EXECUTABLE = 2;
    public static String JAVA_FX_MODULES = "javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web";

    private static void execute(String[] cmd, boolean inheritIO) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            if (inheritIO) {
                pb.inheritIO();
            }
            Process p = pb.start();
            if (inheritIO) {
                p.waitFor();
            }
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] argsStr) {
        CLIArgs.Parsed args;
        try {
            args = CLIArgs.parse(argsStr);
        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(EXIT_INVALID_ARGS);
            return; // unnecessary statement, helps code linting
        }

        Path java = args.useJavaw ? DetectJava.getJavaw() : DetectJava.getJava();
        if (java == null) {
            System.err.println("Could not resolve path to Java");
            System.exit(EXIT_JAVA_NOT_EXECUTABLE);
            return; // unnecessary statement, helps code linting
        }

        if (DetectJava.getVersion() == 8) {
            ArrayList<String> cmd = new ArrayList<>(Arrays.asList(
                    java.toString(),
                    "-jar", args.jar.toAbsolutePath().toString()
            ));
            cmd.addAll(Arrays.asList(args.args));
            execute(cmd.toArray(new String[0]), !args.useJavaw);
            return;
        }

        Path javaFX = args.javaFX;
        if (javaFX == null) {
            javaFX = DetectJavaFX.getJavaFX();
        }
        if (javaFX == null) {
            System.out.println("Env.PATH_TO_FX not found, downloading JavaFX. " +
                    "Already got a copy somewhere? Use the -jfx argument (before your .jar)");
            javaFX = DetectJavaFX.downloadJavaFX();
        }

        ArrayList<String> cmd = new ArrayList<>(Arrays.asList(
                java.toString(),
                "--module-path", javaFX.toAbsolutePath().toString(),
                "--add-modules", JAVA_FX_MODULES,
                "-jar", args.jar.toAbsolutePath().toString()
        ));
        cmd.addAll(Arrays.asList(args.args));
        execute(cmd.toArray(new String[0]), !args.useJavaw);
    }
}
