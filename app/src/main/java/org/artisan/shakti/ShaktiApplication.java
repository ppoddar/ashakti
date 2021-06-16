package org.artisan.shakti;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The application class holds immutable, global state.
 */
public class ShaktiApplication extends Application
         {
    private static List<String> englishPoems;
    private static List<String> bengaliPoems;
    private static List<String> audioFiles;
    private static Language language;
    private static Typeface bengaliFont;
    private static Typeface englishFont;

    public static final String KEY_CURSOR = "CURSOR";
    private static final String APP = ShaktiApplication.class.getSimpleName();

    /**
     * initializes this application.
     * The application holds an immutable list of content
     * in English and Bengali.
     * The list of file names is declared statically. This method reads
     * the file names, locates the file as a raw resource, reads
     * the file content and caches them.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        boolean initialized = (language != null);
        if (initialized) {
            Log.e(APP, "onCreate is not initializing. App has already been initialized");
            return;
        }
        Log.e(APP,"=========================================");
        Log.e(APP, "onCreate initializing...");
        Log.e(APP,"=========================================");

        language = Language.ENGLISH;

        englishPoems = createTOC(
                getResources().getString(R.string.content_english_path),
                getResources().getStringArray(R.array.english_poems_toc), true);
        bengaliPoems = createTOC(getResources().getString(R.string.content_bengali_path),
                getResources().getStringArray(R.array.bengali_poems_toc), true);
        audioFiles = createTOC(getResources().getString(R.string.audio_path),
                getResources().getStringArray(R.array.audio_files), false);
        if (englishPoems.size() != bengaliPoems.size()) {
            throw new IllegalArgumentException("english poems " + englishPoems.size() + " != bengali poems " + bengaliPoems.size());
        }

        bengaliFont = getResources().getFont(R.font.kalpurush);
        englishFont = getResources().getFont(R.font.raleway);
    }

    public Language getCurrentLanguage() {
        return language;
    }

    public ShaktiApplication setLanguage(Language lang) {
        this.language = lang;
        return this;
    }
    /**
     * Switches the language of the application.
     * @return the current language
     */
    public Language switchLanguage() {
        Language old = this.getCurrentLanguage();
        language = getCurrentLanguage() == Language.ENGLISH
                ? Language.BENGALI : Language.ENGLISH;
        Log.e(APP,"Switched language from " + old + " to " + language);
        return language;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * reads the content of each raw resource.
     * For empty names, a null entry is added to returned list
     *
     * @param dir         base directory of the content files
     * @param rsrcNames   names of the content files
     * @param readContent if true reads the content file, else just cache the name
     * @return a list that may contain null
     */
    List<String> createTOC(String dir, String[] rsrcNames, boolean readContent) {
        List<String> list = new ArrayList<>();
        for (String rsrcName : rsrcNames) {
            String fileName = dir + '/' + rsrcName;
            if (readContent) {
                try {
                    InputStream in = getAssets().open(fileName);
                    Log.e("SHAKTI", "open asset " + fileName);
                    list.add(readFileContent(in));
                } catch (Exception ex) {
                    Log.e("SHAKTI", "can not read asset " + fileName + " due to " + ex.getMessage());
                    list.add(null);
                }
            } else {
                if (rsrcName == null || rsrcName.isEmpty()) {
                    list.add(null);
                } else {
                    Log.e("SHAKTI", "add asset (not read) " + fileName);
                    list.add(fileName);
                }
            }
        }
        Log.e(APP, "Created " + list.size() + " poems/audio from assets at " + dir);
        return list;
    }

    /**
     * Reads the content of given input stream.
     *
     * @param fis input stream to read
     * @return content of the stream in UTF8
     * @throws Exception if error
     */
    String readFileContent(InputStream fis) throws Exception {
        StringBuilder buffer = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(fis, StandardCharsets.UTF_8));
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        return buffer.toString();
    }


    /**
     * gets the poem of given language at given cursor.
     *
     * @param cursor cursor
     * @return null if cursor out of range
     */
    public String getPoem(int cursor) {
        Log.e(APP, "requested poem at cursor " + cursor + " language " + getCurrentLanguage());
        if (!hasPoem(cursor)) {
            Log.e(APP, "requested poem at cursor " + cursor + " is out of range. There are " + getPoemCount() + " registered poems");
            return null;
        }
        switch (getCurrentLanguage()) {
            case ENGLISH:
                return englishPoems.get(cursor);
            case BENGALI:
                return bengaliPoems.get(cursor);
        }
        return null;
    }

    /**
     * gets the file descriptor of a audio file at given cursor.
     *
     * @param cursor a index
     * @return null if audio file name is empty
     */
    public FileDescriptor getAudioStream(int cursor) {
        String fileName = audioFiles.get(cursor);
        if (fileName == null) {
            Log.e(APP, "no audio file at cursor " + cursor);
            return null;
        }
        try {
            InputStream in = getAssets().open(fileName);
            if (in == null) {
                Log.e(APP, "no stream for audio file  " + fileName);
                return null;
            }
            Log.e(APP, "reading audio stream " + fileName);
            File tmpFile = File.createTempFile("audio", "mp3", null);
            FileOutputStream out = new FileOutputStream(tmpFile);
            int ch;
            while ((ch = in.read()) != -1) {
                out.write(ch);
            }
            in.close();
            out.close();

            FileInputStream fis = new FileInputStream(tmpFile);
            return fis.getFD();
        } catch (Exception ex) {
            Log.e(APP, "error reading audio stream " + fileName);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * get the font for given language
     *
     * @param lang language
     * @return a Typeface
     */
    public Typeface getFont(Language lang) {
        switch (lang) {
            case ENGLISH:
                return englishFont;
            case BENGALI:
                return bengaliFont;
        }
        return null;
    }

    public Typeface getFont() {
        return getFont(this.language);
    }

    public int getPoemCount() {
        return englishPoems.size();
    }

    public boolean hasPoem(int cursor) {
        return cursor >= 0 && cursor < getPoemCount();
    }

    public String getPoemTitle(Language lang, int index) {
        String[] toc = new String[0];
        if (lang == Language.BENGALI) {
            toc = getResources().getStringArray(R.array.bengali_poem_toc);
        } else {
            toc = getResources().getStringArray(R.array.english_poem_toc);
        }
        return toc[index];
    }
}
