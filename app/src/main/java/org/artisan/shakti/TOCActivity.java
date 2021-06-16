package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TOCActivity extends AppCompatActivity {
    private static final String ACTIVITY = TOCActivity.class.getSimpleName();

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
        setSupportActionBar(toolbar);
    }

    /**
     * adds a TOC entry.
     *
     * @param app   a context
     * @param title1 a title
     * @param title2 another title
     * @param index a index of the entry
     */
    void addTOCEntry(final ShaktiApplication app,
                     String title1, String title2,
                     final int index) {
        Log.e(ACTIVITY, "adding toc entry " + title1 + " (" + title2 + ")");
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup entries = (ViewGroup) findViewById(R.id.toc_entries);
        ViewGroup template = (ViewGroup)inflater.inflate(R.layout.toc_entry_template, null);
        TextView englishTitle = (TextView)template.findViewById(R.id.english_title) ;
        TextView bengaliTitle = (TextView)template.findViewById(R.id.bangla_title) ;
        englishTitle.setText(title1);
        bengaliTitle.setText(title2);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,   // width
                ViewGroup.LayoutParams.WRAP_CONTENT);  // height

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

        bengaliTitle.setOnClickListener(v -> {
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
