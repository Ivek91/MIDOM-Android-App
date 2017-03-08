package hr.fer.zari.midom.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import hr.fer.zari.midom.R;
import hr.fer.zari.midom.activities.SetDownloadType;
import hr.fer.zari.midom.dialogs.DialogLoading;
import hr.fer.zari.midom.picture.ImageBitmap;
import hr.fer.zari.midom.utils.Constants;
import hr.fer.zari.midom.utils.ImageException;
import hr.fer.zari.midom.utils.MidomUtils;
import hr.fer.zari.midom.utils.decode.CBPredictor;
import hr.fer.zari.midom.utils.decode.GRCoder;
import hr.fer.zari.midom.utils.decode.PGMImage;
import hr.fer.zari.midom.utils.decode.Predictor;

import static com.google.android.gms.internal.a.F;
import static com.google.android.gms.internal.a.I;
import static com.google.android.gms.internal.a.T;
import static hr.fer.zari.midom.utils.Constants.FOLDER;
import static hr.fer.zari.midom.utils.Constants.TEST_FOLDER;
import static hr.fer.zari.midom.utils.Constants.ZIP_EXTRACT;

/**
 * AsyncTask for downloading study.
 * Downloading file from given url in constructor,
 * and unzipping it folder "/temp-download"
 *
 *
 * Currently can't show progress because server returns -1,
 * maybe add later
 *
 */
public class AsyncDownloadStudy extends AsyncTask<Void, Void, Void> {

    public static final String TAG = AsyncDownloadStudy.class.getSimpleName();

    private Activity activity;
    private URL url;
    private int ID;
    //private AsyncLoadImage.OnTaskCompleted listener;
    private DialogLoading dialogLoading;
    private unzipCompleted listener;
    private String downloadType;
    boolean isWiFi;
    boolean isMobile;

//    private DialogDownloading dialogDownloading;

    public AsyncDownloadStudy(Activity activity, int ID) throws MalformedURLException {

        this.activity = activity;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        this.downloadType = sharedPref.getString(SetDownloadType.DOWNLOAD_TYPE_PREFERENCE, "");

        ConnectivityManager cm = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        this.isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        this.isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;


        Log.e ("DownloadType", "Download Type: " + downloadType);
        if (downloadType.equals("Uncompressed") || (downloadType.equals("Automatic") && isWiFi))
            this.url = new URL(Constants.GET_UNCOMP_STUDY + ID);
        else
            this.url = new URL(Constants.GET_COMP_STUDY + ID);
        Log.e ("DownloadType", "URL: " + this.url);
        this.ID = ID;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MidomUtils.lockOrientation(activity);
        Log.d(TAG, "Started onPreExecute");
        dialogLoading = new DialogLoading();
        dialogLoading.setTitle("Downloading study");
        dialogLoading.show(activity.getFragmentManager(), TAG);

//        dialogDownloading = new DialogDownloading();
//        dialogDownloading.show(activity.getFragmentManager(), TAG);
        // create folder for downloading images
        File downloadFolder = new File(Constants.ZIP_DOWNLOAD_LOCATION);
        if (!downloadFolder.exists()) {
            boolean created = downloadFolder.mkdirs();
            if (!created) {
                Log.e(TAG, "cannot create the folder for downloading images");
            }
        }
    }

    @Override
    protected Void doInBackground(Void... v) {
        Log.d(TAG, "Started doInBackground");

        InputStream input = null;
        OutputStream output = null;
        try {
            URLConnection connection = url.openConnection();
            connection.connect();

            // if -1 server dosen't return size of file
            int lengthOfFile = connection.getContentLength();
            Log.d(TAG, "Length of file = " + lengthOfFile);
            Log.e(TAG, "Opening input for " + url);
            // input stream to read file - with 8k buffer
            input = new BufferedInputStream(url.openStream(), 8192);
            Log.e(TAG, "Input open :)");
            // Output stream to write file
            output = new FileOutputStream(Constants.ZIP_DOWNLOAD_LOCATION + "/"
                    + Constants.ZIP_DOWNLOAD_NAME + ID + ".zip");

            byte data[] = new byte[1024];
            //long total = 0;
            int count;

            while ((count = input.read(data)) != -1) {
                //total += count;
                //calculate the percentage of downloaded file
                if (lengthOfFile > 0) {
                    //progress = (int) ((total * 100) / lengthOfFile);
                    //publishProgress();
                }
                // writing data to file
                output.write(data, 0, count);
            }
            unzipFunction(Constants.ZIP_DOWNLOAD_LOCATION + "/" + Constants.ZIP_DOWNLOAD_NAME + ID + ".zip",
                    Constants.ZIP_EXTRACT);


            if (downloadType.equals("Compressed") || (downloadType.equals("Automatic") && isMobile)){
                List<File> files = getFiles();
                for (File file:  files){
                    if (file.getName().toLowerCase().endsWith(".cbp")){
                        Log.e(TAG, "Decompressing " + file.getName());
                        decompressFile(file);
                        Log.e(TAG, "Decompressed " + file.getName());

                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error with downloading file");
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... v) {
        super.onProgressUpdate(v);
    }

    /**
     * First unzips downloaded file and then start AsyncTask to load and show image.
     * @param   v   Void nothing
     */
    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        dialogLoading.dismiss();
        MidomUtils.unLockOrientation(activity);
        listener.unzipFinished();
    }


    /**
     * Function for unzipping file.zip
     * @param zipFile   Path to file.zip
     * @param destinationFolder Path for extraction
     */
    private void unzipFunction(String zipFile, String destinationFolder) {
        File directory = new File(destinationFolder);

        // if the output directory doesn't exist, create it
        if(!directory.exists()) {
            directory.mkdirs();
        }

        // buffer for read and write data to file
        byte[] buffer = new byte[4096];

        try {
            FileInputStream fInput = new FileInputStream(zipFile);
            BufferedInputStream bInput = new BufferedInputStream(fInput, buffer.length);
            ZipInputStream zipInput = new ZipInputStream(bInput);

            ZipEntry entry = zipInput.getNextEntry();

            while(entry != null){
                String entryName = entry.getName();
                File file = new File(destinationFolder + File.separator + entryName);

                System.out.println("Unzip file " + entryName + " to " + file.getAbsolutePath());

                // create the directories of the zip directory
                if(entry.isDirectory()) {
                    File newDir = new File(file.getAbsolutePath());
                    if(!newDir.exists()) {
                        boolean success = newDir.mkdirs();
                        if(!success) {
                            System.out.println("Problem creating Folder");
                        }
                    }
                }
                else {
                    FileOutputStream fOutput = new FileOutputStream(file);
                    BufferedOutputStream bOutput = new BufferedOutputStream(fOutput, buffer.length);
                    int count;
                    while ((count = zipInput.read(buffer)) > 0) {
                        // write 'count' bytes to the file output stream
                        bOutput.write(buffer, 0, count);
                    }
                    bOutput.flush();
                    bOutput.close();

                    fOutput.flush();
                    fOutput.close();
                }
                // close ZipEntry and take the next one
                zipInput.closeEntry();
                entry = zipInput.getNextEntry();
            }

            // close the last ZipEntry
            zipInput.closeEntry();

            zipInput.close();
            fInput.close();




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface unzipCompleted {
        void unzipFinished();
    }

    public void setListener(unzipCompleted listener) {
        this.listener = listener;
    }

    public void decompressFile (File file){
        //File needs to be .cbp
        GRCoder dekoder = new GRCoder();

        int[] buffer = dekoder.decode(file.getAbsolutePath());

        PGMImage decodedImage = new PGMImage();
        decodedImage.setDimension(buffer[0], buffer[1]);
        decodedImage.setMaxGray(buffer[2]);

        Predictor predictor = new CBPredictor(CBPredictor.VectorDistMeasure.L2, CBPredictor.BlendPenaltyType.SSQR, 5, 6, 6, 0, false);

        int renewedPixel;
        for (int k = 0; k < buffer[1]; k++) {
            for (int j = 0; j < buffer[0]; j++) {
                int prediction = predictor.predict(k, j, decodedImage);
                renewedPixel = buffer[k * buffer[0] + j + 3] + prediction;
                decodedImage.setPixel(k, j, renewedPixel);
            }
            if (k % 25 == 0) Log.e("dekodiranje", String.valueOf(k));
        }

        String filepath = ZIP_EXTRACT +"/images/" + file.getName().substring(0, file.getName().indexOf(".")) + ".pgm";
        Log.e("decoder", "Saving file to " + filepath);
        decodedImage.setFilePath(filepath);
        decodedImage.writeImage();
    }

    private List<File> getFiles(){
        List<File> result = new ArrayList<>();

        String path = ZIP_EXTRACT + "/" + ID + ".cbp";
        Log.e("Files", "Geting files from Path: " + path);
        File directory = new File(path);
        if (! directory.exists()){
            directory.mkdir();
        }
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            result.add(files[i]);
            Log.e("Files", "FileName:" + files[i].getName());
        }
        return result;
    }


}
