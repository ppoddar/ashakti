package org.artisan.shakti;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
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
    }
}
