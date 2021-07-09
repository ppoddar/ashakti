package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.artisan.shakti.model.Model;
import org.artisan.shakti.model.ModelBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

/**
 * This application presents a set of poems (and their English translation)
 * by Bengali poet Shakti Chattopadhyay.
 * The application on Android platform is mostly based on a series of pages
 * presented as {@link androidx.fragment.app.Fragment fragment} and a
 * {@link }ViewPager2 pager} that sweeps these pages using a {@link PoemViewAdapter
 * adapter}.
 * <p>
 * The underlying (@link {@link Model content model} has a set of {@link org.artisan.shakti.model.Poem Poem},
 * {@link org.artisan.shakti.model.Poet Poet} and {@link org.artisan.shakti.model.TOCEntry
 * entries}. A poem occurs in two languages. It may have a audio rendition.
 * </p>
 */
public class ShaktiApplication extends Application {
    public static final String KEY_CURSOR   = "CURSOR";
    public static final String KEY_LANGUAGE = "LANGUAGE";
    public static final Language LANGUAGE_DEFAULT = Language.ENGLISH;
    public static final String  TAG = "App";

    private Model model;
    private Language language;
    private static final String APP = ShaktiApplication.class.getSimpleName();

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate() {
        super.onCreate();
        language = LANGUAGE_DEFAULT;
        Log.e(APP, "=========================================");
        Log.e(APP, "onCreate() initializing...");
        Log.e(APP, "=========================================");
        try {
            model = getModel();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
   }

    /**
     * Gets the content model, creating from an asset if necessary.
     * @return a model
     */
   public @NotNull Model getModel()  {
        if (model == null) {
            try {
                InputStream config = getAssets().open("shakti.json");
                model = ModelBuilder.build(config, this);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return model;
   }

    /**
     * The current language for all the pages.
     * @return a language
     * @see #setLanguage(Language)
     */
   public Language getCurrentLanguage() {
        return language;
   }

    /**
     * Changes the language.
     * @param lang a language
     */
    public void setLanguage(Language lang) {
        Log.e(TAG, "setToLanguage " + lang);
        language = lang;
    }
}
