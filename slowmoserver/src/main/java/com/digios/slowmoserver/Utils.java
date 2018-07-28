package com.digios.slowmoserver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
}
