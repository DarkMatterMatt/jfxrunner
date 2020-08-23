package win.mattm.jfxrunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;

public class CLIArgs {
    public static void displayHelp() {
        System.out.println("Usage:\n" +
                "    java -jar jfxrunner.jar  my_program.jar  args to pass to my_program\n" +
                "    java -jar jfxrunner.jar  -jfx /path/to/jfx/lib  my_program.jar\n\n" +
                "    Parameters (must be before my_program.jar):\n" +
                "      -javaw                    run in background using javaw (default: false, use java and inherit I/O)\n" +
                "      -jfx     /path/           specifies the path to JavaFX (default: env.PATH_TO_FX, download)\n" +
                "      -modules javafx.controls  specifies the modules to load (default: all JavaFX modules)\n" +
                "      -help                     display this help"
        );
    }

    public static Parsed parse(String[] args) throws IllegalArgumentException {
        Parsed p = new Parsed();
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i].toLowerCase(Locale.ENGLISH);
            if (arg.equals("-javaw")) {
                p.useJavaw = true;
            }
            else if (arg.equals("-javafx")) {
                p.javaFX = Paths.get(args[++i]);
                if (!Files.isDirectory(p.javaFX)) {
                    throw new IllegalArgumentException("Invalid path to -javaFX: " + p.javaFX);
                }
                if (!p.javaFX.endsWith("lib")) {
                    System.err.println("WARN: -javaFX path should probably be " + p.javaFX.resolve("lib"));
                }
            }
            else if (arg.equals("-modules")) {
                p.modules = args[++i];
            }
            else if (arg.endsWith(".jar")) {
                p.jar = Paths.get(arg);
                if (!Files.exists(p.jar)) {
                    throw new IllegalArgumentException("Invalid path to jar: " + p.jar);
                }

                ArrayList<String> remainingArgs = new ArrayList<>();
                while (++i < args.length) {
                    remainingArgs.add(args[i]);
                }
                p.args = remainingArgs.toArray(new String[0]);
                break;
            }
            else if (arg.equals("help") || arg.equals("-help") || arg.equals("-h") || arg.equals("-?")) {
                displayHelp();
                System.exit(0);
            }
            else {
                displayHelp();
                throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }

        if (p.jar == null) {
            displayHelp();
            throw new IllegalArgumentException("Missing required positional parameter *.jar");
        }
        return p;
    }

    static class Parsed {
        public String[] args;
        public Path jar;
        public Path javaFX;
        public boolean useJavaw;
        public String modules = "javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web";
    }
}
