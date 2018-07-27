package com.digios.slowmoserver;

import java.io.File;
import java.util.*;

public class MovieWithPhotoMaker implements MovieMaker {

    private List<File> videoFiles;
    private List<File> photoFiles;

    public MovieWithPhotoMaker(List<File> videoFiles, List<File> photoFiles) {
        this.videoFiles = videoFiles;
        this.photoFiles = photoFiles;
    }

    @Override
    public String render() {
        return null;
    }

    private List<File> createVideoFromImage(List<File> photoFiles) {
        List<File> files = new ArrayList<>();



        return files;
    }
}
