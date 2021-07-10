package org.artisan.shakti;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ui.StyledPlayerControlView;

import org.artisan.shakti.model.Poem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This fragment displays a poem in a particular language.
 */
public class PoemFragment extends Fragment {
    private Poem poem;
    private Typeface font;
    private String audio;
    private static final String TAG = PoemFragment.class.getSimpleName();

    /**
     * Initialize the fragment
     * @param p the poem to render
     * @param f the font used
     * @param a audio if any
     */
    public void init(@NotNull Poem p, @NotNull Typeface f, @Nullable String a) {
        poem = p;
        font = f;
        audio = a;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceCache) {
        // IMPORTANT: the third argument must be false
        Log.d("PoemFragment", "onCreateView");
        return inflater.inflate(R.layout.fragment_poem, container, false);
    }


    //                     Contextual Menu handling
    // https://developer.android.com/guide/topics/ui/menus#context-menu

    /**
     * The view is created by an HTML version of the poem as main content
     * in given font.
     *
     * @param view               the view returned from {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @param savedInstanceCache state
     */
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceCache) {
        super.onViewCreated(view, savedInstanceCache);
        EditText poem = view.findViewById(R.id.text_poem);
        Spanned html = Html.fromHtml(this.poem.content, Html.FROM_HTML_MODE_COMPACT);
        poem.setText(html);
        poem.setTypeface(font);
        poem.setMovementMethod(new ScrollingMovementMethod());
        poem.setTextIsSelectable(true); // IMPORTANT
        poem.setFocusable(true);
        poem.setFocusableInTouchMode(true);

        //this.requireActivity().registerForContextMenu(poem);
    }

}

