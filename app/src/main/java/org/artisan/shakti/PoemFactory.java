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
 * a language. So there are two factories for a pair of languages.
 */
public class PoemFactory {
    final private Language language;
    final private Typeface font;
    final private Fragment[] fragments;

    private static final String TAG = PoemFactory.class.getSimpleName();

    public PoemFactory(Language lang, Typeface font, int n) {
        fragments = new Fragment[n];
        language = lang;
        this.font = font;
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
        Log.d(TAG, "createFragment() Language " + language + " position=" + position);
        Fragment page = fragments[position];
        if (page != null) {
            Log.d(TAG, "createFragment() page " + position + " found in cache");
            return page;
        }

        if (position == 0) {
            page = new FrontPage();
            Poet poet = model.getPoet(language);
            ((FrontPage)page).init(poet, language);
        } else {
            Log.d(TAG, "createFragment() poem " + position + " ");
            TOCEntry entry = model.getEntry(position-1); // 0-th position is front page
            Poem poem = language == Language.BANGLA ? entry.bangla : entry.english;

            page = new PoemFragment();
            ((PoemFragment)page).init(poem, font, entry.audio);
        }
        fragments[position] = page;
        return page;
    }
}
