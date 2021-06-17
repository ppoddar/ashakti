package org.artisan.shakti;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
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
 * The application class holds immutable list of document
 * in two languages.
 * The list of document files is available as assets.
 * The titles and file list are statically declared.
 */
public class ShaktiApplication extends Application {
    private static List<String> englishPoems;
    private static List<String> banglaPoems;
    private static List<String> audioFiles;
    private static Language language;
    private static Typeface banglaFont;
    private static Typeface englishFont;

    public static final String KEY_CURSOR = "CURSOR";
    private static final String APP = ShaktiApplication.class.getSimpleName();

    /**
     * initializes this application.
     * The application holds an immutable list of content
     * in English and Bangla.
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
        Log.e(APP, "=========================================");
        Log.e(APP, "onCreate initializing...");
        Log.e(APP, "=========================================");

        language = Language.ENGLISH;
        String[] fileNames = getResources().getStringArray(R.array.poem_file_list);
        englishPoems = createTOC(getResources().getString(R.string.english_poem_directory), fileNames, true);
        banglaPoems  = createTOC(getResources().getString(R.string.bangla_poem_directory), fileNames, true);
        audioFiles = createTOC(getResources().getString(R.string.audio_file_directory),
                getResources().getStringArray(R.array.audio_files), false);
        if (englishPoems.size() != banglaPoems.size()) {
            throw new IllegalArgumentException("english poems " + englishPoems.size() + " != bengali poems " + banglaPoems.size());
        }
        if (englishPoems.size() != audioFiles.size()) {
            throw new IllegalArgumentException("english poems " + englishPoems.size() + " != audio files " + audioFiles.size());
        }
        banglaFont = getResources().getFont(R.font.kalpurush);
        englishFont = getResources().getFont(R.font.raleway);
    }

    /**
     * Gets the currently active language.
     * The currently active language determines the font.
     *
     * @return currently active language
     */
    public Language getCurrentLanguage() {
        return language;
    }

    /**
     * Sets the currently active language.
     * The currently active language determines the font.
     * @param  new language to set
     * @return true if language has really changed
     */
    public boolean setLanguage(Language lang) {
        if (this.language == lang) {
            return false;
        } else {
            this.language = lang;
            return true;
        }
    }

    /**
     * Switches the language of the application.
     *
     * @return the current language
     */
    public Language switchLanguage() {
        Language old = this.getCurrentLanguage();
        language = getCurrentLanguage() == Language.ENGLISH
                ? Language.BANGLA : Language.ENGLISH;
        Log.e(APP, "Switched language from " + old + " to " + language);
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
                    Log.e(APP, "open asset " + fileName);
                    list.add(readFileContent(in));
                } catch (Exception ex) {
                    Log.e(APP, "can not read asset " + fileName + " due to " + ex.getMessage());
                    list.add(null);
                }
            } else {
                if (rsrcName == null || rsrcName.isEmpty()) {
                    list.add(null);
                } else {
                    Log.e(APP, "add asset (not read) " + fileName);
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
            case BANGLA:
                return banglaPoems.get(cursor);
        }
        return null;
    }

    public Uri getAudioUri(int cursor) {
        String audioFile = audioFiles.get(cursor);
        if (audioFile == null) return null;
        return Uri.parse("asset:///" + audioFile);
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
            //int length = 1024;
            //byte[] buffer = new byte[length];
            int size = 1024;
            byte[] buffer = new byte[size];
            int len = 0;
            int d = 0;
            while ((len = in.read(buffer, 0, size)) != -1) {
                out.write(buffer, 0, len);
                d += len;
            };
            in.close();
            out.close();
            Log.e(APP, "audio stream " + fileName + " has " + d + " bytes");
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
            case BANGLA:
                return banglaFont;
        }
        return null;
    }

    private Typeface getFont() {
        return getFont(this.language);
    }

    public int getPoemCount() {
        return englishPoems.size();
    }

    public boolean hasPoem(int cursor) {
        return cursor >= 0 && cursor < getPoemCount();
    }

    public boolean hasAudio(int cursor) {
        if (cursor < 0) return false;
        if (cursor > getPoemCount()-1) return false;
        String rsrcName = audioFiles.get(cursor);
        if (rsrcName == null || rsrcName.isEmpty()) return false;

        return true;
    }

    /**
     * gets the title of the poem.
     * @param lang language
     * @param index index
     * @return a title
     */
    public String getPoemTitle(Language lang, int index) {
        String[] toc;
        if (lang == Language.BANGLA) {
            toc = getResources().getStringArray(R.array.bangla_poem_title_list);
        } else {
            toc = getResources().getStringArray(R.array.english_poem_title_list);
        }
        return toc[index];
    }
}
