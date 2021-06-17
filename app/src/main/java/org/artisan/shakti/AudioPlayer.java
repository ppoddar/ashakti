package org.artisan.shakti;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

import java.io.FileDescriptor;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioPlayer implements
         MediaPlayer.OnPreparedListener,
MediaPlayer.OnErrorListener,
MediaPlayer.OnBufferingUpdateListener {
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private static final String SERVICE = AudioPlayer.class.getSimpleName();
    private final Activity app;
    private MediaPlayer mp;
    private boolean paused;
    private View play, pause, stop;
    private ProgressBar progress;
    private Snackbar snackbar;

    public AudioPlayer(Activity app) {
        this.app = app;
    }

    public AudioPlayer setControls(View play, View pause, View stop, ProgressBar pb) {
        this.play = play;
        this.pause = pause;
        this.stop = stop;
        this.progress = pb;
        return this;
    }

    /**
     * Plays a given audio file.
     * The media player would be prepared if necessary.
     * This work will be done in a thread separate from the
     * calling thread.
     * <p>
     * The progress bar will start at the beginning and
     * stop after the preparation or in case of any failure.
     *
     * @param fd a audio file descriptor
     */
    public void play(final FileDescriptor fd) {
        if (fd == null) return;
        reportStatus("playing audio...");
        updateButtonState();

        threadPool.execute(() -> {
            try {
                _playSync(fd);
            } catch (Exception ex) {
                reportError(ex);
            } finally {
                hideProgress();
                release();
            }
        });

    }

    private void _playSync(FileDescriptor fd) throws Exception {
        showProgress();
        if (mp == null) {
            reportStatus("creating new media player");
            mp = new MediaPlayer();
            mp.setDataSource(fd);
            reportStatus("preparing new media player...");
            mp.setOnPreparedListener(this);
            mp.setOnErrorListener(this);
            mp.setDisplay(null);
            mp.prepareAsync();
        } else if (mp.isPlaying()) {
            Log.e(SERVICE, "doing nothing as  media player is playing");
        } else if (mp.isLooping()) {
            Log.e(SERVICE, "doing nothing as  media player is looping");
        } else {
            reportStatus("starting existing media player...");
            mp.start();
        }
    }

    /**
     * pause player.
     */
    public void pause() {
        if (mp == null) return;
        paused = !paused;
        if (paused) {
            mp.pause();
        } else {
            resume();
        }
        app.runOnUiThread(() -> {
            if (play != null) play.setEnabled(true);
            if (pause != null) pause.setVisibility(View.VISIBLE);
            if (stop != null) stop.setVisibility(View.VISIBLE);
        });

    }

    public void resume() {
        if (mp == null) return;
        mp.start();
    }

    public void stop() {
        if (mp == null) return;
        mp.stop();
    }

    public void release() {
        if (mp != null) {
            mp.release();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        reportStatus("new media player prepared. Starting audio...");
        mp.start();
    }

    void reportStatus(String message) {
        Log.e(SERVICE, message);
        snackbar = Snackbar.make(progress.getRootView(), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    void reportError(Exception ex) {
        snackbar = Snackbar.make(progress.getRootView(),
                Objects.requireNonNull(ex.getMessage()),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        ex.printStackTrace();
    }

    void showProgress() {
        if (progress == null) return;
        app.runOnUiThread(() ->
                progress.setVisibility(View.VISIBLE));
    }

    void hideProgress() {
        if (progress == null) return;
        app.runOnUiThread(() ->
                progress.setVisibility(View.GONE));
    }

    void updateButtonState() {
        app.runOnUiThread(() -> {
            if (play != null) play.setEnabled(false);
            if (pause != null) pause.setEnabled(true);
            if (stop != null) stop.setEnabled(true);
        });

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e(SERVICE, "buffering " + percent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        reportError(new RuntimeException("media error " + what));

        return false;
    }
}
