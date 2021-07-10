package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ui.StyledPlayerControlView;

import org.artisan.shakti.model.Model;
import org.artisan.shakti.model.TOCEntry;

/**
 * Handles the table of contents in two different languages.
 * Shows a list of poem titles. The title can be clicked to
 * show the poem in its own language.
 *
 * An optional audio
 * player control can play the audio, if one is available.
 * A single audio player for all the audio.
 *
 */
public class TOCActivity extends AppCompatActivity {
    //public static final String TAG = TOCActivity.class.getSimpleName();
    private Model model;
    private ListView list;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toc);
        ShaktiApplication app = (ShaktiApplication) getApplication();
        this.model = app.getModel();
        this.list = findViewById(R.id.toc_entries);
        TOCAdapter adapter = new TOCAdapter();
        this.list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        AudioPlayer.release();
    }

    // --------------------------------------------------------------
    class TOCAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return model.getPoemCount();
        }

        @Override
        public Object getItem(int position) {
            return model.getEntry(position);
        }

        @Override
        public long getItemId(int position) {
            return model.getEntry(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.toc_entry_template, list, false);
            }
            final Context ctx = getApplicationContext();
            TextView englishTitle = convertView.findViewById(R.id.english_title);
            TextView banglaTitle = convertView.findViewById(R.id.bangla_title);
            englishTitle.setOnClickListener((v) -> {
                Intent intent = new Intent(ctx, MainActivity.class);
                intent.putExtra(ShaktiApplication.KEY_CURSOR, position + 1);// 0-th page is front page
                intent.putExtra(ShaktiApplication.KEY_LANGUAGE, Language.ENGLISH.ordinal());// 0-th page is front page
                startActivity(intent);
            });
            banglaTitle.setOnClickListener((v) -> {
                Intent intent = new Intent(ctx, MainActivity.class);
                intent.putExtra(ShaktiApplication.KEY_CURSOR, position + 1);// 0-th page is front page
                intent.putExtra(ShaktiApplication.KEY_LANGUAGE, Language.BANGLA.ordinal());// 0-th page is front page
                startActivity(intent);
            });

            TOCEntry entry = (TOCEntry) getItem(position);
            englishTitle.setText(entry.english.title);
            banglaTitle.setText(entry.bangla.title);
            englishTitle.setTypeface(model.getFont(Language.ENGLISH));
            banglaTitle.setTypeface(model.getFont(Language.BANGLA));

            // ----------- audio -------------
            // each TOCEntry has a dedicated AudioPlayer. The StyledPlayerControlView
            // is attached to the player to reflect the player state of play.
            // However, playing an audio would pause all other players
            // Multiple players are managed by AudioPlayer
            StyledPlayerControlView audioButton = convertView.findViewById(R.id.action_play_audio);
            if (entry.audio != null) {
                audioButton.setVisibility(View.VISIBLE);
                final AudioPlayer player = AudioPlayer.create(
                        TOCActivity.this.getApplicationContext(),
                        audioButton, entry.audio);
                // the visible control is a button inside StyledPlayerControlView
                View ctl = audioButton.findViewById(R.id.exo_play_pause);
                ctl.setOnClickListener(v -> player.play());
            } else {
                audioButton.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }
}