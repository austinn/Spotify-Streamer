package com.refect.spotifystreamer.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Set;

/**
 * Created by anelson on 2/27/15.
 */
public class Utils {

    public static final String PREFS_KEY = "SpotifyStreamer";
    public static final String TAG = "SpotifyStreamer";

    public static final String PREFS_FIRST_TIME = "FIRST_TIME";

    public static final String PREFS_LAYOUT_MANAGER = "LAYOUT_MANAGER";
    public static final String PREFS_GRID = "GRID";
    public static final String PREFS_LIST = "LIST";

    public static final String KEY_ARTIST_MODELS = "PARCEABLE_ARTIST_MODELS";
    public static final String KEY_TRACK_MODELS = "PARCEABLE_TRACK_MODELS";
    public static final String PREFS_SEARCH_HISTORY = "SEARCH_HISTORY";

    public static final String INTENT_ARTIST_ID = "ARTIST_ID";
    public static final String INTENT_ARTIST_IMAGE_URL = "ARTIST_IMAGE_URL";


    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // Store string setting
    public static void storeSetting(String settingKey, String settingValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(settingKey, settingValue);
        // Commit the edits!
        editor.apply();
    }

    // Get string setting
    public static String getSetting(String settingName, String defaultValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return settings.getString(settingName, defaultValue);
    }

    // Store string setting
    public static void storeSettingSet(String settingKey, Set<String> settingValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(settingKey, settingValue);
        // Commit the edits!
        editor.apply();
    }

    // Get string setting
    public static Set<String> getSettingSet(String settingName, Set<String> defaultValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return settings.getStringSet(settingName, defaultValue);
    }

    public static int getScreenHeight(Context c) {
        int screenHeight;
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        int screenWidth = 0;
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        return screenWidth;
    }

    // ---------------------------------------------------------------------------------------
    // FILE HANDLING
    // ---------------------------------------------------------------------------------------
    public static void saveObjectToFile(Context context, String filename, Object obj) {

        filename = filename.replace(' ', '_');
        FileOutputStream fos;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutput oo = new ObjectOutputStream(bos);
            oo.writeObject(obj);
            byte[] buff = bos.toByteArray();

            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(buff);
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //Reads an object from file
    public static Object readObjectFromFile(Context context, String filename) {
        filename = filename.replace(' ', '_');
        File file = new File(context.getFilesDir(), filename);
        Object obj = null;

        if (file.exists()) {
            try {
                InputStream fileInput = context.openFileInput(filename);
                ObjectInputStream objectInput = new ObjectInputStream(fileInput);

                obj = objectInput.readObject();
                fileInput.close();
                objectInput.close();

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        return obj; // can return null
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

    /**
     * Converts a resource to a bitmap
     * @param resource
     * @return
     */
    public static Bitmap resource2Bitmap(Context ctx, int resource) {
        return BitmapFactory.decodeResource(ctx.getResources(), resource);
    }

    public static byte[] resource2ByteArray(Context ctx, int resource) {
        return bitmap2ByteArray(resource2Bitmap(ctx, resource));
    }

    /**
     * Converts a bitmap into a byte array
     * @param bmp
     * @return
     */
    public static byte[] bitmap2ByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Converts a byte array into a bitmap
     * @param data
     * @return
     */
    public static Bitmap byteArray2Bitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }


    public static String convertArrayToString(String[] array) {
        String strSeparator = "__,__";
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str) {
        String strSeparator = "__,__";
        String[] arr = str.split(strSeparator);
        return arr;
    }

    public static void hideKeyboard(Context ctx, View view) {
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(final Context ctx, final View view) {
        new Runnable() {
            public void run() {
                InputMethodManager inputMethodManager =  (InputMethodManager)ctx.getSystemService(ctx.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                view.requestFocus();
            }
        }.run();
    }

    /**
     *
     * @param sourceActivity
     * @param id
     */
    public static void showNotification(Activity sourceActivity, int id, int resId, String title, String text, boolean persistant) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(sourceActivity.getApplicationContext())
                        .setSmallIcon(resId)
                        .setContentTitle(title)
                        .setContentText(text);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(sourceActivity.getApplicationContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(sourceActivity);

        mBuilder.setOngoing(persistant);
        NotificationManager mNotificationManager =
                (NotificationManager) sourceActivity.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());
    }

    /**
     *
     * @param context
     * @param id
     */
    public static void dismissNotification(Context context, int id) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }

}
