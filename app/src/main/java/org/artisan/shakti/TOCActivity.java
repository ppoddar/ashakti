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
        Log.e(ACTIVITY, "showing TOC in " + language);
        String[] toc = language == Language.ENGLISH
                ? getResources().getStringArray(R.array.english_poem_toc)
                : getResources().getStringArray(R.array.bengali_poem_toc);

        int index = 0;
        for (String title : toc) {
            addTOCEntry(app, title, index++);
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
     * @param title a title
     * @param index a index of the entry
     */
    void addTOCEntry(ShaktiApplication app, String title, final int index) {
        Log.e(ACTIVITY, "adding toc entry " + title);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.toc_entries);
        TextView entry = (TextView) inflater.inflate(R.layout.toc_entry_template, null);
        entry.setText(title);
        entry.setTypeface(app.getFont());

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,   // width
                ViewGroup.LayoutParams.WRAP_CONTENT);  // height

        insertPoint.addView(entry, params);

        // click on the entry will show the poem
        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(ACTIVITY, "clicked TOC entry " + index);
                Intent intent = new Intent(getApplicationContext(), ShowPoemActivity.class);
                intent.putExtra(ShaktiApplication.KEY_CURSOR, index);
                startActivity(intent);
            }
        });

    }


}
