package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import org.artisan.shakti.model.Poet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Superclass of fragment sets the toolbar and its action.
 */
public abstract class BaseFragment extends Fragment {

    public void onViewCreated(@NotNull View view, @Nullable Bundle state) {
        setToolbar(view);
    }

    /**
     * Toolbar is contributed typically by the activity. But here the fragment configures
     * the toolbar for easier layout.
     * However, the menu action handlers delegate the actions to the parent main activity.
     *
     * @param view a view of the fragment that declared the toolbar in its layout
     */
    @SuppressLint("NonConstantResourceId")
    void setToolbar(@NotNull View view) {
        ShaktiApplication app = (ShaktiApplication) requireActivity().getApplication();
        Toolbar toolbar = view.findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_show_poem);
        Language language = app.getCurrentLanguage();
        Poet poet = app.getModel().getPoet(language);
        toolbar.setTitle(poet.title);
        toolbar.setTitleTextAppearance(getActivity(),
                    language == Language.BANGLA
                    ? R.style.banglaPoemStyle : R.style.englishPoemStyle);


        toolbar.setOnMenuItemClickListener(item -> {
            EditText poem = requireView().findViewById(R.id.text_poem);
            MainActivity main = (MainActivity)getActivity();
            if (item.getItemId() == R.id.copy_text) {
                main.copyText(poem);
            } else if (item.getItemId() == R.id.share_text) {
                main.shareText(poem);
            } else if (item.getItemId() == R.id.action_table_of_content) {
                Intent intent = new Intent(main.getApplicationContext(), TOCActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.action_switch_language) {
                main.switchLanguage();
            } else if (item.getItemId() == R.id.action_biography) {
                main.showWebpage("Biography", "html/biography.html");
            } else if (item.getItemId() == R.id.action_about) {
                main.showWebpage("Notes", "html/notes.html");
            }
            return true;
        });
    }

}
