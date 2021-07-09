package org.artisan.shakti;

import android.graphics.Typeface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.artisan.shakti.model.Poet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrontPage extends BaseFragment {
    Language language;
    Poet     poet;

    public void init(Poet poet, Language language) {
        this.poet = poet;
        this.language = language;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_front_page, container, false);
    }

    /**
     * Populates the current view with name and lifetime of the poet
     * in current language.
     * @param view the view to populate. The same view returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @param state the saved state
     */
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle state)  {
        super.onViewCreated(view, state);
        ShaktiApplication app = (ShaktiApplication) requireActivity().getApplication();
        Typeface font = app.getModel().getFont(language);
        Poet poet     = app.getModel().getPoet(language);
        TextView name = view.findViewById(R.id.label_poet_name);
        TextView lifetime = view.findViewById(R.id.label_poet_life);
        name.setText(poet.name);
        lifetime.setText(poet.lifetime);
        name.setTypeface(font);
        lifetime.setTypeface(font);

    }

    @NotNull
    public String toString() {
        return "FrontPage";
    }
}