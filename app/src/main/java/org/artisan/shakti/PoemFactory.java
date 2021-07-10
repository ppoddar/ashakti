package org.artisan.shakti;

import android.graphics.Typeface;
import android.util.Log;

import androidx.fragment.app.Fragment;

import org.artisan.shakti.model.Model;
import org.artisan.shakti.model.Poem;
import org.artisan.shakti.model.Poet;
import org.artisan.shakti.model.TOCEntry;

/**
 * Creates a factory to create {@link PoemFragment}.
 * Maintains a cache of fragments created. A factory creates poems for
 * a language. So there are two caches, one for each language.
 */
public class PoemFactory {
    private final Fragment[] cache;
    private final Typeface font;
    private final Language language;

    private static final String TAG = PoemFactory.class.getSimpleName();

    /**
     * create a factory
     * @param n
     */
    public PoemFactory(Typeface font, Language language, int n) {
        Log.e(TAG, "<init> pageCount=" + n + " language " + language);
        cache = new Fragment[n];
        this.font = font;
        this.language = language;
    }

    /**
     * Gets the cached {@link PoemFragment} if it exists, otherwise creates one
     * and caches. The 0-th cache entry is reserved for the {@link FrontPage},
     * which is not a {@link PoemFragment}. Hence the given position is reduced
     * by one to {@link Model#getEntry(int) lookup} in the model for an entry.
     * <p></p>
     * The poem is {@link PoemFragment#init(Poem, Typeface, String) initialized}.
     *
     * @param model a model used to initialize the poem
     * @param position a position of a requested page in the ViewPager
     * @return a fragment to be viewed in the ViewPager
     */
    public Fragment createPoem(Model model, int position) {
        Fragment page = cache[position];
        if (page != null) {
            Log.d(TAG, "createFragment() page " + position + " found in cache");
            return page;
        }
        Log.d(TAG, "createFragment() Language " + language + " position=" + position);
        if (position == 0) {
            Log.d(TAG, "createFragment() front page " + position + " language " + language);
            page = new FrontPage();
            Poet poet = model.getPoet(language);
            ((FrontPage) page).init(poet, language);
        } else if (position == cache.length-1) {   // last page
            Log.d(TAG, "createFragment() last page " + position + " language " + language);
            page = new LastPage();
            ((LastPage) page).init(font, language);
        } else {
            Log.d(TAG, "createFragment() poem " + position + " language " + language);
            page = new PoemFragment();
            TOCEntry entry = model.getEntry(position-1); // 0-th position is front page
            Poem poem = model.getPoem(position-1, language);
            ((PoemFragment)page).init(poem, font, entry.audio);
        }
        cache[position] = page;
        return page;
    }

    public int getSize() {
        return cache.length;
    }
}
