package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Main Activity is a ViewPager. The pages viewed are {@link PoemFragment}
 * via a pair of {@link PoemViewAdapter adapters}, each adapter serves pages
 * for one particular language.
 */
public class MainActivity extends FragmentActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_SEND_VIA_WHATSAPP = 123;
    private ViewPager2 pager;
    private PoemViewAdapter englishAdapter;
    private PoemViewAdapter banglaAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShaktiApplication app = (ShaktiApplication) getApplication();
        Language lang = findLanguage(savedInstanceState);
        app.setLanguage(lang == null ? ShaktiApplication.LANGUAGE_DEFAULT : lang);
        banglaAdapter = new PoemViewAdapter(this);
        banglaAdapter.init(app, Language.BANGLA);
        englishAdapter = new PoemViewAdapter(this);
        englishAdapter.init(app, Language.ENGLISH);
        pager = findViewById(R.id.pager);

        pager.setAdapter(app.getCurrentLanguage() == Language.BANGLA
                ? banglaAdapter : englishAdapter);
        pager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        pager.setEnabled(true);

        pager.setPageTransformer((page, position) -> {

        });
        pager.setCurrentItem(findCursor(savedInstanceState));
        pager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback cb) {
        return super.startActionMode(cb);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }


    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ShaktiApplication app = ((ShaktiApplication) getApplication());
        outState.putInt(ShaktiApplication.KEY_CURSOR, pager.getCurrentItem());
        outState.putInt(ShaktiApplication.KEY_LANGUAGE, app.getCurrentLanguage().ordinal());
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        Log.e(TAG, "onCreateContextMenu() info:" + info);
        getMenuInflater().inflate(R.menu.menu_text_selection, menu);
        //openContextMenu(v);
    }

    public void copyText(TextView poem) {
        String text = getSelectedText(poem);
        Log.e(TAG, "onContextItemSelected() selected text [" + text + "]");
        copyToClipboard(text);
    }

    public void shareText(TextView poem) {
        String text = getSelectedText(poem);
        Log.e(TAG, "onContextItemSelected() clip  [" + text + "]");
        sendViaWhatsapp(text);
    }
    String getSelectedText(TextView poem) {
        int startOffset = poem.getSelectionEnd();
        int endOffset = poem.getSelectionStart();
        String selectedText = (endOffset > startOffset)
                ? poem.getText().toString().substring(startOffset, endOffset)
                : poem.getText().toString().substring(endOffset, startOffset);
        Log.e(TAG, "getSelectedText() " + selectedText);
        return selectedText;
    }

    void copyToClipboard(String text) {
        Log.e(TAG, "copyToClipboard() " + text);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("primary", text);
        clipboard.setPrimaryClip(clip);
    }

    String getClip() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData primary = clipboard.getPrimaryClip();
        return primary.getItemAt(0).getText().toString();
    }

    @SuppressWarnings("deprecation")
    void sendViaWhatsapp(String text) {
        Log.e(TAG, "sendViaWhatsapp() " + text);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
        startActivityForResult(sendIntent, REQUEST_SEND_VIA_WHATSAPP);
    }

    /**
     * Changes the current language to a new language
     * @return new language
     */
    public Language switchLanguage() {
        ShaktiApplication app = ((ShaktiApplication) getApplication());
        Language language = app.getCurrentLanguage();
        Log.e(TAG, "switchLanguage " + language);
        int index = pager.getCurrentItem();
        Language newLanguage = language == Language.BANGLA ? Language.ENGLISH : Language.BANGLA;
        app.setLanguage(newLanguage);
        pager.setAdapter(newLanguage == Language.BANGLA ? banglaAdapter : englishAdapter);
        pager.setCurrentItem(index);
        return newLanguage;
    }



    /**
     * finds the language from the given bundle
     * @param savedInstanceState a bundle
     * @return a language. DEFAULT_LANGUAGE if bundle does not have the given key
     */
    private Language findLanguage(@Nullable Bundle savedInstanceState) {
        return Language.values()[findIntKey(savedInstanceState, ShaktiApplication.KEY_LANGUAGE)];
    }

    private int findCursor(Bundle savedInstanceState) {
        return findIntKey(savedInstanceState, ShaktiApplication.KEY_CURSOR);
    }

    /**
     * finds integer value for given key from the given bundle
     * @param savedInstanceState a bundle
     * @param key a key
     * @return an integer value. 0 if bundle does not have the given key
     */
    private int findIntKey(Bundle savedInstanceState, String key) {
        int value = 0;
        if (savedInstanceState != null) {
            value = savedInstanceState.getInt(key);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            value = bundle.getInt(key);
        }
        return value;
    }

    void showWebpage(String title, String url) {
        Intent intent = new Intent(getApplicationContext(), LocalWebActivity.class);
        intent.putExtra(LocalWebActivity.KEY_URL, url);
        intent.putExtra(LocalWebActivity.KEY_TITLE, title);
        startActivity(intent);
    }
}