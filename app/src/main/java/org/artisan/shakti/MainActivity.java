package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.FileDescriptor;

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
        //textView.setMovementMethod(new ScrollingMovementMethod());

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

    @SuppressLint("NonConstantResourceId")
    void setToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_table_of_content:
                    break;
                case R.id.action_switch_language:
                    switchPoem();
                    break;
                case R.id.action_play_audio:
                    new Thread(this::playAudio).start();
                    break;
                case R.id.action_pause_audio:
                    pauseAudio();
                    break;
                case R.id.action_stop_audio:
                    stopAudio();
                    break;
            }
            return false;
        });

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

    void playAudio() {
        try {
            ShaktiApplication app = ((ShaktiApplication)getApplication());
            FileDescriptor audio = app.getAudioStream(cursor);
            if (audio == null) return;
            Log.e("SHAKTI", "playing " + audio);
            mp.setDataSource(audio);
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