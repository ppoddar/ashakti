package org.artisan.shakti.model;

import android.net.Uri;

import com.google.gson.JsonObject;

import java.io.InputStream;
import java.net.URI;

public class DirectoryPath {
    public String english;
    public String bangla;
    public String audio;

    public static DirectoryPath fromJson(JsonObject json) throws Exception {
        DirectoryPath obj = new DirectoryPath();

        obj.english = json.get("english").getAsString();
        obj.bangla  = json.get("bangla").getAsString();
        obj.audio   = json.get("audio").getAsString();
        return obj;
    }
}
