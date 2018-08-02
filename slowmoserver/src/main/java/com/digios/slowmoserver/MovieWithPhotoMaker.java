package com.digios.slowmoserver;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.*;

public class MovieWithPhotoMaker extends MovieMaker {
    final static Logger logger = Logger.getLogger(MovieWithPhotoMaker.class);

    private List<File> videoFiles;
    private List<File> photoFiles;

    private final long PHOTO_TIME = Config.INSTANCE.photoTime();
    private final long VIDEO_DELAY = Config.INSTANCE.videoDelay();


    public MovieWithPhotoMaker(List<File> videoFiles, List<File> photoFiles) {
        this.videoFiles = videoFiles;
        this.photoFiles = photoFiles;

        logger.info(videoFiles);
        logger.info(photoFiles);
    }

    @Override
    public String render() throws Exception {
        String tempFolder = Utils.createTempFolder();

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

        //removeTempFolder(tempFolder);

        return finishVideo.toString();
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


}
