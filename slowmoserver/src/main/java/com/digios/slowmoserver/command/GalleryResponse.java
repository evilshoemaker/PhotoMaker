package com.digios.slowmoserver.command;

import java.util.ArrayList;
import java.util.List;

public class GalleryResponse {
    String cmd = "gallery";
    List<String> files = new ArrayList<>();

    public GalleryResponse() {

    }

    public GalleryResponse(List<String> files) {
        this.files = files;
    }
}
