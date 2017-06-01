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
        if (location.endsWith(".pgm")) {
            FormatPGM pgmImage = new FormatPGM(location);
            Log.d(TAG, "width = " + pgmImage.getWidth());
            Log.d(TAG, "height = " + pgmImage.getHeight());
            if (pgmImage.getHeight() > 0 && pgmImage.getWidth() > 0) {

                Bitmap bmap = Bitmap.createBitmap(pgmImage.getWidth(), pgmImage.getHeight(), Bitmap.Config.ARGB_8888);

                byte[] data1d = pgmImage.getData1D();
                int[] pixels = new int[pgmImage.getHeight() * pgmImage.getWidth()];

                int height = pgmImage.getHeight();
                int width = pgmImage.getWidth();
                int intData;

                for (int row = 0; row < height; row++) {
                    for (int column = 0; column < width; column++) {
                        intData = data1d[row * width + column] & 0xff;
                        pixels[row * width + column] = (0xFF << 24) | (intData << 16) | (intData << 8) | intData;
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
        else if(location.endsWith(".dcm")) {

            Log.d("START", "test");
            com.imebra.DataSet loadedDataSet = com.imebra.CodecFactory.load(location);
            com.imebra.Image image = loadedDataSet.getImageApplyModalityTransform(0);
            long width = image.getWidth();
            int width_int = (int) width;
            long height = image.getHeight();
            int height_int = (int) height;
            if (height_int > 0 && width_int > 0) {
                int buffer[] = new int[width_int*height_int];
                com.imebra.ReadingDataHandlerNumeric dataHandler = image.getReadingDataHandler();
                long size  = dataHandler.getUnitSize();
                int size_int = (int) size;
                Log.d("START","Width: " + String.valueOf(width_int));
                Log.d("START","Height: " + String.valueOf(height_int));
                Log.d("START","size: " + String.valueOf(size_int));

                long luminance = 0;
                if (size_int == 1) {
                    for (long scanY = 0; scanY != height; scanY++) {
                        for (long scanX = 0; scanX != width; scanX++) {
                            luminance = dataHandler.getSignedLong(scanY * width + scanX);
                            buffer[(int) scanY * width_int + (int) scanX] = (0xFF << 24) | ((int) luminance << 16) | ((int) luminance << 8) | (int) luminance;

                        }
                    }
                }
              /*  else if (size_int == 4) {
                    for (long scanY = 0; scanY != height; scanY++) {
                        for (long scanX = 0; scanX != width; scanX++) {
                            luminance = dataHandler.getSignedLong(scanY * width + scanX);
                            //buffer[(int) scanY * width_int + (int) scanX] = (0xFF << 24) | ((int) luminance << 16) | ((int) luminance << 8) | (int) luminance ;
                             buffer[(int) scanY * width_int + (int) scanX] = (int)luminance;
                    }
                } */
                 Bitmap bmap = Bitmap.createBitmap(width_int, height_int, Bitmap.Config.ARGB_8888);
                bmap.setPixels(buffer, 0, width_int, 0, 0, width_int, height_int);

                bmap.setPixel(100,100, 150);
                float aspectRatio = width_int /
                    (float) height_int;
                 int wwidth = 512;
                int wheight = Math.round(wwidth / aspectRatio);

                 thumbBitmap = Bitmap.createScaledBitmap(
                    bmap, wwidth, wheight, false);

                 bmap.recycle();

            }
            else {
                thumbBitmap = null;
            }
        }
    }

    public void loadBitmap() throws ImageException {
        if (location.endsWith(".pgm")) {
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
        else if(location.endsWith(".dcm")) {
            com.imebra.DataSet loadedDataSet = com.imebra.CodecFactory.load(location);
            com.imebra.Image image = loadedDataSet.getImageApplyModalityTransform(0);
            long width = image.getWidth();
            int width_int = (int) width;
            long height = image.getHeight();
            int height_int = (int) height;
            int buffer[] = new int[width_int*height_int];
            com.imebra.ReadingDataHandlerNumeric dataHandler = image.getReadingDataHandler();

            for(long scanY = 0; scanY != height; scanY++)
            {
                for(long scanX = 0; scanX != width; scanX++)
                {
                    int luminance = dataHandler.getSignedLong(scanY * width + scanX);
                    buffer[(int) scanY * width_int + (int) scanX] = (0xFF << 24) | ((int)luminance << 16) | ((int)luminance << 8) | (int)luminance;
                }
            }
            Bitmap bmap = Bitmap.createBitmap(width_int, height_int, Bitmap.Config.ARGB_8888);
            bmap.setPixels(buffer, 0, width_int, 0, 0, width_int, height_int);

            bitmap = bmap;
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
