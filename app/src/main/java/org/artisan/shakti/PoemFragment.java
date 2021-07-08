package org.artisan.shakti;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.StyledPlayerControlView;

import org.artisan.shakti.model.TOCEntry;
import org.jetbrains.annotations.NotNull;

public class PoemFragment extends BaseFragment {
    public static final String ACTIVITY = PoemFragment.class.getSimpleName();
    AudioPlayer player;
    private CharSequence text;
    private Typeface typeface;
    private String audio;
    private TOCEntry entry;

    public void setText(@NotNull CharSequence str) {
        this.text = str;
    }

    public void setFont(@NotNull Typeface font) {
        this.typeface = font;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceCache) {
        // IMPORTANT: the third argument must be false
        Log.e("PoemFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_poem, container, false);
        return view;
    }

    public void onResume() {
        super.onResume();
        Log.e("PoemFragment", "-------------- onResume ----------------");
        StyledPlayerControlView audioControl = requireView().findViewById(R.id.audio_player);
        if (audio == null) {
            audioControl.setVisibility(View.GONE);
        } else {
            audioControl.setVisibility(View.VISIBLE);
            player = new AudioPlayer(requireActivity(), audioControl, audio);
        }

        requireView().findViewById(R.id.navigate_next).setOnHoverListener(
                new View.OnHoverListener() {

                    @Override
                    public boolean onHover(View v, MotionEvent event) {
                        v.setVisibility(View.VISIBLE);
                        return true;
                    }
                });


    }

    public void onPause() {
        super.onPause();
        Log.e("PoemFragment", "-------------- paused ----------------");
        if (player != null) player.release();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceCache) {
        super.onViewCreated(view, savedInstanceCache); // will set toolbar
        TextView poem = view.findViewById(R.id.text_poem);
        poem.setText(text);
        poem.setTypeface(typeface);
        poem.setMovementMethod(new ScrollingMovementMethod());

        poem.setOnLongClickListener(v -> {
            ((TextView)v).setCursorVisible(true);
            return true;
        });

        poem.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.removeItem(android.R.id.replaceText);
                menu.removeItem(android.R.id.cut);
                menu.removeItem(android.R.id.copy);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false; // menu not modified
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == android.R.id.copy) {
                    item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            TextView poem = PoemFragment.this.getView().findViewById(R.id.text_poem);
                            int startOffset = poem.getSelectionEnd();
                            int endOffset = poem.getSelectionStart();
                            String selectedText = (endOffset > startOffset)
                                    ? poem.getText().toString().substring(startOffset, endOffset)
                                    : poem.getText().toString().substring(endOffset, startOffset);
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, selectedText);
                            sendIntent.setType("text/plain");
                            sendIntent.setPackage("com.whatsapp");
                            startActivity(sendIntent);
                            return true;
                        }
                    });
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }


    class TextSelectionCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuItem copy  = menu.add("Copy");
            MenuItem share = menu.add("Share");
            share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    TextView poem = PoemFragment.this.getView().findViewById(R.id.text_poem);
                    int startOffset = poem.getSelectionEnd();
                    int endOffset = poem.getSelectionStart();
                    String selectedText = (endOffset > startOffset)
                            ? poem.getText().toString().substring(startOffset, endOffset)
                            : poem.getText().toString().substring(endOffset, startOffset);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, selectedText);
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                    return true;
                }
            });
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }


}
