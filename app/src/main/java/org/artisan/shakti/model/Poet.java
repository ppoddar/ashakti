package org.artisan.shakti.model;

import android.content.Context;

import com.google.gson.JsonObject;

import org.artisan.shakti.Language;

public class Poet {
    public String englishName;
    public String banglaName;
    public String englishLifetime;
    public String banglaLifetime;

    public static Poet fromJson(JsonObject json) throws Exception {
        Poet poet = new Poet();
        poet.englishName = json.get("english").getAsJsonObject().get("name").getAsString();
        poet.englishLifetime = json.get("english").getAsJsonObject().get("lifetime").getAsString();
        poet.banglaName = json.get("bangla").getAsJsonObject().get("name").getAsString();
        poet.banglaLifetime = json.get("bangla").getAsJsonObject().get("lifetime").getAsString();
        return poet;
    }

    }
