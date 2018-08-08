package com.digios.slowmoserver.photomaker;

import com.digios.slowmoserver.core.Config;
import com.digios.slowmoserver.core.Utils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MovieMaker {
    final static Logger logger = Logger.getLogger(MovieMaker.class);

    protected final String FFMPEG_PATH = Config.INSTANCE.ffmpegPath();
    protected final String FFPROBE_PATH = Config.INSTANCE.ffprobePath();
    protected final String RESULT_FULLPATH = Config.INSTANCE.resultFullPath();
    protected final String RESULT_START = Config.INSTANCE.resultStartVideoFile();
    protected final String RESULT_FINISH = Config.INSTANCE.resultFinishVideoFile();

    String render() throws Exception {
        throw new Exception("not implementation");
    }

    protected long getDuration(File videoFile) {
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

    protected Path trimVideoFile(File sourceVideoFile, String tempFolder, long startPos, long duration) {
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

    protected Path makeFileListFile(List<String> list, String tempFolder) throws FileNotFoundException, UnsupportedEncodingException {
        Path textFile = Paths.get(tempFolder, "list.txt");
        PrintWriter writer = new PrintWriter(textFile.toString(), "UTF-8");

        for (String line : list) {
            writer.println("file '" + line + "'");
        }

        writer.close();
        return textFile;
    }

    protected Path joinVideos(Path listFilePath, String tempFolder) {
        Path resultVideoFile = Paths.get(tempFolder, Utils.randomUUIDString() + ".mp4");

        String command = String.format("%s -f concat -safe 0 -i \"%s\" -c copy \"%s\"",
                FFMPEG_PATH,
                listFilePath.toString(),
                resultVideoFile.toString());

        String result = Utils.executeCommand(command);
        logger.info(result);

        return resultVideoFile;
    }

    protected void removeTempFolder(String path) {
        Utils.deleteDirectory(new File(path));
    }
}
