package org.artisan.shakti;

import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import androidx.fragment.app.Fragment;

import org.artisan.shakti.model.Model;
import org.artisan.shakti.model.Poem;

/**
 * Creates a factory to create {@link PoemFragment}.
 * Maintains a cache of fragments created. A factory creates poems for
 * a language. So there are two factories for a pair of languages.
 */
public class PoemFactory {
    final private Language language;
    final private Typeface font;
    final private Fragment[] fragments;

    public PoemFactory(Language lang, Typeface font, int n) {
        fragments = new Fragment[n];
        language = lang;
        this.font = font;
    }
    public Fragment createPoem(Model model, int position) {
        Log.e("PoemFactory", "Language " + language + " position=" + position);
        Fragment page = fragments[position];
        if (page != null) {
            Log.e("PoemFactory", "page " + position + " found in cache");
            return page;
        }

        if (position == 0) {
            page = new FrontPage();
        } else {
            Log.e("PoemFactory", "createFragment poem " + position + " ");
            Poem poem = model.getPoem(position-1, language); // 0-th position is front page
            Spanned html = Html.fromHtml(poem.content, Html.FROM_HTML_MODE_COMPACT);
            page = new PoemFragment();
            ((PoemFragment)page).setFont(font);
            ((PoemFragment)page).setText(html);
            ((PoemFragment)page).setAudio(model.getEntry(position-1).audio);
        }
        fragments[position] = page;
        return page;

    }
}
