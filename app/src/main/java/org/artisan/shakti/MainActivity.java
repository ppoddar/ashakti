package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;

import org.artisan.shakti.model.Model;
import org.artisan.shakti.model.Poem;
import org.artisan.shakti.model.TOCEntry;
import org.jetbrains.annotations.NotNull;

import static android.view.View.INVISIBLE;

/**
 * Main Activity is a ViewPager. The pages viewed are {@link PoemFragment}
 * via a pair of {@link PoemViewAdapter adapters}, each adapter serves pages
 * for one particular language.
 *
 */
public class MainActivity extends FragmentActivity {
    private ViewPager2 pager;
    private PoemViewAdapter englishAdapter;
    private PoemViewAdapter banglaAdapter;
    public static final String TAG = MainActivity.class.getSimpleName();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShaktiApplication app = (ShaktiApplication) getApplication();
        Language lang = findLanguage(savedInstanceState);
        app.setLanguage(lang == null ? ShaktiApplication.LANGUAGE_DEFAULT : lang);
        banglaAdapter  = new PoemViewAdapter(this);
        banglaAdapter.init(app, Language.BANGLA);
        englishAdapter = new PoemViewAdapter(this);
        englishAdapter.init(app, Language.ENGLISH);
        pager = findViewById(R.id.pager);

        pager.setAdapter(app.getCurrentLanguage() == Language.BANGLA
            ? banglaAdapter : englishAdapter);
        pager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        pager.setEnabled(true);

        pager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull @NotNull View page, float position) {

            }
        });
        pager.setCurrentItem(findCursor(savedInstanceState));
        pager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ShaktiApplication app = ((ShaktiApplication)getApplication());
        outState.putInt(ShaktiApplication.KEY_CURSOR, pager.getCurrentItem());
        outState.putInt(ShaktiApplication.KEY_LANGUAGE, app.getCurrentLanguage().ordinal());
    }

    public void switchToLanguage(Language language) {
        Log.e(TAG, "switchToLanguage " + language);
        ShaktiApplication app = ((ShaktiApplication)getApplication());
        int index = pager.getCurrentItem();
        if (language == Language.ENGLISH) {
            app.setLanguage(Language.ENGLISH);
            pager.setAdapter(englishAdapter);
        } else {
            app.setLanguage(Language.BANGLA);
            pager.setAdapter(banglaAdapter);
        }
        pager.setCurrentItem(index);
    }

    private Language findLanguage(Bundle savedInstanceState) {
        return Language.values()[findIntKey(savedInstanceState, ShaktiApplication.KEY_LANGUAGE)];
    }

    private int findCursor(Bundle savedInstanceState) {
        return findIntKey(savedInstanceState, ShaktiApplication.KEY_CURSOR);
    }

    private int findIntKey(Bundle savedInstanceState, String key) {
        int value = 0;
        if (savedInstanceState != null) {
            value = savedInstanceState.getInt(key);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            value = bundle.getInt(key);
        }
        return value;
    }


}