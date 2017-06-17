package hr.fer.zari.midom.picture;

import android.graphics.Bitmap;
import android.util.Log;
import com.imebra.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import hr.fer.zari.midom.utils.ImageException;

import static com.imebra.drawBitmapType_t.drawBitmapRGBA;

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
                com.imebra.ReadingDataHandlerNumeric dataHandler = image.getReadingDataHandler();
                // The transforms chain will contain all the transform that we want to
                // apply to the image before displaying it
                com.imebra.TransformsChain chain = new com.imebra.TransformsChain();
                if(com.imebra.ColorTransformsFactory.isMonochrome(image.getColorSpace()))
                {
                    // Allocate a VOILUT transform. If the DataSet does not contain any pre-defined
                    //  settings then we will find the optimal ones.
                    VOILUT voilutTransform = new VOILUT();
                    // Retrieve the VOIs (center/width pairs)
                    com.imebra.VOIs vois = loadedDataSet.getVOIs();
                    // Retrieve the LUTs
                    List<com.imebra.LUT> luts = new ArrayList<com.imebra.LUT>();
                    for(long scanLUTs = 0; ; scanLUTs++)
                    {
                        try
                        {
                            luts.add(loadedDataSet.getLUT(new com.imebra.TagId(0x0028,0x3010), scanLUTs));
                        }
                        catch(Exception e)
                        {
                            break;
                        }
                    }
                    if(!vois.isEmpty())
                    {
                        voilutTransform.setCenterWidth(vois.get(0).getCenter(), vois.get(0).getWidth());
                    }
                    else if(!luts.isEmpty())
                    {
                        voilutTransform.setLUT(luts.get(0));
                    }
                    else
                    {
                        voilutTransform.applyOptimalVOI(image, 0, 0, width, height);
                    }
                    chain.addTransform(voilutTransform);
                }
                // We create a DrawBitmap that always apply the chain transform before getting the RGB image
                com.imebra.DrawBitmap draw = new com.imebra.DrawBitmap(chain);
                // Ask for the size of the buffer (in bytes)
                long requestedBufferSize = draw.getBitmap(image, drawBitmapRGBA, 4, new byte[0]);
                byte buffer[] = new byte[(int)requestedBufferSize]; // Ideally you want to reuse this in subsequent calls to getBitmap()
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                // Now fill the buffer with the image data and create a bitmap from it
                draw.getBitmap(image, drawBitmapType_t.drawBitmapRGBA, 4, buffer);
                Bitmap renderBitmap = Bitmap.createBitmap((int)image.getWidth(), (int)image.getHeight(), Bitmap.Config.ARGB_8888);
                renderBitmap.copyPixelsFromBuffer(byteBuffer);
                // The Bitmap can be assigned to an ImageView on Android
                bitmap = renderBitmap;



                float aspectRatio = width_int /
                    (float) height_int;
                 int wwidth = 512;
                int wheight = Math.round(wwidth / aspectRatio);

                 thumbBitmap = Bitmap.createScaledBitmap(
                    renderBitmap, wwidth, wheight, false);

                 renderBitmap.recycle();

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
            long height = image.getHeight();
            com.imebra.ReadingDataHandlerNumeric dataHandler = image.getReadingDataHandler();

            // The transforms chain will contain all the transform that we want to
            // apply to the image before displaying it
            com.imebra.TransformsChain chain = new com.imebra.TransformsChain();
            if(com.imebra.ColorTransformsFactory.isMonochrome(image.getColorSpace()))
            {
                // Allocate a VOILUT transform. If the DataSet does not contain any pre-defined
                //  settings then we will find the optimal ones.
                VOILUT voilutTransform = new VOILUT();
                 // Retrieve the VOIs (center/width pairs)
                com.imebra.VOIs vois = loadedDataSet.getVOIs();
                 // Retrieve the LUTs
                List<com.imebra.LUT> luts = new ArrayList<com.imebra.LUT>();
                for(long scanLUTs = 0; ; scanLUTs++)
                {
                    try
                    {
                        luts.add(loadedDataSet.getLUT(new com.imebra.TagId(0x0028,0x3010), scanLUTs));
                    }
                    catch(Exception e)
                    {
                        break;
                    }
                }
                if(!vois.isEmpty())
                {
                    voilutTransform.setCenterWidth(vois.get(0).getCenter(), vois.get(0).getWidth());
                }
                else if(!luts.isEmpty())
                {
                    voilutTransform.setLUT(luts.get(0));
                }
                else
                {
                    voilutTransform.applyOptimalVOI(image, 0, 0, width, height);
                }
                chain.addTransform(voilutTransform);
            }
            // We create a DrawBitmap that always apply the chain transform before getting the RGB image
            com.imebra.DrawBitmap draw = new com.imebra.DrawBitmap(chain);
            // Ask for the size of the buffer (in bytes)
            long requestedBufferSize = draw.getBitmap(image, drawBitmapRGBA, 4, new byte[0]);
            byte buffer[] = new byte[(int)requestedBufferSize]; // Ideally you want to reuse this in subsequent calls to getBitmap()
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            // Now fill the buffer with the image data and create a bitmap from it
            draw.getBitmap(image, drawBitmapType_t.drawBitmapRGBA, 4, buffer);
            Bitmap renderBitmap = Bitmap.createBitmap((int)image.getWidth(), (int)image.getHeight(), Bitmap.Config.ARGB_8888);
            renderBitmap.copyPixelsFromBuffer(byteBuffer);
            // The Bitmap can be assigned to an ImageView on Android
            bitmap = renderBitmap;
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
