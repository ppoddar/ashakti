package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TOCActivity extends AppCompatActivity {
    private static final String ACTIVITY = TOCActivity.class.getSimpleName();

    static SpannableString underline(final String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        return content;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toc);
        setToolbar();
        ShaktiApplication app = (ShaktiApplication) getApplication();
        Language language = app.getCurrentLanguage();
        Log.e(ACTIVITY, "showing TOC");
        for (int i = 0; i < app.getPoemCount(); i++) {
            addTOCEntry(app,
                    app.getPoemTitle(Language.ENGLISH, i),
                    app.getPoemTitle(Language.BANGLA, i), i);
        }
    }

    @SuppressLint("NonConstantResourceId")
    void setToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.findViewById(R.id.action_switch_language).setVisibility(View.GONE);
        toolbar.findViewById(R.id.action_table_of_content).setVisibility(View.GONE);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        showHomeScreen();
                        break;
                }
                return true; // the click is handled here itself
            }
        });
    }

    void showHomeScreen() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }

    /**
     * adds a TOC entry.
     *
     * @param app    a context
     * @param title1 a title
     * @param title2 another title
     * @param index  a index of the entry
     */
    void addTOCEntry(final ShaktiApplication app,
                     String title1, String title2,
                     final int index) {
        Log.e(ACTIVITY, "adding toc entry " + title1 + " (" + title2 + ")");
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup entries = findViewById(R.id.toc_entries);
        ViewGroup template = (ViewGroup) inflater.inflate(R.layout.toc_entry_template, null);
        TextView englishTitle = template.findViewById(R.id.english_title);
        TextView banglaTitle = template.findViewById(R.id.bangla_title);

        englishTitle.setText(underline(title1));
        banglaTitle.setText(underline(title2));
        banglaTitle.setTypeface(app.getFont(Language.BANGLA));
        englishTitle.setTypeface(app.getFont(Language.ENGLISH));

        entries.addView(template);
        // click on the entry will show the poem
        englishTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e(ACTIVITY, "clicked TOC entry " + index);
                app.setLanguage(Language.ENGLISH);
                showPoem(index);
            }
        });

        banglaTitle.setOnClickListener(v -> {
            //Log.e(ACTIVITY, "clicked TOC entry " + index);
            app.setLanguage(Language.BANGLA);
            showPoem(index);
        });

    }

    void showPoem(int index) {
        Intent intent = new Intent(getApplicationContext(), ShowPoemActivity.class);
        intent.putExtra(ShaktiApplication.KEY_CURSOR, index);
        startActivity(intent);
    }


}
