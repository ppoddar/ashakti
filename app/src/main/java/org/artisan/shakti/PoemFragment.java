package org.artisan.shakti;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class PoemFragment extends Fragment {
    private CharSequence text;
    private Typeface typeface;

    public void setText(@NotNull CharSequence str) {
        this.text = str;
    }

    public void setLanguage(@NotNull Typeface font) {
        this.typeface = font;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceCache) {
        // IMPORTANT: the third argument must be false
        View view = inflater.inflate(R.layout.fragment_poem, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceCache) {
        TextView poem = view.findViewById(R.id.text_poem);
        poem.setText(text);
        poem.setTypeface(typeface);
        poem.setMovementMethod(new ScrollingMovementMethod());

        poem.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                return false;
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
        });
    }


    class TextSelectionCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuItem copy = menu.add("Copy");
            MenuItem share = menu.add("Share");
            share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    TextView poem = PoemFragment.this.getView().findViewById(R.id.text_poem);
                    int startOffset = poem.getSelectionEnd();
                    int endOffset   =  poem.getSelectionStart();
                    String selectedText = (endOffset>startOffset)
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
