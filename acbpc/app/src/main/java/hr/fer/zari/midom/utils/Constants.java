package hr.fer.zari.midom.utils;

import android.os.Environment;

public class Constants {

    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String FOLDER = "/midom";
    //public static final String LINK = "http://midom.rasip.fer.hr:8080";   // link for download
    public static final String LINK = "http://161.53.67.87:9000";
    public static final String TEMP = "/temp-download";

    public static final String ZIP_DOWNLOAD_LOCATION = Constants.PATH + Constants.FOLDER +  TEMP;
    public static final String ZIP_DOWNLOAD_NAME = "study";
    public static final String ZIP_EXTRACT = ZIP_DOWNLOAD_LOCATION;

    public static final String AUDIO_NAME = PATH + FOLDER + TEMP + "/audio.3gp";

    public static final String GET_UNCOMP_STUDY = LINK + "/ms/getStudyArchiveUncompressed/";
    public static final String GET_COMP_STUDY = LINK + "/ms/getStudyArchiveCompressed/";

    public static final String TEST_FOLDER= "/MidomData";


    public static final int NUM_OF_COLUMNS_GRID = 3;
    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp

    public final static int ALARM_INTERVAL = 10 * 60 * 1000; // 10 minutes
}
