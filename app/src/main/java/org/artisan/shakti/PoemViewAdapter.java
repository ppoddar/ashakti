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
     ShaktiApplication app;
     PoemFactory factory;
     int pageCount;
     private static final String TAG = PoemViewAdapter.class.getSimpleName();

    public PoemViewAdapter(@NonNull FragmentActivity activity) {
        super(activity);
        Log.e(TAG, "<init> " + activity);
    }

    /**
     * Initializes a view adapter. The {@link FragmentStateAdapter#createFragment(int)}
     * is delegated to a {@link PoemFactory factory}. The factory is also initialized.
     *
     * @param app an application
     * @param lang a language. The associated factory is specific to language
     */
    public void init(@NotNull ShaktiApplication app, Language lang) {
        this.app = app;
        Typeface font = app.getModel().getFont(lang);
        pageCount = app.getModel().getPoemCount()+1;// the 0-th page is the the front page
        factory   = new PoemFactory(lang, font, pageCount);
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {
        Log.e(TAG, "createFragment() position="+position);
        return factory.createPoem(app.getModel(), position);
    }

    /**
     * gets the total number of pages which is one more than the number of poems
     * as teh 0-th page is reserved for the {@link FrontPage}.
     * @return number of pages to sweep through in the pager.
     */
    @Override
    public int getItemCount() {
        return pageCount;
    }


}
