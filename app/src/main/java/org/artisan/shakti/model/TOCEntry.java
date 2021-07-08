package org.artisan.shakti.model;

import android.content.Context;

import com.google.gson.JsonObject;

import org.artisan.shakti.Language;
import org.jetbrains.annotations.NotNull;

public class TOCEntry {
    public Poem english;
    public Poem bangla;
    public String audio;

    public static TOCEntry fromJson(JsonObject json, DirectoryPath dir, Context ctx) throws Exception {
        TOCEntry entry = new TOCEntry();
        entry.english = Poem.fromJson(json.get("english").getAsJsonObject(), dir.english, Language.ENGLISH, ctx);
        entry.bangla = Poem.fromJson(json.get("bangla").getAsJsonObject(), dir.bangla, Language.BANGLA, ctx);
        if (json.has("audio")) {
            entry.audio = dir.audio + '/' + json.get("audio").getAsString();
        }
        return entry;
    }

    @NotNull
    public String toString() {
        return english.toString() + '[' + bangla.toString() + "] audio=" + audio;
    }
}
