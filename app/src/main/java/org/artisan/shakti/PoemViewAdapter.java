package org.artisan.shakti;

import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

/**
 * An adapter that supplies the pages specific to a language.
 */
public class PoemViewAdapter extends FragmentStateAdapter {
     private final ShaktiApplication app;
     private final PoemFactory factory;
     private final Language language;
     private final Typeface font;
     private static final String TAG = PoemViewAdapter.class.getSimpleName();

    public PoemViewAdapter(@NonNull FragmentActivity activity,
                           @NotNull ShaktiApplication app,
                           Language lang) {
        super(activity);
        Log.e(TAG, "<init> " + " language: " + lang);
        this.app = app;
        this.language = lang;
        this.font     = app.getModel().getFont(this.language);
        factory = new PoemFactory(this.font, this.language,
                app.getModel().getPoemCount() +2);
    }


    /**
     * creates a fragment. The current language and font is used to configure
     * the fragment.
     * @param position the position
     * @return a fragment for the poem or the fornt and back page
     */
    @NotNull
    @Override

    public Fragment createFragment(int position) {
        return factory.createPoem(app.getModel(), position);
    }

    /**
     * gets the total number of pages which is one more than the number of poems
     * as teh 0-th page is reserved for the {@link FrontPage}.
     * @return number of pages to sweep through in the pager.
     */
    @Override
    public int getItemCount() {
        return factory.getSize();
    }
}
