package org.artisan.shakti;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The application class holds immutable, global state.
 */
public class ShaktiApplication extends Application {
    //private static final List<String> audioFiles = new ArrayList<>();
    //private static final String audioFilePath = "";
    private List<String> englishPoems;
    private List<String> bengaliPoems;
    private List<String> audioFiles;
    private Typeface bengaliFont;
    private Typeface englishFont;

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
        englishPoems = createTOC(getResources().getStringArray(R.array.english_poems_toc), true);
        bengaliPoems = createTOC(getResources().getStringArray(R.array.bengali_poems_toc), true);
        audioFiles   = createTOC(getResources().getStringArray(R.array.audio_files), false);

        bengaliFont = Typeface.createFromAsset(getAssets(), "font/kalpurush.ttf");
        englishFont = Typeface.DEFAULT;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * reads the content of each raw resource.
     * For empty names, a null entry is added to returned list
     * @param rsrcNames
     * @param readContent if true reads the content, else just the anme
     * @return a list that may contain null
     */
    List<String> createTOC(String[] rsrcNames, boolean readContent) {
        List<String> list = new ArrayList<>();
        for (String rsrcName : rsrcNames) {
            if (readContent) {
                Log.e("SHAKTI", "get resource id for " + rsrcName);
                int rsrcId = getResources().getIdentifier(rsrcName, "raw", getPackageName());
                try {
                    InputStream in = getResources().openRawResource(rsrcId);
                    Log.e("SHAKTI", "read raw resource " + rsrcName);
                    list.add(readFileContent(in));
                } catch (Exception ex) {
                    Log.e("SHAKTI", "can not read resource " + rsrcName + " due to " + ex.getMessage());
                    //ex.printStackTrace();
                }
            } else {
                Log.e("SHAKTI", "add raw resource " + rsrcName);
                list.add(rsrcName);
            }
        }
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
     * @param lang   language
     * @param cursor cursor
     * @return null if cursor out of range
     */
    public String getPoem(Language lang, int cursor) {
        if (cursor < 0 || cursor > englishPoems.size() - 1)
            return null;
        switch (lang) {
            case ENGLISH:
                return englishPoems.get(cursor);
            case BENGALI:
                return bengaliPoems.get(cursor);
            default:
                return null;
        }
    }

    /**
     * gets the name of a audio file at given cursor.
     * @param cursor a index
     * @return null if audio file name is empty
     */
    public String getAudio(int cursor) {
        String fileName = audioFiles.get(cursor);
        return fileName == null || fileName.isEmpty() ? null : fileName;
    }

    public Typeface getFont(Language lang) {
        switch (lang) {
            case ENGLISH:
                return englishFont;
            case BENGALI:
                return bengaliFont;
        }
        return null;
    }


}
