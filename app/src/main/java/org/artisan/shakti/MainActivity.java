package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity {
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();
        RelativeLayout layout = findViewById(R.id.main_activity_layout);
        layout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeLeft() {
                showDefaultPoem();
            }
        });
        ImageButton enter = findViewById(R.id.action_start_app);
        enter.setOnClickListener(v -> showDefaultPoem());
    }

    void setToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.findViewById(R.id.action_home).setVisibility(View.GONE);
        toolbar.findViewById(R.id.action_switch_language).setVisibility(View.GONE);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_table_of_content) {
                Intent intent = new Intent(getApplicationContext(), TOCActivity.class);
                startActivity(intent);
            }
            return true;
        });
    }

    /**
     * Shows the default poem in default language
     * via {@link ShowPoemActivity}.
     */
    void showDefaultPoem() {
        Intent intent = new Intent(getApplicationContext(), ShowPoemActivity.class);
        intent.putExtra(ShaktiApplication.KEY_CURSOR, ShowPoemActivity.DEFAULT_CURSOR);
        intent.putExtra(ShaktiApplication.KEY_LANGUAGE, ShowPoemActivity.DEFAULT_LANGUAGE);
        startActivity(intent);
    }
}