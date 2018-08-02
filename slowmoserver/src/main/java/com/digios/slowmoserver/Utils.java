package com.digios.slowmoserver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Utils {

    public static String randomUUIDString() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void executeCommandNotWait(String command) {
        try {
            Runtime.getRuntime().exec(command);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String executeCommand(String command) {
        StringBuffer inputString = new StringBuffer();

        try {
            Process process = Runtime.getRuntime().exec(command);

            StreamGobbler inputStreamGobbler = new StreamGobbler(process.getInputStream(), (x) ->
                        {
                            inputString.append(x);
                            inputString.append('\n');
                        });

            StreamGobbler errorStreamGobbler = new StreamGobbler(process.getErrorStream(), (x) ->
                        {
                            inputString.append(x);
                            inputString.append('\n');
                        });

            Executors.newSingleThreadExecutor().submit(inputStreamGobbler);
            Executors.newSingleThreadExecutor().submit(errorStreamGobbler);

            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
                inputString.append(line + "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputString.toString();
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

    public static String timeFromMillis(long durationInMillis) {
        long millis = durationInMillis % 1000;
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
    }

    public static void createPath(Path pathToFile) throws IOException {
        Files.createDirectories(pathToFile);
    }

    public static String createTempFolder() throws IOException {
        String folderName = Utils.randomUUIDString();
        Path pathToFile = Paths.get(Variables.tempDirPath(), folderName);
        Files.createDirectories(pathToFile);

        return pathToFile.toString();
    }

    public static File lastFileModified(String dir, boolean isSlowMo) {
        try {
            File fl = new File(dir);
            File[] files = fl.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    boolean assept =  (file.isFile() && (file.getName().contains(".jpg") || file.getName().contains(".mp4") || file.getName().contains(".png")));
                    if (isSlowMo) {
                        if (file.getName().contains(".mp4")) {
                            return true;
                        } else {
                            return false;
                        }
                    } else return assept;
                }
            });
            long lastMod = Long.MIN_VALUE;
            File choice = null;
            for (File file : files) {
                BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                if (attr.creationTime().toMillis() > lastMod) {
                    choice = file;
                    lastMod = attr.lastAccessTime().toMillis();
                }
            }
            return choice;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFileExtension(String file) {
        String name = file;
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}
