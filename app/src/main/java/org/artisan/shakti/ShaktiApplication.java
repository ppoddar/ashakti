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
    public static final String KEY_CURSOR = "CURSOR";
    public static final String KEY_LANGUAGE = "LANGUAGE";

    private static final String APP = ShaktiApplication.class.getSimpleName();

    private List<String> englishPoems;
    private List<String> banglaPoems;
    private List<String> audioFiles;
    private Typeface banglaFont;
    private Typeface englishFont;

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
        Log.e(APP, "=========================================");
        Log.e(APP, "onCreate initializing...");
        Log.e(APP, "=========================================");
        String[] fileNames = getResources().getStringArray(R.array.poem_file_list);
        englishPoems = createTOC(getResources().getString(R.string.english_poem_directory), fileNames, true);
        banglaPoems = createTOC(getResources().getString(R.string.bangla_poem_directory), fileNames, true);
        String[] audioFileNames = getResources().getStringArray(R.array.audio_files);
        audioFiles = createTOC(getResources().getString(R.string.audio_file_directory), audioFileNames, false);

        assert (englishPoems.size() == banglaPoems.size()) : String.format("english poems %d != bengali poems %d", englishPoems.size(), banglaPoems.size());
        assert (englishPoems.size() == audioFiles.size()) : String.format("english poems %d != audio files %d", englishPoems.size(), audioFiles.size());

        banglaFont = getResources().getFont(R.font.kalpurush);
        englishFont = getResources().getFont(R.font.raleway);
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
        throw new IllegalArgumentException("no font defined for language " + lang);
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
    String readFileContent(InputStream fis) throws Exception {
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
    public String getPoem(int cursor, Language language) {
        if (!hasPoem(cursor)) {
            Log.e(APP, "requested poem at cursor " + cursor + " is out of range. There are " + getPoemCount() + " registered poems");
            return null;
        }
        switch (language) {
            case ENGLISH:
                return englishPoems.get(cursor);
            case BANGLA:
                return banglaPoems.get(cursor);
        }
        throw new IllegalArgumentException(String.format("no poem for language %s", language));
    }

    /*
     * Gets the URI for audio file at given cursor.
     */
    public Uri getAudioUri(int cursor) {
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


    public int getPoemCount() {
        return englishPoems.size();
    }

    public boolean hasPoem(int cursor) {
        return cursor >= 0 && cursor < getPoemCount();
    }

    public boolean hasAudio(int cursor) {
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

//    /**
//     * Play audio.
//     *
//     * @param cursor
//     * @param view   an optional view t which the player will be attached
//     * @param pb     an optional progress bar.
//     */
//    public void playAudio(int cursor, StyledPlayerControlView view, ProgressBar pb) {
//        Uri uri = getAudioUri(cursor);
//        if (uri == null) return;
//        try {
//            if (pb != null) pb.setVisibility(View.VISIBLE);
//            audioPlayer = new SimpleExoPlayer.Builder(this).build();
//            MediaItem mediaItem = new MediaItem.Builder()
//                    .setUri(uri)
//                    .build();
//            audioPlayer.setMediaItem(mediaItem);
//            audioPlayer.prepare();
//            if (view != null) view.setPlayer(audioPlayer);
//            audioPlayer.play();
//        } finally {
//            if (pb != null) pb.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    public void stopAudio() {
//        if (audioPlayer != null) {
//            audioPlayer.stop();
//        }
//    }
//
//    public void pauseAudio() {
//        if (audioPlayer != null) {
//            audioPlayer.pause();
//        }
//    }
//    /**
//     * gets the file descriptor of a audio file at given cursor.
//     *
//     * @param cursor a index
//     * @return null if audio file name is empty
//     */
//    public FileDescriptor getAudioStream(int cursor) {
//        String fileName = audioFiles.get(cursor);
//        if (fileName == null) {
//            Log.e(APP, "no audio file at cursor " + cursor);
//            return null;
//        }
//        try {
//            InputStream in = getAssets().open(fileName);
//            if (in == null) {
//                Log.e(APP, "no stream for audio file  " + fileName);
//                return null;
//            }
//            Log.e(APP, "reading audio stream " + fileName);
//            File tmpFile = File.createTempFile("audio", "mp3", null);
//            FileOutputStream out = new FileOutputStream(tmpFile);
//            //int length = 1024;
//            //byte[] buffer = new byte[length];
//            int size = 1024;
//            byte[] buffer = new byte[size];
//            int len;
//            int d = 0;
//            while ((len = in.read(buffer, 0, size)) != -1) {
//                out.write(buffer, 0, len);
//                d += len;
//            }
//            in.close();
//            out.close();
//            Log.e(APP, "audio stream " + fileName + " has " + d + " bytes");
//            FileInputStream fis = new FileInputStream(tmpFile);
//            return fis.getFD();
//        } catch (Exception ex) {
//            Log.e(APP, "error reading audio stream " + fileName);
//            ex.printStackTrace();
//            return null;
//        }
//    }
