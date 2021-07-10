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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.MenuRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.exoplayer2.ui.StyledPlayerControlView;

import org.artisan.shakti.model.Poem;
import org.artisan.shakti.model.Poet;
import org.artisan.shakti.model.TOCEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Main Activity uses a ViewPager. The pages viewed are {@link PoemFragment}
 * via a pair of {@link PoemViewAdapter adapters}, each adapter serves pages
 * for one particular language.
 */
public class MainActivity extends FragmentActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_SEND_VIA_WHATSAPP = 123;
    private ViewPager2 pager;
    private PoemViewAdapter[] adapters;
    private AudioPlayer audioPlayer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar(R.menu.menu_main);

        ShaktiApplication app = (ShaktiApplication) getApplication();
        Language lang = findLanguage(savedInstanceState);
        app.setLanguage(lang == null ? ShaktiApplication.LANGUAGE_DEFAULT : lang);
        adapters = new PoemViewAdapter[]{
                new PoemViewAdapter(this, app, Language.ENGLISH),
                new PoemViewAdapter(this, app, Language.BANGLA),
        };
        pager = findViewById(R.id.pager);
        PoemViewAdapter adapter = adapters[lang.ordinal()];
        pager.setAdapter(adapter);
        pager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        pager.setEnabled(true);

        pager.setPageTransformer(new ZoomOutPageTransformer());
        int position = findCursor(savedInstanceState);
        pager.setCurrentItem(position);
        adapter.notifyDataSetChanged();
    }

    /**
     * Listens to pager change and adjusts UI accordingly
     */
    @Override
    public void onResume() {
        super.onResume();

        final ShaktiApplication app = (ShaktiApplication)getApplication();
        int position = findCursor(getIntent().getExtras());
        pager.setCurrentItem(position);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "--------> page " + position + " selected");
                clearNavigation();
                updateToolbar(app);
                if (position == 0) { // front page
                    setAudioPlayer(null);
                } else if (position == pager.getAdapter().getItemCount()-1) { // last page
                    setAudioPlayer(null);
                } else {
                    TOCEntry entry = app.getModel().getEntry(position-1);
                    setNavigation(entry);
                    setAudioPlayer(entry.audio);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) {
            audioPlayer.release();
            audioPlayer = null;
        }
    }

    // ---------------------------------------------------
    // Audio Player
    // ---------------------------------------------------

    /**
     * Makes the audio control visible and sets their action handler.
     * Creates an Audio player to play the audio.
     * If the audio is null, makes the control invisible
     * @param audio an audio
     */
    public void setAudioPlayer(String audio) {
        StyledPlayerControlView audioControl = findViewById(R.id.audio_player);
        if (audio == null) {
            audioControl.setVisibility(View.INVISIBLE);
        } else {
            audioControl.setVisibility(View.VISIBLE);
            audioPlayer = AudioPlayer.create(this, audioControl, audio);
            audioControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    audioPlayer.play();
                }
            });
        }
    }
    // ----------------------------------------------
    //
    // ----------------------------------------------

    /**
     * Sets navigation buttons visible and sets their action handlers
     * @param entry
     */
    public void setNavigation(TOCEntry entry) {
        ImageButton next = findViewById(R.id.navigate_next);
        ImageButton prev = findViewById(R.id.navigate_prev);
        next.setVisibility(View.VISIBLE);
        prev.setVisibility(View.VISIBLE);
        next.setOnClickListener(v -> pager.setCurrentItem(pager.getCurrentItem() + 1));
        prev.setOnClickListener(v -> pager.setCurrentItem(pager.getCurrentItem() - 1));
    }

    /**
     * makes navigation buttons invisible
     */
    public void clearNavigation() {
        ImageButton next = findViewById(R.id.navigate_next);
        ImageButton prev = findViewById(R.id.navigate_prev);
        next.setVisibility(View.INVISIBLE);
        prev.setVisibility(View.INVISIBLE);
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

    // ---------------------------------------------------------
    // Selected Text Handing
    // ---------------------------------------------------------

    /**
     * Copies selected text in the given view to clipboard
     * @param poem a text view containing the poem text
     */
    public void copyText(TextView poem) {
        String text = getSelectedText(poem);
        Log.e(TAG, "onContextItemSelected() selected text [" + text + "]");
        copyToClipboard(text);
    }
    /**
     * Shares selected text in the given view to WhatsApp
     * @param poem a text view containing the poem text
     */
    public void shareText(TextView poem) {
        String text = getSelectedText(poem);
        Log.e(TAG, "onContextItemSelected() shareText  [" + text + "]");
        sendViaWhatsapp(text);
    }
    /**
     * Gets selected text in the given view
     * @param poem a text view containing the poem text
     */
    String getSelectedText(TextView poem) {
        int startOffset = poem.getSelectionEnd();
        int endOffset = poem.getSelectionStart();
        String selectedText = (endOffset > startOffset)
                ? poem.getText().toString().substring(startOffset, endOffset)
                : poem.getText().toString().substring(endOffset, startOffset);
        Log.e(TAG, "getSelectedText() " + selectedText);
        return selectedText;
    }

    /**
     * Copies the given text to clipboard as primary clip
     * @param text a text
     */
    void copyToClipboard(String text) {
        Log.e(TAG, "copyToClipboard() " + text);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("primary", text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Gets the primary cip from the clipboard
     * @return a text
     */
    String getClip() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData primary = clipboard.getPrimaryClip();
        return primary.getItemAt(0).getText().toString();
    }

    /**
     * Sends given text to WhatsApp
     * @param text
     */
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
     * Changes the current language to a new language. The subsequent
     * pages will be shown in switched language.
     * The adapter will be changed and same page index is shown again.
     */
    public void switchLanguage() {
        ShaktiApplication app = ((ShaktiApplication) getApplication());
        Language language = app.getCurrentLanguage();
        Language newLanguage = language == Language.BANGLA ? Language.ENGLISH : Language.BANGLA;
        Log.e(TAG, "switchLanguage() " + language + " to " + newLanguage);
        int index = pager.getCurrentItem();
        app.setLanguage(newLanguage);
        PoemViewAdapter adapter = adapters[newLanguage.ordinal()];
        pager.setAdapter(adapter);
        pager.setCurrentItem(index);
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

    /**
     * configures the toolbar.
     * the menu action are handled.
     *
     */
    @SuppressLint("NonConstantResourceId")
    void setToolbar(@MenuRes int menuRsrc) {
        ShaktiApplication app = (ShaktiApplication) getApplication();
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.inflateMenu(menuRsrc);
        updateToolbar(app);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.copy_text) {
                View page = pager.getFocusedChild();
                TextView poem = page.findViewById(R.id.text_poem);
                if (poem != null) copyText(poem);
            } else if (item.getItemId() == R.id.share_text) {
                View page = pager.getFocusedChild();
                TextView poem = page.findViewById(R.id.text_poem);
                if (poem != null)shareText(poem);
            } else if (item.getItemId() == R.id.action_table_of_content) {
                Intent intent = new Intent(getApplicationContext(), TOCActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (item.getItemId() == R.id.action_switch_language) {
                switchLanguage();
            } else if (item.getItemId() == R.id.action_biography) {
                showWebpage("Biography", "html/biography.html");
            } else if (item.getItemId() == R.id.action_about) {
                showWebpage("Notes", "html/notes.html");
            }
            return true;
        });
    }

    /**
     * Updates toolbar title according to the current language of the app
     * @param app a application
     */
    void updateToolbar(ShaktiApplication app) {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        Language language = app.getCurrentLanguage();
        Poet poet = app.getModel().getPoet(language);
        toolbar.setTitle(poet.title);
        toolbar.setTitleTextAppearance(this,
                language == Language.BANGLA
                        ? R.style.banglaPoemStyle : R.style.englishPoemStyle);


    }
}