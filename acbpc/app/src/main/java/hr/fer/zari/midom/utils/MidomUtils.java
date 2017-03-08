package hr.fer.zari.midom.utils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.Surface;
import android.view.WindowManager;

public class MidomUtils {

    public static void lockOrientation(Activity activity) {
        int orientation = activity.getRequestedOrientation();
        int rotation = ((WindowManager) activity.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }

        activity.setRequestedOrientation(orientation);
    }

    public static void unLockOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public static int toRGB(int red, int green, int blue) {
        return (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }
}
