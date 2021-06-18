package org.artisan.shakti;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.HashSet;
import java.util.Set;

/**
 * A specialized dispatcher maintains a set of {@link com.google.android.exoplayer2.SimpleExoPlayer
 * audio player(s)} such that only more than one player can not play
 * at the same time.
 * If a player is requested to play, all other players would stop
 * playing.
 */
public class SingularPlayerDispatcher
        extends DefaultControlDispatcher {

    private final Set<Player> players = new HashSet<>();

    @Override
    public boolean dispatchSetPlayWhenReady(Player player, boolean playWhenReady) {
        if (playWhenReady) {
            stopAnyPlayerExcept(player);
        }
        return super.dispatchSetPlayWhenReady(player, playWhenReady);
    }

    public Player newPlayer(Context ctx, Uri uri) {
        SimpleExoPlayer p = new SimpleExoPlayer.Builder(ctx).build();
        players.add(p);
        MediaItem audio = new MediaItem.Builder().setUri(uri).build();
        p.setMediaItem(audio);

        return p;
    }

    /**
     * add the given player to mutually exclusive set.
     *
     * @param p a player. only added if not null.
     */
     private void addPlayer(Player p) {
        if (p != null) {
            players.add(p);
        }
    }

    void stopAnyPlayerExcept(Player p, boolean release) {
        for (Player player : players) {
            if (player == p) continue;
            if (player.isPlaying()) {
                player.stop();
                if (release) player.release();
            }
        }
    }
    void stopAnyPlayerExcept(Player p) {
         stopAnyPlayerExcept(p, false);
    }


        public void clear() {
         stopAnyPlayerExcept(null, true);
         players.clear();
    }

}
