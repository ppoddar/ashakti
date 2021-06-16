package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * This activity displays content in two languages.
 * The list of content is available as HTML files.
 * The file names are statically declared.
 */
public class MainActivity extends AppCompatActivity {
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();

//        LinearLayout layout = findViewById(R.id.main_activity_layout);
//        layout.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(),
//                    ShowPoemActivity.class);
//            startActivity(intent);
//        });
    }

    @SuppressLint("NonConstantResourceId")
    void setToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.minimum_menu);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_table_of_content) {
                Intent intent = new Intent(getApplicationContext(), TOCActivity.class);
                startActivity(intent);
            }
            return true;
        });
    }
}