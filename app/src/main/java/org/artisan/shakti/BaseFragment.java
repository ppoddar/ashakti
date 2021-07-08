package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ui.StyledPlayerControlView;

import org.artisan.shakti.model.Model;
import org.artisan.shakti.model.Poem;
import org.artisan.shakti.model.TOCEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static android.view.View.INVISIBLE;

/**
 * Superclass of fragment sets the toolbar and its action.
 */
public class BaseFragment extends Fragment {

    public void onViewCreated(@NotNull View view, @Nullable Bundle state) {
        setToolbar(view);
    }

    @SuppressLint("NonConstantResourceId")
    void setToolbar(@NotNull View view) {
        ShaktiApplication app = (ShaktiApplication) requireActivity().getApplication();
        Toolbar toolbar = view.findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_show_poem);
        Language language = app.getCurrentLanguage();
        toolbar.setTitle(language == Language.BANGLA ? "শক্তি" : "Shakti");
        toolbar.setTitleTextAppearance(getActivity(),
                language == Language.BANGLA ? R.style.banglaFont : R.style.englishPoemStyle);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_table_of_content) {
                Intent intent = new Intent(requireActivity().getApplicationContext(), TOCActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.action_switch_language) {
                this.switchLanguage();
            } else if (item.getItemId() == R.id.action_biography) {
                showWebpage("Biography", "html/biography.html");
            } else if (item.getItemId() == R.id.action_about) {
                showWebpage("Notes", "html/notes.html");
            }
            return true;
        });
    }

    void switchLanguage() {
        ShaktiApplication app = (ShaktiApplication) requireActivity().getApplication();
        Language newLanguage = app.getCurrentLanguage() == Language.BANGLA
                ? Language.ENGLISH : Language.BANGLA;
        Log.e(this.toString(), "switchLanguage " + app.getCurrentLanguage() + "->" + newLanguage);
        ((MainActivity)getActivity()).switchToLanguage(newLanguage);
    }

    void showWebpage(String title, String url) {
        Intent intent = new Intent(requireActivity().getApplicationContext(),
                LocalWebActivity.class);
        intent.putExtra(LocalWebActivity.KEY_URL, url);
        intent.putExtra(LocalWebActivity.KEY_TITLE, title);
        startActivity(intent);
    }
}
