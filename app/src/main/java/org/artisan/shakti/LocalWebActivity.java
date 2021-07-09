package org.artisan.shakti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class LocalWebActivity extends AppCompatActivity {
    public static final String KEY_URL      = "URL";
    public static final String KEY_TITLE    = "TITLE";
    private static final String BASE_URL    = "file:///android_asset";
    private static final String DEFAULT_URL = "html/index.html";

    private WebView localWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_web);

        setToolbar();
        setWebClient();

    }

    void setToolbar() {
        Toolbar toolbar = new Toolbar(this);
        toolbar.inflateMenu(R.menu.menu_web);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                showHome();
            }
            return true;
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    void setWebClient() {
        localWeb = findViewById(R.id.local_web);
        localWeb.getSettings().setJavaScriptEnabled(true);
        localWeb.setWebChromeClient(new WebChromeClient());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String title = getIntent().getStringExtra(KEY_TITLE);
        String url = getIntent().getStringExtra(KEY_URL);
        if (url == null)  url = DEFAULT_URL;
        if (title != null && getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(title);
        }
        localWeb.loadUrl(BASE_URL + '/' + url);
    }

    /**
     * Starts MainActivity.
     */
    void showHome() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void showPage(Context ctx, String url) {
        WebView web = new WebView(ctx);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebChromeClient(new WebChromeClient());
        web.loadUrl(BASE_URL + '/' + url);
    }

}