package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;

import org.jetbrains.annotations.NotNull;

/**
 * This activity displays content in two languages.
 * The list of content is available from the application.
 * The content can be accessed by {@link ShaktiApplication#getPoem(int, Language)}}..
 */
public class ShowPoemActivity extends AppCompatActivity {
    public static final int DEFAULT_CURSOR = 0;
    public static final Language DEFAULT_LANGUAGE = Language.ENGLISH;
    private static final String ACTIVITY = ShowPoemActivity.class.getSimpleName();
    private int cursor = DEFAULT_CURSOR;
    private Language language = DEFAULT_LANGUAGE;
    //private TextSwitcher switcher;
    private SimpleExoPlayer player;

    /**
     * Gets the currently active language.
     * The currently active language determines the font.
     *
     * @return currently active language
     */
    private Language getCurrentLanguage() {
        return language;
    }

    /**
     * This is called once on an instance.
     * The critical instance state is integer cursor and language.
     * The state can be passed through the input bundle
     * or via the intent. By default, the cursor is zero.
     *
     * @param savedInstanceState saved state
     */
    @SuppressWarnings("unused")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShaktiApplication app = (ShaktiApplication) getApplication();
        if (savedInstanceState != null) {
            cursor = savedInstanceState.getInt(ShaktiApplication.KEY_CURSOR);
            String lang = savedInstanceState.getString(ShaktiApplication.KEY_LANGUAGE, DEFAULT_LANGUAGE.toString());
            language = Language.valueOf(lang.toUpperCase());
        }
        Log.d(ACTIVITY, String.format("onCreate cursor=%d language=%s", cursor, language));
        setContentView(R.layout.activity_show_poem);

        setToolbar();
        setNavigationButton();
        setAudioPlayer(app);
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        super.onNewIntent(null);
        bundle.putInt(ShaktiApplication.KEY_CURSOR, cursor);
        bundle.putString(ShaktiApplication.KEY_LANGUAGE, language.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ACTIVITY, "onResume");
        ShaktiApplication app = (ShaktiApplication) getApplication();
        String lang = getIntent().getStringExtra(ShaktiApplication.KEY_LANGUAGE);
        if (lang == null) lang = DEFAULT_LANGUAGE.toString();
        cursor = getIntent().getIntExtra(ShaktiApplication.KEY_CURSOR, cursor);
        language = Language.valueOf(lang.toUpperCase());
        if (showPoem(cursor)) {
            setAudioPlayer(app);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.stop(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_show_poem);
        toolbar.setTitle(language == Language.BANGLA ? "শক্তি" : "Shakti");
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    showHome();
                    break;
                case R.id.action_table_of_content:
                    showTOC();
                    break;
                case R.id.action_switch_language:
                    switchPoem();
                    break;
                case R.id.action_biography:
                    showWebpage("Biography", "html/biography.html");
                    break;
                case R.id.action_about:
                    showWebpage("Notes", "html/notes.html");
                    break;
            }
            return true; // the click is handled here itself            }
        });
    }

    /**
     * sets audio player if audio exists.
     * IMPORTANT: must be called when cursor changes to make audio control
     * visible or invisible
     */
    private void setAudioPlayer(ShaktiApplication app) {
        StyledPlayerControlView view = findViewById(R.id.audio_player);
        view.setShowTimeoutMs(0); // never expire
        Uri uri = app.getAudioUri(cursor);
        view.setVisibility(uri == null ? View.INVISIBLE : View.VISIBLE);
        String title = app.getPoemTitle(getCurrentLanguage(), cursor);
        if (uri != null) {
            Log.e(ACTIVITY, "enabling audio playback " + title + " uri " + uri);
            initAudioPlayer(view, uri);
        }
    }

    /**
     * sets the button to next and previous poem.
     * must be updated after cursor changes
     */
    private void setNavigationButton() {
        ImageButton next = findViewById(R.id.action_next_poem);
        ImageButton prev = findViewById(R.id.action_prev_poem);
        next.setOnClickListener(v -> showNextPoem());
        prev.setOnClickListener(v -> showPreviousPoem());

    }

    /**
     * long running operation.
     *
     * @param view a view to attach
     * @param uri  an media URI
     */
    private void initAudioPlayer(StyledPlayerControlView view, Uri uri) {
        try {
            player = new SimpleExoPlayer.Builder(this).build();
            Log.e(ACTIVITY, "creating audio media item from " + uri);
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(uri)
                    .build();
            player.setMediaItem(mediaItem);

            view.setPlayer(player);
        } catch (Exception ex) {
            // TODO: Error Dialog
            ex.printStackTrace();
        }
    }

    /**
     * Displays a poem.
     * The current <tt>cursor</tt> selects the  poem from the application.
     * The cursor is not modified by this method.
     *
     * @param index index of the poem to be shown
     * @return true if a poem can be displayed i.e. the cursor is
     * within range. false, otherwise.
     */
    private boolean showPoem(int index) {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        String poemText = app.getPoem(index, language);
        if (poemText == null) {
            Log.e(ACTIVITY, "No poem found at " + index);
            return false;
        }
        Spanned poemHtml = Html.fromHtml(poemText, Html.FROM_HTML_MODE_COMPACT);
        PoemFragment poem = new PoemFragment();
        poem.setText(poemHtml);
        poem.setLanguage(app.getFontFor(language));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_view_poem, poem)
                .commit();
        return true;
    }

    /**
     * Switches the language for display.
     */
    private void switchLanguage() {
        Language old = this.getCurrentLanguage();
        language = getCurrentLanguage() == Language.ENGLISH
                ? Language.BANGLA : Language.ENGLISH;
        Log.d(ACTIVITY, "Switched language from " + old + " to " + language);
    }

    /**
     * shows next poem.
     * IMPORTANT: increments the cursor if next poem exists
     */
    private void showNextPoem() {
        boolean success = this.showPoem(cursor + 1);
        if (success) {
            updateCursor(1);
        } else {
            showHome();
        }
    }

    /**
     * shows previous poem.
     * IMPORTANT: decrements the cursor if previous poem exists
     */
    private void showPreviousPoem() {
        boolean success = this.showPoem(cursor - 1);
        if (success) {
            updateCursor(-1);
        } else {
            showHome();
        }
    }

    /**
     * updates internal state and rendered views when cursor changes.
     *
     * @param cursorDelta delta of cursor. can be both positive or negative.
     */
    private void updateCursor(int cursorDelta) {
        if (cursorDelta == 0) return;
        cursor += cursorDelta;
        ShaktiApplication app = (ShaktiApplication) getApplication();
        if (player != null) {
            try {
                player.stop(true);
            } catch (Exception ex) {
                // ignore ?
            }
        }
        setAudioPlayer(app);
        setNavigationButton();
    }

    /**
     * show the poem at current cursor in a different language
     * <p>
     * IMPORTANT: switches the active language. Hence all subsequent
     * poems will be displayed in changed language
     */
    private void switchPoem() {
        switchLanguage();
        showPoem(cursor);
    }

    private void showHome() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ShaktiApplication.KEY_LANGUAGE, language.toString());
        main.putExtras(bundle);
        startActivity(main);
    }

    private void showTOC() {
        Intent intent = new Intent(getApplicationContext(), TOCActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) player.release();
    }

    void showWebpage(String title, String url) {
        Intent intent = new Intent(getApplicationContext(), LocalWebActivity.class);
        intent.putExtra(LocalWebActivity.KEY_URL, url);
        intent.putExtra(LocalWebActivity.KEY_TITLE, title);
        startActivity(intent);
    }

}