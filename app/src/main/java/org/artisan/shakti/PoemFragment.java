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

import com.google.android.exoplayer2.ui.StyledPlayerControlView;

import org.artisan.shakti.model.Poem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This fragment displays a poem in a particular language.
 */
public class PoemFragment extends BaseFragment {
    private static final String TAG = PoemFragment.class.getSimpleName();
    AudioPlayer audioPlayer;
    private Poem poem;
    private Typeface font;
    private String audio;

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

    /**
     * Creates and attaches an audio player if the associated entry has audio.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d("PoemFragment", "onResume " + poem);
        StyledPlayerControlView audioControl = requireView().findViewById(R.id.audio_player);
        if (audio == null) {
            audioControl.setVisibility(View.GONE);
        } else {
            audioControl.setVisibility(View.VISIBLE);
            audioPlayer = AudioPlayer.create(requireActivity(), audioControl, audio);
            audioControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    audioPlayer.play();
                }
            });
        }

        requireView().findViewById(R.id.navigate_next).setOnHoverListener(
                (v, event) -> {
                    v.setVisibility(View.VISIBLE);
                    return true;
                });
    }

    /**
     * This fragment pauses when becomes invisible. The associated
     * audio player, if any is released .
     */
    public void onPause() {
        Log.e("PoemFragment", "onPause() " + poem);
        super.onPause();
        if (audioPlayer != null) {
            audioPlayer.release();
            audioPlayer = null;
        }
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
        super.onViewCreated(view, savedInstanceCache); // will set toolbar
        EditText poem = view.findViewById(R.id.text_poem);
        Spanned html = Html.fromHtml(this.poem.content, Html.FROM_HTML_MODE_COMPACT);
        poem.setText(html);
        poem.setTypeface(font);
        poem.setMovementMethod(new ScrollingMovementMethod());
        poem.setTextIsSelectable(true); // IMPORTANT
        poem.setFocusable(true);
        poem.setFocusableInTouchMode(true);

        this.requireActivity().registerForContextMenu(poem);
    }
}

