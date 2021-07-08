package org.artisan.shakti.model;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.JsonObject;

import org.artisan.shakti.Language;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class Poem {
    public Language language;
    public String title;
    public String content;

    public static Poem fromJson(JsonObject json, String dir, Language lang, Context ctx) throws Exception {
        Poem poem = new Poem();
        AssetManager assets = ctx.getResources().getAssets();
        poem.title = json.get("title").getAsString();
        poem.language = lang;
        String src = json.get("source").getAsString();
        //Uri uri = Uri.parse("assets:///" + dir + "/" + src);
        InputStream in = assets.open(dir + '/' + src);
        poem.content = readContent(in);
        return poem;
    }

    static String readContent(InputStream stream) throws IOException {
        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        for (int numRead; (numRead = reader.read(buffer, 0, buffer.length)) > 0; ) {
            out.append(buffer, 0, numRead);
        }
        return out.toString();
    }

    @NotNull
    public String toString() {
        return title;
    }
}
