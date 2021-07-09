package org.artisan.shakti.model;

import android.content.Context;

import com.google.gson.JsonObject;

import org.artisan.shakti.Language;

public class Poet {
    public String title;
    public String name;
    public String lifetime;

    public static Poet fromJson(JsonObject json) throws Exception {
        Poet poet = new Poet();
        poet.title    = json.get("title").getAsString();
        poet.name     = json.get("name").getAsString();
        poet.lifetime = json.get("lifetime").getAsString();

        return poet;
    }

    }
