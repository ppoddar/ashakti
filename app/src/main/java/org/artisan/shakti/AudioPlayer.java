package org.artisan.shakti;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;

public class AudioPlayer {
     private SimpleExoPlayer player;
    final StyledPlayerControlView view;
    final Context ctx;
    final String audio;
    final MediaItem item;
    /**
     * A player exists for each activity instance.
     * The button would toggle play/pause.
     *
     * @param ctx a context
     * @param view a control to audio player play/pause button only configured
     * @param audio path to audio file
     */
    AudioPlayer (Context ctx, StyledPlayerControlView view, final String audio) {
        this.ctx = ctx;
        this.view = view;
        this.audio = audio;
        player = initPlayer();
        Uri uri = Uri.parse("asset:///" + audio);
        item = new MediaItem.Builder()
                .setUri(uri)
                .build();
        player.setMediaItem(item);
        player.prepare();

        view.findViewById(R.id.exo_play_pause).setOnClickListener((v)->play());
    }

    /**
     * play/pause given audio.
     */
    public void play() {
        if (player.isPlaying()) {
            Log.e("AudioPlayer", "pause " + audio);
            player.setPlayWhenReady(false);
        } else {
            Log.e("AudioPlayer", "play " + audio);
            player.setPlayWhenReady(true);
        }
    }

    SimpleExoPlayer initPlayer() {
        Log.e("AudioPlayer", "initPlayer");
        player = new SimpleExoPlayer.Builder(ctx).build();
        view.setPlayer(player);
        view.setShowTimeoutMs(0); // never expire
        return player;
    }

    public void release() {
        Log.e("AudioPlayer", "release");
        player.stop(true);
        player.release();
    }
}
