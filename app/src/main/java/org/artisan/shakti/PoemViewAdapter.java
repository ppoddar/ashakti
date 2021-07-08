package org.artisan.shakti;

import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import org.jetbrains.annotations.NotNull;

public class PoemViewAdapter extends FragmentStateAdapter {
     ShaktiApplication app;
     PoemFactory factory;
     int pageCount;
    public PoemViewAdapter(@NonNull FragmentActivity activity) {
        super(activity);
        Log.e("PoemViewAdapter", "<init> " + activity);



    }
    public void init(@NotNull ShaktiApplication app, Language lang) {
        this.app = app;
        Typeface font = app.getModel().getFont(lang);
        pageCount = app.getModel().getPoemCount()+1;// the 0-th page is the the front page
        factory = new PoemFactory(lang, font, pageCount);
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {
        Log.e("PoemViewAdapter", "createFragment() position="+position);
        Fragment f = factory.createPoem(app.getModel(), position);
        return f;
    }

    @Override
    public int getItemCount() {
        return pageCount;
    }


}
