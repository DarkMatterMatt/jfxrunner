package win.mattm.jfxrunner;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DetectJavaFX {
    public static String LATEST_JAVAFX_VERSION = "11.0.2";
    public static Path _javaFX;

    public static Path downloadJavaFX() {
        return downloadJavaFX(LATEST_JAVAFX_VERSION);
    }

    public static Path downloadJavaFX(String version) {
        String urlStr = getDownloadURL(version);
        if (urlStr == null) return null;

        String[] urlSplit = urlStr.split("/");
        String zipName = urlSplit[urlSplit.length - 1] + ".zip";
        Path finalPath = getPathInZip(version);

        if (Files.isDirectory(finalPath)) {
            return finalPath;
        }

        if (Files.exists(Paths.get(zipName))) {
            unzipDir(zipName, ".");
            return finalPath;
        }

        URL url;
        try {
            url = new URL(urlStr);
        }
        catch (MalformedURLException e) {
            System.err.println("Malformed download URL: " + urlStr);
            return null;
        }

        try (
                InputStream is = url.openStream();
                ReadableByteChannel rbc = Channels.newChannel(is);
                FileOutputStream fos = new FileOutputStream(zipName)
        ) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        catch (IOException e) {
            System.err.println("Failed downloading from " + urlStr);
            return null;
        }

        unzipDir(zipName, ".");
        return finalPath;
    }

    public static String getDownloadURL() {
        return getDownloadURL(LATEST_JAVAFX_VERSION);
    }

    public static String getDownloadURL(String version) {
        String os;
        switch (DetectOS.getOS()) {
            case Linux:
                os = "linux";
                break;
            case MacOS:
                os = "mac";
                break;
            case Windows:
                os = "windows";
                break;
            default:
                return null;
        }
        version = version.replaceAll("\\.", "-");
        return "https://gluonhq.com/download/javafx-" + version + "-sdk-" + os + "/";
    }

    public static Path getJavaFX() {
        if (_javaFX == null) {
            String env = System.getenv("PATH_TO_FX");
            if (env == null) return null;
            Path javaFxPath = Paths.get(env);
            _javaFX = Files.isDirectory(javaFxPath) ? javaFxPath : null;
        }
        return _javaFX;
    }

    public static Path getPathInZip(String version) {
        return Paths.get("javafx-sdk-" + version, "lib");
    }

    public static void unzipDir(String zipFile, String extractDir) {
        try {
            int BUFFER = 4096;
            ZipFile zip = new ZipFile(new File(zipFile));
            new File(extractDir).mkdir();

            Enumeration<? extends java.util.zip.ZipEntry> zipFileEntries = zip.entries();

            // process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = zipFileEntries.nextElement();
                String currentEntry = entry.getName();

                File destFile = new File(extractDir, currentEntry);
                File destinationParent = destFile.getParentFile();

                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte[] data = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
