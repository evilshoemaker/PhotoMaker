package com.digios.slowmoserver;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.*;

public class MovieWithPhotoMaker implements MovieMaker {
    final static Logger logger = Logger.getLogger(MovieWithPhotoMaker.class);

    private List<File> videoFiles;
    private List<File> photoFiles;

    private static String FFMPEG_PATH = Config.INSTANCE.ffmpegPath();
    private static String FFPROBE_PATH = Config.INSTANCE.ffprobePath();
    private static long PHOTO_TIME = Config.INSTANCE.photoTime();
    private static long VIDEO_DELAY = Config.INSTANCE.videoDelay();
    private static String RESULT_FULLPATH = Config.INSTANCE.resultFullPath();
    private static String RESULT_START = Config.INSTANCE.resultStartVideoFile();
    private static String RESULT_FINISH = Config.INSTANCE.resultFinishVideoFile();

    public MovieWithPhotoMaker(List<File> videoFiles, List<File> photoFiles) {
        this.videoFiles = videoFiles;
        this.photoFiles = photoFiles;
    }

    public String render() throws IOException {
        String tempFolder = createTempFolder();

        List<String> videoForConcat = new ArrayList<String>();

        for (File file: photoFiles) {
            videoForConcat.add(createVideoFromImage(file, tempFolder).toString());
        }

        for (File file : videoFiles) {
            long videoDelay = VIDEO_DELAY;
            if (VIDEO_DELAY == 0) {
                videoDelay = getDuration(file);
            }

            long duration = videoDelay / 2;
            videoForConcat.add(0, trimVideoFile(file, tempFolder, 0, duration).toString());
            videoForConcat.add(trimVideoFile(file, tempFolder, duration, 3600000).toString());
        }

        if (!RESULT_START.isEmpty()) {
            videoForConcat.add(0, RESULT_START);
        }

        if (!RESULT_FINISH.isEmpty()) {
            videoForConcat.add(RESULT_FINISH);
        }

        Path listFile = makeFileListFile(videoForConcat, tempFolder);

        Path resultVideo = joinVideos(listFile, tempFolder);
        Path finishVideo = Paths.get(RESULT_FULLPATH, resultVideo.getFileName().toString());
        Files.copy(resultVideo, finishVideo, StandardCopyOption.REPLACE_EXISTING);

        removeTempFolder(tempFolder);

        return finishVideo.toString();
    }

    private String createTempFolder() throws IOException {
        String folderName = Utils.randomUUIDString() + " slkfjl";
        Path pathToFile = Paths.get(Variables.tempDirPath(), folderName);
        Files.createDirectories(pathToFile);

        return pathToFile.toString();
    }

    private void removeTempFolder(String path) {
        Utils.deleteDirectory(new File(path));
    }

    private Path createVideoFromImage(File photoFile, String tempFolder) {
        Path videoFile = Paths.get(tempFolder, Utils.randomUUIDString() + ".mp4");

        double time = 1000.0 / PHOTO_TIME;
        String command = String.format("%s -f image2 -r %s -i \"%s\" -y -r 30 \"%s\"",
                                            FFMPEG_PATH,
                                            new DecimalFormat("#0.000").format(time).replace(',', '.'),
                                            photoFile.getAbsolutePath(),
                                            videoFile.toString());

        String result = Utils.executeCommand(command);
        logger.info(result);

        return videoFile;
    }

    private Path trimVideoFile(File sourceVideoFile, String tempFolder, long startPos, long duration) {
        Path videoFile = Paths.get(tempFolder, Utils.randomUUIDString() + ".mp4");

        String command = String.format("%s -ss %s -i \"%s\" -t %s \"%s\"",
                                            FFMPEG_PATH,
                                            Utils.timeFromMillis(startPos),
                                            sourceVideoFile.getAbsolutePath(),
                                            Utils.timeFromMillis(duration),
                                            videoFile.toString());

        String result = Utils.executeCommand(command);
        logger.info(result);

        return videoFile;
    }

    private long getDuration(File videoFile) {
        String command = String.format("%s -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 \"%s\"",
                FFPROBE_PATH, videoFile.getAbsolutePath());

        String result = Utils.executeCommand(command);

        try {
            return (long)(1000 * Float.parseFloat(result));
        }
        catch (Exception ex)
        {
            logger.error(ex);
        }

        return 0;
    }

    private Path makeFileListFile(List<String> list, String tempFolder) throws FileNotFoundException, UnsupportedEncodingException {
        Path textFile = Paths.get(tempFolder, "list.txt");
        PrintWriter writer = new PrintWriter(textFile.toString(), "UTF-8");

        for (String line : list) {
            writer.println("file '" + line + "'");
        }

        writer.close();
        return textFile;
    }

    private Path joinVideos(Path listFilePath, String tempFolder) {
        Path resultVideoFile = Paths.get(tempFolder, Utils.randomUUIDString() + ".mp4");

        String command = String.format("%s -f concat -safe 0 -i \"%s\" -c copy \"%s\"",
                FFMPEG_PATH,
                listFilePath.toString(),
                resultVideoFile.toString());

        String result = Utils.executeCommand(command);
        logger.info(result);

        return resultVideoFile;
    }
}
