package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.Objects;

/**
 * This activity displays content in two languages.
 * The list of content is available as HTML files.
 * The file names are statically declared.
 */
public class MainActivity extends AppCompatActivity {
    private final MediaPlayer mp = new MediaPlayer();
    private TextView textView;
    private int cursor;
    private Language lang;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();

        textView = findViewById(R.id.text_content);
        textView.setMovementMethod(new ScrollingMovementMethod());
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);

        cursor = 0;
        lang = Language.ENGLISH;
        showPoem(false);

        textView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                prevPoem();
            }

            public void onSwipeLeft() {
                nextPoem();
            }
        });
    }

    void setToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        if (toolbar == null) return;
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    /**
     * Displays a poem of current language and cursor.
     */
    private void showPoem(boolean changeLanguage) {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        String poem = app.getPoem(lang, cursor);
        if (poem == null) return;
        if (changeLanguage) {
            textView.setTypeface(app.getFont(lang));
        }

        textView.setText(Html.fromHtml(poem,
                Html.FROM_HTML_MODE_COMPACT));

    }

    private void nextPoem() {
        cursor++;
        showPoem(false);
    }

    private void prevPoem() {
        cursor++;
        showPoem(false);
    }

    private void switchPoem() {
        switch (this.lang) {
            case ENGLISH:
                this.lang = Language.BENGALI;
                break;
            case BENGALI:
                this.lang = Language.ENGLISH;
                break;
        }
        showPoem(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this)
                .inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu);
        // IMPORTANT
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.action_table_of_content:
                break;
            case R.id.action_switch_language:
                switchPoem();
                break;
            case R.id.action_play_audio:
                playAudio();
                break;
            case R.id.action_pause_audio:
                pauseAudio();
                break;
            case R.id.action_stop_audio:
                stopAudio();
                break;
        }
        return true;
    }

    void playAudio() {
        try {
            ShaktiApplication app = ((ShaktiApplication)getApplication());
            String audioFile = app.getAudio(cursor);
            if (audioFile == null) return;
            Uri audio = Uri.fromFile(new File(audioFile));
            Log.e("SHAKTI", "playing " + audio);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(app, audio);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void pauseAudio() {
        try {
            mp.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stopAudio() {
        //set up MediaPlayer
        try {
            mp.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}