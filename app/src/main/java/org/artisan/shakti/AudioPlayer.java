package org.artisan.shakti;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Plays audio stream.
 * Manages multiple players each play/pause a single stream.
 */
public class AudioPlayer {
    private final SimpleExoPlayer delegate;
    final Context ctx;
    final StyledPlayerControlView view;
    final String audio;

    private static final List<SimpleExoPlayer> _players = new ArrayList<>();
    private static final String TAG = AudioPlayer.class.getSimpleName();

    /**
     * Creates a player.
     * @param ctx a context
     * @return an audio player to play the given audio
     */
    public static AudioPlayer create(Context ctx, StyledPlayerControlView view, String audio) {
        AudioPlayer player = new AudioPlayer(ctx, view, audio);
        _players.add(player.delegate);

        return player;
    }

    /**
     * A player exists for each activity instance.
     * The button would toggle play/pause.
     *  @param ctx a context
     */
    private AudioPlayer(@NotNull Context ctx, StyledPlayerControlView view, String audio) {
        this.ctx = ctx;
        this.view = view;
        this.audio = audio;
        Uri uri = Uri.parse("asset:///" + audio);
        MediaItem item = new MediaItem.Builder()
                .setUri(uri)
                .build();
        delegate = new SimpleExoPlayer.Builder(ctx).build();
        delegate.setMediaItem(item);
        view.setPlayer(delegate);
        delegate.prepare();

        //view.findViewById(R.id.exo_play_pause).setOnClickListener((v)->play());
    }

    /**
     * play current audio.
     */
    public void play() {
        for (SimpleExoPlayer p : _players) {
            if (p != delegate && p.isPlaying()) {
                p.stop();
            }
        }
        Log.e(TAG, this + ".play() " + audio);
        if (isPlaying()) {
            pause();
        } else {
            delegate.play();
        }
    }

    public boolean isPlaying() {
        return delegate.isPlaying();
    }
    public void pause() {
        Log.e(TAG, this + ".pause() " + audio);
        delegate.pause();
    }

    public void stop() {
        Log.e("AudioPlayer", "stop()");
        delegate.stop(false);
    }

    /**
     * Releases all audio players.
     */
    public static void release() {
        Log.e("AudioPlayer", "release");
        for (SimpleExoPlayer p : _players) {
            p.stop(true);
            p.release();
            p = null;
        }
    }

    @NotNull
    public String toString() {
        return AudioPlayer.class.getSimpleName() + "@"
                + Integer.toHexString(this.hashCode());
    }
}
