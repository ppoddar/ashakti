package org.artisan.shakti;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.artisan.shakti.model.Poet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrontPage extends BaseFragment {
    
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
        Language language = app.getCurrentLanguage();
        TextView name = view.findViewById(R.id.label_poet_name);
        TextView life = view.findViewById(R.id.label_poet_life);
        Poet poet = app.getModel().poet;
        name.setText(language == Language.BANGLA ? poet.banglaName : poet.englishName);
        life.setText(language == Language.BANGLA ? poet.banglaLifetime : poet.englishLifetime);
    }

    @NotNull
    public String toString() {
        return "FrontPage";
    }
}