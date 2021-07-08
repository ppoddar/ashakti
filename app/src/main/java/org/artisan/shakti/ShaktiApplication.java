package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.artisan.shakti.model.Model;
import org.artisan.shakti.model.ModelBuilder;

import java.io.InputStream;

/**
 * The application class holds a navigable {@link Model model}.
 * The model is accessible statically.
 */
public class ShaktiApplication extends Application {
    public static final String KEY_CURSOR   = "CURSOR";
    public static final String KEY_LANGUAGE = "LANGUAGE";
    public static final Language LANGUAGE_DEFAULT = Language.ENGLISH;
    public static final String  TAG = "App";

    private Context context;
    private Model model;
    private Language language;
    private static final String APP = ShaktiApplication.class.getSimpleName();

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        language = LANGUAGE_DEFAULT;
        Log.e(APP, "=========================================");
        Log.e(APP, "onCreate initializing...");
        Log.e(APP, "=========================================");
        try {
            model = getModel();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
   }

   public Context getContext() {
        return context;
   }


   public Model getModel()  {
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

   public Language getCurrentLanguage() {
        return language;
   }


    public void setLanguage(Language lang) {
        Log.e(TAG, "setToLanguage " + lang);
        language = lang;
    }
}
