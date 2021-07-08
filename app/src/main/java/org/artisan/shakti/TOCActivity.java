package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.artisan.shakti.model.Model;
import org.artisan.shakti.model.TOCEntry;

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
        setContentView(R.layout.activity_toc);
        setToolbar();

        ShaktiApplication app = (ShaktiApplication)getApplication();

        ListView tocEntries = findViewById(R.id.toc_entries);
        TOCAdapter adapter = new TOCAdapter(app.getModel(), tocEntries);
        tocEntries.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
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

    class TOCAdapter extends BaseAdapter {
        final Model model;
        ListView list;
        TOCAdapter(Model model, ListView list) {
            this.model = model;
            this.list  = list;
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
            TextView englishTitle = convertView.findViewById(R.id.english_title);
            TextView banglaTitle  = convertView.findViewById(R.id.bangla_title);
            englishTitle.setOnClickListener((v)->{
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(ShaktiApplication.KEY_CURSOR, position+1);// 0-th page is front page
                intent.putExtra(ShaktiApplication.KEY_LANGUAGE, Language.ENGLISH.ordinal());// 0-th page is front page
                startActivity(intent);
            });
            banglaTitle.setOnClickListener((v)->{
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(ShaktiApplication.KEY_CURSOR, position+1);// 0-th page is front page
                intent.putExtra(ShaktiApplication.KEY_LANGUAGE, Language.BANGLA.ordinal());// 0-th page is front page
                startActivity(intent);
            });

            TOCEntry entry = (TOCEntry)getItem(position);
            englishTitle.setText(entry.english.title);
            banglaTitle.setText(entry.bangla.title);
            englishTitle.setTypeface(model.getFont(Language.ENGLISH));
            banglaTitle.setTypeface(model.getFont(Language.BANGLA));
            if (entry.audio != null) {
                convertView.findViewById(R.id.action_play_audio).setVisibility(View.VISIBLE);
            } else {
                convertView.findViewById(R.id.action_play_audio).setVisibility(View.GONE);
            }

            return convertView;        }
    }
}
