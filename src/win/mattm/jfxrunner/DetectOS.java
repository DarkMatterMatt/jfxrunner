package win.mattm.jfxrunner;

import java.util.Locale;

public class DetectOS {
    private static OS detectedOS;

    public static OS getOS() {
        if (detectedOS == null) {
            String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((osName.contains("mac")) || (osName.contains("darwin"))) {
                detectedOS = OS.MacOS;
            }
            else if (osName.contains("win")) {
                detectedOS = OS.Windows;
            }
            else if (osName.contains("nux")) {
                detectedOS = OS.Linux;
            }
            else {
                detectedOS = OS.Other;
            }
        }
        return detectedOS;
    }

    enum OS {
        Windows,
        MacOS,
        Linux,
        Other
    }
}
