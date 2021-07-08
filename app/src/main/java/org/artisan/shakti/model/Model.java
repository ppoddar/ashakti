package org.artisan.shakti.model;

import android.graphics.Typeface;
import android.util.Log;

import org.artisan.shakti.Language;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The model for entire application.
 * Accessible statically from the application.
 */
public class Model {
    public Typeface englishFont;
    public Typeface banglaFont;
    public DirectoryPath roots;
    public Poet poet;
    private final List<TOCEntry> entries = new ArrayList<>();

    /**
     * Adds an entry
     * @param e to entry
     */
    public void add(@NotNull  TOCEntry e) {
        Log.e("Model", "addEntry " + entries.size() + " " + e);
        entries.add(e);
    }

    public TOCEntry getEntry(int position) {
        return entries.get(position);
    }

    public Poem getPoem(int position, Language lang) {
        TOCEntry entry = getEntry(position);
        return lang == Language.BANGLA ? entry.bangla : entry.english;
    }


    public int getPoemCount() {
        return entries.size();
    }
    public Typeface getFont(Language lang) {
        return lang == Language.BANGLA ? banglaFont : englishFont;

    }
}
