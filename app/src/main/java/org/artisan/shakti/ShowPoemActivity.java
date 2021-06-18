package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;

/**
 * This activity displays content in two languages.
 * The list of content is available from the application.
 * The content can ve accessed by (language, index) tuple.
 */
public class ShowPoemActivity extends AppCompatActivity {
    private static final String ACTIVITY = ShowPoemActivity.class.getSimpleName();
    private int cursor;
    private TextSwitcher switcher;
    private SimpleExoPlayer player;

    /**
     * This is called once on an instance.
     * The critical instance state us a integer cursor.
     * The state can be passed through the input bundle
     * or via the intent. By default, the cursor is zero.
     *
     * @param savedInstanceState saved state
     */
    @SuppressWarnings("unused")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ShaktiApplication app = (ShaktiApplication) getApplication();
        cursor = (savedInstanceState != null)
                ? savedInstanceState.getInt(ShaktiApplication.KEY_CURSOR)
                : getIntent().getIntExtra(ShaktiApplication.KEY_CURSOR, 0);

        Log.e(ACTIVITY, "onCreate cursor=" + cursor);
        setContentView(R.layout.activity_show_poem);

        setToolbar(app);
        setTextSwitcher(app);
        setNavigationButton(app);
        setAudioPlayer(app);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ShaktiApplication.KEY_CURSOR, cursor);
    }

    @Override
    public void onStart() {
        super.onStart();
        //ShaktiApplication app = (ShaktiApplication)getApplication();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(ACTIVITY, "onResume");
        ShaktiApplication app = (ShaktiApplication) getApplication();
        if (showPoem(cursor, app.getCurrentLanguage(), false)) {
            setAudioPlayer(app);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        player.stop(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        player.release();
    }

    @SuppressLint("NonConstantResourceId")
    void setToolbar(final ShaktiApplication app) {
        final Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        showHomeScreen();
                        break;
                    case R.id.action_table_of_content:
                        showTOC();
                        break;
                    case R.id.action_switch_language:
                        switchPoem();
                        break;
                }
                return true; // the click is handled here itself            }
            }
        });
    }


    /**
     * Adds two text views to a TextSwitcher declared n XML layout.
     * Configures the child views to display language specific font.
     *
     * @param app the application
     */
    @SuppressLint("ClickableViewAccessibility")
    void setTextSwitcher(final ShaktiApplication app) {
        switcher = findViewById(R.id.text_content_switcher);
        TextView view1 = new TextView(ShowPoemActivity.this);
        TextView view2 = new TextView(ShowPoemActivity.this);
        view1.setTypeface(app.getFont(Language.ENGLISH));
        view2.setTypeface(app.getFont(Language.BANGLA));
        switcher.addView(view1);
        switcher.addView(view2);

        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        /**
         * the next and previous poem is shown by right and left swipe.
         * if the cursor is out of range, the main activity pag eis shown.
         */
        switcher.setOnTouchListener(new OnSwipeTouchListener(ShowPoemActivity.this) {
            public void onSwipeRight() {
                if (!nextPoem()) showHomeScreen();
            }

            public void onSwipeLeft() {
                if (!prevPoem()) showHomeScreen();
            }
        });
    }

    /**
     * sets audio player if audio exists.
     * IMPORTANT: must be called when cursor changes to make audio control
     * visible or invisible
     */
    void setAudioPlayer(ShaktiApplication app) {
        StyledPlayerControlView view = findViewById(R.id.audio_player);
        view.setShowTimeoutMs(0); // never expire
        Uri uri = app.getAudioUri(cursor);
        view.setVisibility(uri == null ? View.INVISIBLE : View.VISIBLE);
        String title = app.getPoemTitle(app.getCurrentLanguage(), cursor);
        if (uri != null) {
            Log.e(ACTIVITY, "enabling audio playback " + title + " uri " + uri);
            initAudioPlayer(view, uri);
        }
    }

    /**
     * sets the button to next and previous poem.
     * must be updated after cursor changes
     *
     * @param app application
     */
    void setNavigationButton(ShaktiApplication app) {
        ImageButton next = findViewById(R.id.action_next_poem);
        ImageButton prev = findViewById(R.id.action_prev_poem);
        next.setOnClickListener(v -> {
            if (!nextPoem()) showHomeScreen();
        });
        prev.setOnClickListener(v -> {
            if (!prevPoem()) showHomeScreen();
        });

    }

    /**
     * long running operation.
     *
     * @param view a view to attach
     * @param uri  an media URI
     */
    void initAudioPlayer(StyledPlayerControlView view, Uri uri) {
        ProgressBar progress = findViewById(R.id.progress_bar);
        try {
            progress.setVisibility(View.VISIBLE);
            player = new SimpleExoPlayer.Builder(this).build();
            Log.e(ACTIVITY, "creating audio media item from " + uri);
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(uri)
                    .build();
            player.setMediaItem(mediaItem);

            view.setPlayer(player);
        } finally {
            progress.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Displays a poem.
     * The current <tt>cursor</tt> selects the  poem from the application.
     * The cursor is not modified by this method.
     *
     * @param index    index of the poem to be shown
     * @param language the poem language.
     * @return true if a poem can be displayed i.e. the cursor is
     * within range. false, otherwise.
     */
    private boolean showPoem(int index, Language language, boolean changeLanguage) {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        String poem = app.getPoem(index);
        if (poem == null) {
            Log.e(ACTIVITY, "No poem found at " + index);
            return false;
        }
        Spanned text = Html.fromHtml(poem, Html.FROM_HTML_MODE_COMPACT);
        if (changeLanguage) {
            switcher.setText(text);
        } else {
            switcher.setCurrentText(text);
        }

        return true;
    }

    /**
     * shows next poem.
     * IMPORTANT: increments the cursor if next poem exists
     *
     * @return true if next poem exists and was shown
     */
    private boolean nextPoem() {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        boolean success = this.showPoem(cursor + 1, app.getCurrentLanguage(), false);
        if (success) {
            updateCursor(1);
        }
        return success;
    }

    /**
     * shows previous poem.
     * IMPORTANT: decrements the cursor if previous poem exists
     *
     * @return true if previous poem exists and was shown
     */
    private boolean prevPoem() {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        boolean success = this.showPoem(cursor - 1, app.getCurrentLanguage(), false);
        if (success) {
            updateCursor(-1);
        }
        return success;
    }

    private void updateCursor(int change) {
        cursor += change;
        ShaktiApplication app = (ShaktiApplication) getApplication();
        if (player != null) {
            try {
                player.stop(true);
            } catch (Exception ex) {
                // ignore ?
            }
        }
        setAudioPlayer(app);
        setNavigationButton(app);

    }

    /**
     * show the poem at current cursor in a different language
     * <p>
     * IMPORTANT: switches the active language.
     */
    private void switchPoem() {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        app.switchLanguage();
        showPoem(cursor, app.getCurrentLanguage(), true);
    }

    void showHomeScreen() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }

    void showTOC() {
        Intent intent = new Intent(getApplicationContext(), TOCActivity.class);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }
}



