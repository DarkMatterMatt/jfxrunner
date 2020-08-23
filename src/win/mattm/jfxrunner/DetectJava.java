package win.mattm.jfxrunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DetectJava {
    private static int _Version = -1;
    private static Path _java;
    private static Path _javaBin;
    private static Path _javaw;

    private static String addExeExtension(String s) {
        return DetectOS.getOS() == DetectOS.OS.Windows ? s + ".exe" : s;
    }

    public static Path getJava() {
        if (_java == null) {
            Path javaBinPath = getJavaBin();
            if (javaBinPath == null) return null;

            Path javaPath = javaBinPath.resolve(addExeExtension("java"));
            _java = Files.isExecutable(javaPath) ? javaPath : null;
        }
        return _java;
    }

    public static Path getJavaBin() {
        if (_javaBin == null) {
            Path javaBinPath = Paths.get(System.getProperty("java.home"), "bin");
            _javaBin = Files.isDirectory(javaBinPath) ? javaBinPath : null;
        }
        return _javaBin;
    }

    public static Path getJavaw() {
        if (_javaw == null) {
            Path javaBinPath = getJavaBin();
            if (javaBinPath == null) return null;

            Path javawPath = javaBinPath.resolve(addExeExtension("javaw"));
            _javaw = Files.isExecutable(javawPath) ? javawPath : null;
        }
        return _javaw;
    }

    public static int getVersion() {
        if (_Version == -1) {
            _Version = Integer.parseInt(getVersion_());
        }
        return _Version;
    }

    private static String getVersion_() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            return version.substring(2, 3);
        }
        else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                return version.substring(0, dot);
            }
        }
        return version;
    }
}
