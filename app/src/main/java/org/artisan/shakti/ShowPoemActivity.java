package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.FileDescriptor;

/**
 * Shows a poem in a language.
 */
public class ShowPoemActivity extends AppCompatActivity
        implements MediaPlayer.OnPreparedListener {
    private MediaPlayer mp;
    private TextView textView;
    private int cursor;
    private static final String ACTIVITY = ShowPoemActivity.class.getSimpleName();
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(ACTIVITY, "onCreate");
        setContentView(R.layout.activity_show_poem);
        setToolbar();

        ShaktiApplication app = (ShaktiApplication)getApplication();
        cursor = getIntent().getIntExtra(ShaktiApplication.KEY_CURSOR, 0);

        textView = findViewById(R.id.text_content);
        textView.setTypeface(app.getFont());
        textView.setOnTouchListener(new OnSwipeTouchListener(ShowPoemActivity.this) {
            public void onSwipeRight() {
                if (cursor + 1 >= app.getPoemCount()) {
                    gotoMain();
                } else {
                    nextPoem();
                }
            }

            public void onSwipeLeft() {
                if (cursor - 1 < 0) {
                    gotoMain();
                } else {
                    prevPoem();
                }
            }
        });
        textView.setOnTouchListener(new OnSwipeTouchListener(ShowPoemActivity.this) {
            public void onSwipeRight() {
                prevPoem();
            }
            public void onSwipeLeft() {
                nextPoem();
            }
        });

        showPoem(false);
    }

    @SuppressLint("NonConstantResourceId")
    void setToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
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
     * Displays a poem.
     * The current <tt>cursor</tt> selects the  poem from the app.
     * The cursor is not modified by this method.
     * @param changeLanguage if true, the language if switched
     */
    private void showPoem(boolean changeLanguage) {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        if (changeLanguage) {
            app.switchLanguage();
            textView.setTypeface(app.getFont());
        }
        Log.e(ACTIVITY, "show poem " + cursor + " in " + app.getCurrentLanguage()
         + (changeLanguage ? " (changed language)" : ""));
        String poem = app.getPoem(cursor);
        if (poem == null) {
            Log.e(ACTIVITY, "No poem found at cursor " + cursor);
            return;
        }
        textView.setText(Html.fromHtml(poem,
                Html.FROM_HTML_MODE_COMPACT));
    }

    private void nextPoem() {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        if (app.hasPoem(cursor + 1)) {
            cursor++;
            showPoem(false);
        } else {
            gotoMain();
        }
    }

    private void prevPoem() {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        if (app.hasPoem(cursor-1)) {
            cursor--;
            showPoem(false);
        } else {
            gotoMain();
        }
    }

    private void switchPoem() {
        showPoem(true);
    }

    void playAudio() {
        try {
            ShaktiApplication app = ((ShaktiApplication) getApplication());
            FileDescriptor audio = app.getAudioStream(cursor);
            if (audio == null) return;
            if (mp != null) {
                if (mp.isPlaying()) mp.stop();
                mp.release();
            }
            mp = new MediaPlayer();
            mp.setOnPreparedListener(this);
            Log.e(ACTIVITY, "playing " + audio);
            mp.setDataSource(audio);
            mp.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void pauseAudio() {
        if (mp == null || !mp.isPlaying())
            return;
        try {
            mp.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stopAudio() {
        if (mp == null || !mp.isPlaying())
            return;
        try {
            mp.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void gotoMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

}

