package com.digios.slowmoserver.database;

import javax.print.attribute.DateTimeSyntax;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserDataForSend {
    private long id;
    private String email;
    private String fileName;
    private String createAt;

    public UserDataForSend(long id, String email, String fileName, String createAt) {
        this.id = id;
        this.email = email;
        this.fileName = fileName;
        this.createAt = createAt;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFileName() {
        return fileName;
    }

    public Map<String, String> toMap() {

        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("filename", fileName);
        map.put("createAt", createAt);

        return map;
    }
}
