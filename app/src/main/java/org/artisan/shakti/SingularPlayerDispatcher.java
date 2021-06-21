package org.artisan.shakti;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * A specialized dispatcher maintains a set of {@link com.google.android.exoplayer2.SimpleExoPlayer
 * audio player(s)} such that only more than one player can not play
 * at the same time.
 * If a player is requested to play, all other players would stop
 * playing.
 */
class SingularPlayerDispatcher
        extends DefaultControlDispatcher {

    private final Set<Player> players = new HashSet<>();

    @Override
    public boolean dispatchSetPlayWhenReady(@NotNull Player player, boolean playWhenReady) {
        if (playWhenReady) {
            stopAnyPlayerExcept(player);
        }
        return super.dispatchSetPlayWhenReady(player, playWhenReady);
    }

    /**
     * Creates a new player for given uri. caches it
     * @param ctx context
     * @param uri audio uri
     * @return a new player
     */
    public Player newPlayer(Context ctx, Uri uri) {
        SimpleExoPlayer p = new SimpleExoPlayer.Builder(ctx).build();
        players.add(p);
        MediaItem audio = new MediaItem.Builder().setUri(uri).build();
        p.setMediaItem(audio);

        return p;
    }

    private void stopAnyPlayerExcept(Player p, boolean release) {
        for (Player player : players) {
            if (player == p) continue;
            if (player.isPlaying()) {
                player.stop();
                if (release) player.release();
            }
        }
    }
    private void stopAnyPlayerExcept(Player p) {
         stopAnyPlayerExcept(p, false);
    }


    void clear() {
         stopAnyPlayerExcept(null, true);
         players.clear();
    }

}
