package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;

/**
 * Handles the table of contents in two different languages.
 * Shows a list of poem titles. The title can be clicked to
 * show the poem in its own language. An optional audio
 * player control can play the audio if one is available.
 */
public class TOCActivity extends AppCompatActivity {
    private static final SingularPlayerDispatcher dispatcher
            = new SingularPlayerDispatcher();

    private static final String ACTIVITY = TOCActivity.class.getSimpleName();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toc);
        setToolbar();
        setTOCEntries();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void setTOCEntries() {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        dispatcher.clear();
        for (int index = 0; index < app.getPoemCount(); index++) {
            Player player = null;
            if (app.hasAudio(index)) {
                player = dispatcher.newPlayer(this, app.getAudioUri(index));
            }
            String englishTitle = app.getPoemTitle(Language.ENGLISH, index);
            String banglaTitle  = app.getPoemTitle(Language.BANGLA, index);
            addTOCEntry(englishTitle, banglaTitle, player, index);
        }
    }

    /**
     * The toolbar for this activity allows to go to 'home' screen.
     * The irrelevant menu items are removed from the menu.
     */
    @SuppressLint("NonConstantResourceId")
    void setToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_toc);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    showHome();
                    break;
                case R.id.action_biography:
                    showWebpage("Biography", "html/biography.html");
                    break;
                case R.id.action_about:
                    showWebpage("Notes", "html/notes.html");
                    break;
            }
            return true; // the click is handled here itself
        });
    }

    /**
     * Starts MainActivity.
     */
    void showHome() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }

    /**
     * adds a TOC entry. A TOC entry is described in a template
     * <code>R.layout.toc_entry_template</code>.
     * The layout is inflated, the titles are set with appropriate
     * font and an optional audio player control.
     * The audio player control for each TOC entry are distinct.
     * But they share a common dispatcher that ensures that only
     * one player can be active at any given time.
     *  @param title1 english title
     * @param title2 bangla title
     * @param index  a index of the entry
     */
    void addTOCEntry(String title1, String title2,
                     Player player,
                     final int index) {
        Log.i(ACTIVITY, "adding toc entry " + title1 + " (" + title2 + ")");
        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup entries = findViewById(R.id.toc_entries);
        ViewGroup entry = (ViewGroup) inflater.inflate(R.layout.toc_entry_template, null);
        final TextView englishTitle = entry.findViewById(R.id.english_title);
        final TextView banglaTitle  = entry.findViewById(R.id.bangla_title);
        final StyledPlayerControlView audioPlayer = entry.findViewById(R.id.action_play_audio);
        if (player != null) {
            audioPlayer.show();
            audioPlayer.setPlayer(player);
            audioPlayer.setControlDispatcher(dispatcher);
        }
        englishTitle.setText(underline(title1));
        banglaTitle.setText(underline(title2));

        entries.addView(entry);
        // click on the entry will show the poem
        englishTitle.setOnClickListener(v -> showPoem(index, Language.ENGLISH));
        banglaTitle.setOnClickListener(v -> showPoem(index, Language.BANGLA));
    }

    /**
     * Shows a poem via ShowPoemActivity.
     * The poem index and language is passed to the intent that starts
     * the activity.
     * @param index the poem index
     * @param language the language
     */
    private void showPoem(int index, Language language) {
        dispatcher.clear(); // stops any running playback
        Intent showPoem = new Intent(getApplicationContext(), ShowPoemActivity.class);
        showPoem.putExtra(ShaktiApplication.KEY_CURSOR, index);
        showPoem.putExtra(ShaktiApplication.KEY_LANGUAGE, language.toString());
        startActivity(showPoem);
    }

    /**
     * underlines a given string.
     * @param text a string
     * @return an underlined text
     */
    private static SpannableString underline(final String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        return content;
    }

    void showWebpage(String title, String url) {
        Intent intent = new Intent(getApplicationContext(), LocalWebActivity.class);
        intent.putExtra(LocalWebActivity.KEY_URL, url);
        intent.putExtra(LocalWebActivity.KEY_TITLE, title);
        startActivity(intent);
    }
}
