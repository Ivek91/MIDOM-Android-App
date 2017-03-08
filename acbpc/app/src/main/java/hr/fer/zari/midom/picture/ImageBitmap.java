package hr.fer.zari.midom.picture;

import android.graphics.Bitmap;
import android.util.Log;

import hr.fer.zari.midom.utils.ImageException;

public class ImageBitmap {

    public static final String TAG = ImageBitmap.class.getName();

    private Bitmap bitmap;
    private Bitmap thumbBitmap;
    private String location;

    public ImageBitmap(String location) {
        this.location = location;
    }

    public void loadThumbBitmap() throws ImageException {
        FormatPGM pgmImage = new FormatPGM(location);
        Log.d(TAG, "width = " + pgmImage.getWidth());
        Log.d(TAG, "height = " + pgmImage.getHeight());
        if (pgmImage.getHeight() > 0 && pgmImage.getWidth() > 0) {

            Bitmap bmap = Bitmap.createBitmap(pgmImage.getWidth(), pgmImage.getHeight(), Bitmap.Config.ARGB_8888);

            byte[] data1d = pgmImage.getData1D();
            int[] pixels = new int[pgmImage.getHeight()*pgmImage.getWidth()];

            int height = pgmImage.getHeight();
            int width = pgmImage.getWidth();
            int intData;

            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    intData = data1d[row*width + column] & 0xff;
                    pixels[row * width +column] = (0xFF << 24) | (intData << 16) | (intData << 8) | intData;
                }
            }

            bmap.setPixels(pixels, 0, pgmImage.getWidth(), 0, 0, pgmImage.getWidth(), pgmImage.getHeight());

            float aspectRatio = pgmImage.getWidth() /
                    (float) pgmImage.getHeight();
            int wwidth = 512;
            int wheight = Math.round(wwidth / aspectRatio);

            thumbBitmap = Bitmap.createScaledBitmap(
                    bmap, wwidth, wheight, false);

            bmap.recycle();
        } else {
            thumbBitmap = null;
        }
    }

    public void loadBitmap() throws ImageException {
        FormatPGM pgmImage = new FormatPGM(location);
        Log.d(TAG, "width = " + pgmImage.getWidth());
        Log.d(TAG, "height = " + pgmImage.getHeight());
        if (pgmImage.getHeight() > 0 && pgmImage.getWidth() > 0) {

            Bitmap bmap = Bitmap.createBitmap(pgmImage.getWidth(), pgmImage.getHeight(), Bitmap.Config.ARGB_8888);

            byte[] data1d = pgmImage.getData1D();
            int[] pixels = new int[pgmImage.getHeight()*pgmImage.getWidth()];

            int height = pgmImage.getHeight();
            int width = pgmImage.getWidth();
            int intData;

            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    intData = data1d[row*width + column] & 0xff;
                    pixels[row * width +column] = (0xFF << 24) | (intData << 16) | (intData << 8) | intData;
                }
            }

            bmap.setPixels(pixels, 0, pgmImage.getWidth(), 0, 0, pgmImage.getWidth(), pgmImage.getHeight());

            bitmap = bmap;
        } else {
            bitmap = null;
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public String getLocation() {
        return location;
    }
}
