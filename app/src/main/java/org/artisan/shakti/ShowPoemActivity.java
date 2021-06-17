package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;

/**
 * This activity displays content in two languages.
 * The list of content is available as HTML files.
 * The file names are statically declared.
 */
public class ShowPoemActivity extends AppCompatActivity {
    private static final String ACTIVITY = ShowPoemActivity.class.getSimpleName();

    private int cursor;
    private SimpleExoPlayer player;

    /**
     * This is called once on an instance.
     *
     * @param savedInstanceState saved state
     */
    @SuppressWarnings("unused")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //savedInstanceState.get(ShaktiApplication.KEY_CURSOR);
        Log.e(ACTIVITY, "onCreate");
        setContentView(R.layout.activity_show_poem);
        ShaktiApplication app = (ShaktiApplication) getApplication();
        cursor   = getIntent().getIntExtra(ShaktiApplication.KEY_CURSOR, -1);
        if (cursor < 0) {
            Log.e(ACTIVITY, "onCreate() in not created by explicit intent"
                    + "defaulting cursor to 0");
            cursor = 0;
        } else {
            Log.e(ACTIVITY, "onCreate() in created by explicit intent"
                    + "cursor is " + cursor);
        }
        Toolbar toolbar = setToolbar(app);
        TextSwitcher switcher = setTextSwitcher(app);
        player = setAudioPlayer(app);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(ACTIVITY, "onResume");
        ShaktiApplication app = (ShaktiApplication)getApplication();
        showPoem(cursor, app.getCurrentLanguage());
    }

    @Override
    public void onPause() {
        super.onPause();
        player.stop(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) player.release();
    }

    @SuppressLint("NonConstantResourceId")
    Toolbar setToolbar(final ShaktiApplication app) {
        final Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_table_of_content:
                    Intent showTOC = new Intent(getApplicationContext(), TOCActivity.class);
                    startActivity(showTOC);
                    break;
                case R.id.action_switch_language:
                    switchPoem();
                    break;
            }
            return false;
        });

        return toolbar;
    }

    @SuppressLint("ClickableViewAccessibility")
    TextSwitcher setTextSwitcher(final ShaktiApplication app) {
        TextSwitcher switcher = findViewById(R.id.text_content_switcher);
        TextView view1 = new TextView(ShowPoemActivity.this);
        TextView view2 = new TextView(ShowPoemActivity.this);
        view1.setTypeface(app.getFont(Language.ENGLISH));
        view2.setTypeface(app.getFont(Language.BANGLA));
        switcher.addView(view1);
        switcher.addView(view2);

        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        switcher.setOnTouchListener(new OnSwipeTouchListener(ShowPoemActivity.this) {
            public void onSwipeRight() {
                if (!nextPoem()) gotoMain();
            }

            public void onSwipeLeft() {
                if (!prevPoem()) gotoMain();

            }
        });

        return switcher;
    }

    SimpleExoPlayer setAudioPlayer(ShaktiApplication app) {
        SimpleExoPlayer player  = new SimpleExoPlayer.Builder(this).build();
        StyledPlayerControlView view = findViewById(R.id.audio_player);
        view.setShowTimeoutMs(0); // never expire
        Uri uri = app.getAudioUri(cursor);
        Log.e(ACTIVITY, "audio uri " + uri);
        if (uri != null) {
            Log.e(ACTIVITY, "creating audio media item from " + uri);
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(uri)
                    .build();
            player.setMediaItem(mediaItem);
            view.setPlayer(player);
        } else {
            Log.e(ACTIVITY, "disabling audio playback");
            view.setEnabled(false);
            view.setVisibility(View.INVISIBLE);
            player.release();
        }
        return player;
    }

    /**
     * Displays a poem.
     * The current <tt>cursor</tt> selects the  poem from the application.
     * The cursor is not modified by this method.
     * @param  index index of the poem to be shown
     * @param  language the poem language.
     */
    private boolean showPoem(int index, Language language) {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        String poem = app.getPoem(index);
        if (poem == null) {
            Log.e(ACTIVITY, "No poem found at " + index);
            return false;
        }
        TextSwitcher switcher = findViewById(R.id.text_content_switcher);
        Spanned text = Html.fromHtml(poem, Html.FROM_HTML_MODE_COMPACT);
        boolean changed = app.setLanguage(language);
        if (changed) {
            switcher.setText(text);
        } else {
            switcher.setCurrentText(text);
        }
        return true;
    }

    private boolean nextPoem() {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        boolean success = this.showPoem(cursor+1, app.getCurrentLanguage());
        if (success) {
            cursor++;
        }
        return success;
    }

    private boolean prevPoem() {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        boolean success = this.showPoem(cursor-1, app.getCurrentLanguage());
        if (success) {
            cursor--;
        }
        return success;
    }

    private void switchPoem() {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        app.switchLanguage();
        showPoem(cursor, app.getCurrentLanguage());
    }

    void gotoMain() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }
}



