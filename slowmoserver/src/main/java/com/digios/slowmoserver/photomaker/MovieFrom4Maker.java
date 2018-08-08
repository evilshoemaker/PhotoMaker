package com.digios.slowmoserver.photomaker;

import com.digios.slowmoserver.core.Utils;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class MovieFrom4Maker extends MovieMaker {

    final static Logger logger = Logger.getLogger(MovieFrom4Maker.class);

    private List<File> videoFiles;
    private List<File> photoFiles;

    public MovieFrom4Maker(List<File> videoFiles, List<File> photoFiles) {
        this.videoFiles = videoFiles;
        this.photoFiles = photoFiles;
    }

    @Override
    public String render() throws Exception {

        if (videoFiles.isEmpty()) {
            throw new Exception("No files found");
        }

        if (videoFiles.size() < 4) {
            throw new Exception("Not enough files");
        }

        if (videoFiles.size() > 4) {
            logger.warn("Files more than expected. Part will not be used.");
        }

        String tempFolder = Utils.createTempFolder();

        List<String> videoForConcat = new ArrayList<String>();

        videoForConcat.add(getFirstPartVideo(videoFiles.get(0), tempFolder));
        videoForConcat.add(getMiddlePartVideo(videoFiles.get(1), tempFolder));
        videoForConcat.add(getMiddlePartVideo(videoFiles.get(2), tempFolder));
        videoForConcat.add(getLastPartVideo(videoFiles.get(3), tempFolder));

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

    private String getFirstPartVideo(File videoFile, String tempFile) throws Exception {
        long duration = getDuration(videoFile);
        if (duration == 0)
            throw new Exception("Duration = 0");

        long trimDuration = duration / 3;
        return trimVideoFile(videoFile, tempFile, 0, trimDuration).toString();
    }

    private String getMiddlePartVideo(File videoFile, String tempFile) throws Exception {
        long duration = getDuration(videoFile);
        if (duration == 0)
            throw new Exception("Duration = 0");

        long trimDuration = duration / 3;
        return trimVideoFile(videoFile, tempFile, trimDuration, trimDuration).toString();
    }

    private String getLastPartVideo(File videoFile, String tempFile) throws Exception {
        long duration = getDuration(videoFile);
        if (duration == 0)
            throw new Exception("Duration = 0");

        long trimDuration = duration / 3;
        return trimVideoFile(videoFile, tempFile, trimDuration * 2, trimDuration).toString();
    }
}
