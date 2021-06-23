package org.artisan.shakti;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The application class holds immutable list of poems
 * in two languages.
 * The list of poems is available as assets.
 * The poem titles and file list are statically declared.
 */
public class ShaktiApplication extends Application {
    public static final String KEY_CURSOR   = "CURSOR";
    public static final String KEY_LANGUAGE = "LANGUAGE";

    private static final String APP = ShaktiApplication.class.getSimpleName();

    private List<String> englishPoems;
    private List<String> banglaPoems;
    private List<String> audioFiles;
    private Typeface englishFont, banglaFont;

    /**
     * initializes this application.
     * The application holds an immutable list of poems
     * in English and Bangla.
     * The list of file names is declared statically. This method reads
     * the file names, locates the file as an  asset, reads
     * the file content and caches them.
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(APP, "=========================================");
        Log.d(APP, "onCreate initializing...");
        Log.d(APP, "=========================================");
        String[] fileNames = getResources().getStringArray(R.array.poem_file_list);
        englishPoems = createTOC(getResources().getString(R.string.english_poem_directory), fileNames, true);
        banglaPoems = createTOC(getResources().getString(R.string.bangla_poem_directory), fileNames, true);
        String[] audioFileNames = getResources().getStringArray(R.array.audio_files);
        audioFiles = createTOC(getResources().getString(R.string.audio_file_directory), audioFileNames, false);

        assert (englishPoems.size() == banglaPoems.size()) : String.format("english poems %d != bengali poems %d", englishPoems.size(), banglaPoems.size());
        assert (englishPoems.size() == audioFiles.size()) : String.format("english poems %d != audio files %d", englishPoems.size(), audioFiles.size());

        englishFont = Typeface.createFromAsset(getAssets(), "font/robato_slab_medium.ttf");
        //englishFont = Typeface.createFromAsset(getAssets(), "font/raleway.ttf");
        banglaFont  = Typeface.createFromAsset(getAssets(), "font/kalpurush.ttf");
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
    private List<String> createTOC(String dir, String[] rsrcNames, boolean readContent) {
        List<String> list = new ArrayList<>();
        for (String rsrcName : rsrcNames) {
            String fileName = dir + '/' + rsrcName;
            if (readContent) {
                try {
                    InputStream in = getAssets().open(fileName);
                    Log.d(APP, "open asset " + fileName);
                    list.add(readFileContent(in));
                } catch (Exception ex) {
                    Log.w(APP, String.format("can not read asset %s due to %s", fileName, ex.getMessage()));
                    list.add(null);
                }
            } else {
                if (rsrcName == null || rsrcName.isEmpty()) {
                    list.add(null);
                } else {
                    Log.d(APP, String.format("add asset (not read) %s", fileName));
                    list.add(fileName);
                }
            }
        }
        Log.e(APP, String.format("Created %d poems/audio from assets at %s", list.size(), dir));
        return list;
    }

    /**
     * Reads the content of given input stream.
     *
     * @param fis input stream to read
     * @return content of the stream in UTF8
     * @throws Exception if error
     */
    private String readFileContent(InputStream fis) throws Exception {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(fis, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        return buffer.toString();
    }


    /**
     * gets the poem of current language at given cursor.
     *
     * @param cursor cursor
     * @return null if cursor out of range
     */
    String getPoem(int cursor, Language language) {
        if (!hasPoem(cursor)) {
            Log.e(APP, "requested poem at cursor " + cursor + " is out of range. There are " + getPoemCount() + " registered poems");
            return null;
        }
        switch (language) {
            case ENGLISH:
                return englishPoems.get(cursor);
            case BANGLA:
                return banglaPoems.get(cursor);
            default:
                throw new IllegalArgumentException(String.format("no poem for language %s", language));
        }
    }

    /*
     * Gets the URI for audio file at given cursor.
     */
    Uri getAudioUri(int cursor) {
        try {
            String audioFile = audioFiles.get(cursor);
            if (audioFile == null) return null;
            return Uri.parse("asset:///" + audioFile);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(String.format("can not get audio URI for cursor %d", cursor));
        }
    }


    int getPoemCount() {
        return englishPoems.size();
    }

    boolean hasPoem(int cursor) {
        return cursor >= 0 && cursor < getPoemCount();
    }

    boolean hasAudio(int cursor) {
        if (cursor < 0) return false;
        if (cursor > getPoemCount() - 1) return false;
        String rsrcName = audioFiles.get(cursor);
        return rsrcName != null && !rsrcName.isEmpty();
    }

    /**
     * gets the title of the poem.
     *
     * @param lang  language
     * @param index index
     * @return a title
     */
    String getPoemTitle(Language lang, int index) {
        String[] toc;
        if (lang == Language.BANGLA) {
            toc = getResources().getStringArray(R.array.bangla_poem_title_list);
        } else {
            toc = getResources().getStringArray(R.array.english_poem_title_list);
        }
        return toc[index];
    }

    Typeface getFontFor(Language lang) {
        return (lang == Language.BANGLA)
                ? banglaFont : englishFont;
    }
}
