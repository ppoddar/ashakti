package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity {
    private static final Language LANGUAGE_DEFAULT = Language.ENGLISH;
    private Language language;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            language = Language.valueOf(savedInstanceState.getString(
                    ShaktiApplication.KEY_LANGUAGE));
        } else {
            language = LANGUAGE_DEFAULT;
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String lang = bundle.getString(ShaktiApplication.KEY_LANGUAGE);
            if (lang != null) {
                language = Language.valueOf(lang.toUpperCase());
            } else {
                language = LANGUAGE_DEFAULT;
            }
        }
        setToolbar();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ShaktiApplication.KEY_LANGUAGE, language.toString());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        RelativeLayout layout = findViewById(R.id.main_activity_layout);
        layout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeLeft() {
                showPoem();
            }
        });
        ImageButton enter = findViewById(R.id.action_start_app);
        enter.setOnClickListener(v -> showPoem());
    }

    @SuppressLint("NonConstantResourceId")
    void setToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setTitle(language == Language.BANGLA ? "শক্তি" : "Shakti");
        toolbar.setTitleTextAppearance(this,
                language == Language.BANGLA ? R.style.banglaFont : R.style.englishPoemStyle);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_table_of_content:
                    Intent intent = new Intent(getApplicationContext(), TOCActivity.class);
                    startActivity(intent);
                    break;
                case R.id.action_switch_language:
                    switchLanguage();
                    break;
                case R.id.action_biography:
                    showWebpage("Biography", "html/biography.html");
                    break;
                case R.id.action_about:
                    showWebpage("Notes", "html/notes.html");
                    break;
            }
            return true;
        });
    }

    private void switchLanguage() {
        language = (language == Language.BANGLA) ? Language.ENGLISH : Language.BANGLA;
        updateUI();
    }

    private void updateUI() {
        TextView poetName = findViewById(R.id.label_poet_name);
        TextView poetLife = findViewById(R.id.label_poet_life);
        int style = language == Language.BANGLA
                ? R.style.banglaTitleStyle
                : R.style.englishPoemStyle;
        poetName.setTextAppearance(style);
        poetLife.setTextAppearance(style);
        poetName.setText(language == Language.BANGLA ? "শক্তি চট্টোপাধ্যায়" : "Shakti Chattopadhayay");
        poetLife.setText(language == Language.BANGLA ? "১৯৩৩-৯৫" : "1933-95");
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(language == Language.BANGLA ? "শক্তি" : "Shakti");
            toolbar.setTitleTextAppearance(this, style);
        }
    }
    /**
     * Shows the poem at current cursor and language
     * via ShowPoemActivity.
     */
    void showPoem() {
        Intent intent = new Intent(getApplicationContext(), ShowPoemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ShaktiApplication.KEY_CURSOR, 0);
        bundle.putString(ShaktiApplication.KEY_LANGUAGE, language.toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    void showWebpage(String title, String url) {
        Intent intent = new Intent(getApplicationContext(), LocalWebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(LocalWebActivity.KEY_URL, url);
        bundle.putString(LocalWebActivity.KEY_TITLE, title);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}