package org.artisan.shakti.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.artisan.shakti.Language;
import org.artisan.shakti.model.DirectoryPath;
import org.artisan.shakti.model.Model;
import org.artisan.shakti.model.TOCEntry;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A facility to build the model.
 */
public class ModelBuilder {
    /**
     * Builds a model.
     *
     * @throws Exception if things go south
     */
    public static Model build(InputStream in, Context ctx) throws Exception {
        Model model = new Model();
        JsonObject json = JsonParser.parseReader(new InputStreamReader(in))
                .getAsJsonObject();
        model.roots = parseDirectoryPath(json, ctx);
        model.englishFont = parseFont(json, Language.ENGLISH, ctx);
        model.banglaFont  = parseFont(json, Language.BANGLA, ctx);
        model.poet = Poet.fromJson(json.get("poet").getAsJsonObject());
        JsonArray array = json.get("poems").getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject poem = array.get(i).getAsJsonObject();
            TOCEntry entry = TOCEntry.fromJson(poem, model.roots, ctx);
            model.add(entry);
        }
        return model;
    }

    @SuppressWarnings("unused")
    private static DirectoryPath parseDirectoryPath(JsonObject json, Context ctx) throws Exception {
        JsonObject rootJson = json.get("root-directory").getAsJsonObject();
        return DirectoryPath.fromJson(rootJson);
    }

    private static Typeface parseFont(JsonObject json, Language lang, Context ctx) throws Exception {
        JsonObject fonts = json.get("font").getAsJsonObject();
        AssetManager mgr = ctx.getResources().getAssets();
        String fontPath = fonts.get(lang.name().toLowerCase()).getAsString();
        return Typeface.createFromAsset(mgr, fontPath);

    }

}
