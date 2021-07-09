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
import androidx.appcompat.widget.Toolbar;

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
    public static final String TAG = TOCActivity.class.getSimpleName();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toc);
        setToolbar();

        ShaktiApplication app = (ShaktiApplication) getApplication();

        ListView tocEntries = findViewById(R.id.toc_entries);
        TOCAdapter adapter = new TOCAdapter(app.getModel(), tocEntries);
        tocEntries.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        //audioPlayer = new AudioPlayer(TOCActivity.this.getApplicationContext());
    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (audioPlayer != null) {
//            audioPlayer.release();
//            audioPlayer = null;
//        }
//    }

    /**
     * The toolbar for this activity .
     */
    @SuppressLint("NonConstantResourceId")
    void setToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_toc);
        toolbar.setOnMenuItemClickListener(item -> {
            final Context ctx = this.getApplicationContext();
            switch (item.getItemId()) {
                case R.id.action_home:
                    startActivity(new Intent(ctx, MainActivity.class));
                    break;
                case R.id.action_biography:
                    LocalWebActivity.showPage(ctx, "html/biography.html");
                    break;
                case R.id.action_about:
                    LocalWebActivity.showPage(ctx, "html/notes.html");
                    break;
            }
            return true; // the click is handled here itself
        });
    }



    // --------------------------------------------------------------
    class TOCAdapter extends BaseAdapter {
        final Model model;
        ListView list;

        TOCAdapter(Model model, ListView list) {
            this.model = model;
            this.list = list;

        }

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
                audioButton.setVisibility(View.GONE);
            }

            return convertView;
        }
    }
}


//    private static final SingularPlayerDispatcher dispatcher
//            = new SingularPlayerDispatcher();
//    private static SpannableString underline(final String text) {
//        SpannableString content = new SpannableString(text);
//        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
//        return content;
//    }

