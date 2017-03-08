package hr.fer.zari.midom.picture;

import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import hr.fer.zari.midom.utils.ImageException;

/**
 * Class for .pgm picture format.
 * Parses data from .pgm and stores values.
 * Getters and setters for all values.
 */
public class FormatPGM {


    public static final String TAG = FormatPGM.class.getSimpleName();

    public static final String magicNumberPGM = "P5";

    private StringBuilder stringBuilder = new StringBuilder();

    private String fileName;
    private String filePath;
    private String magicNumber;
    private String comment;
    private int width;
    private int height;
    private int maxValOfGray;
//    private int[][] data2D;

    //moguca optimizacija - da se prilikom citanja pgm-a pripreme argb za bitmap
    // data1D[pos] = 0xff << 24 | (pixel & 0xff) << 16 | (pixel & 0xff) << 8 | pixel;
    private byte[] data1D;

    /**
     * Constructor of formatPGM
     * @param filePath  String  location of PGM file
     */
    public FormatPGM(String filePath) throws ImageException {
        if (filePath.endsWith(".pgm")) {
            this.filePath = filePath;
            Log.d(TAG, filePath);
            File file = new File(filePath);
            fileName = file.getName();
            readImage();
        } else {
            Log.d(TAG, "Wrong file format!");
            throw new ImageException("File is not in expected format .pgm");
        }
    }

    private void readImage(){
        //File pgmFile = new File(filePath);
        FileInputStream fileInputStream = null;
        String temp;
        int numberOfLines = 0;

        // first read the values of picture
        try {
            fileInputStream = new FileInputStream(filePath);
            Scanner scan = new Scanner(fileInputStream);

            // read Magic Number
            magicNumber = scan.nextLine();
            numberOfLines++;
            if (!magicNumber.equals(magicNumberPGM)) {
                throw new ImageException("Image not starting with " + magicNumberPGM);
            }

            // read width of picture
            do {
                temp = scan.nextLine();
                numberOfLines++;
            } while (readComment(temp));
            String[] split = temp.split("\\s+");
            width = getNumber(split[0]);
            if (split.length > 1) {
                height = getNumber(split[1]);
            } else {
                // read height of picture
                do {
                    temp = scan.nextLine();
                    numberOfLines++;
                } while (readComment(temp));
                String[] splitWidth = temp.split("\\s+");
                height = getNumber(splitWidth[0]);
            }

            if (width < 1 || height < 1) {
                throw new ImageException("Image width or height is below 1");
            }

            // read maximum value of gray for picture
            do {
                temp = scan.nextLine();
                numberOfLines++;
            } while (readComment(temp));
            maxValOfGray = getNumber(temp);

            fileInputStream.close();
        } catch (IOException | ImageException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.toString());
            }
        }

        comment = stringBuilder.toString();


        // read raw data of picture
        try {
            fileInputStream = new FileInputStream(filePath);

            DataInputStream dis = new DataInputStream(fileInputStream);

            int i = 0;
            char c;
            while (i != numberOfLines) {
                do {
                    c = (char)(dis.readUnsignedByte());
                } while (c != '\n');
                i++;
            }

            // read the image data
//            data2D = new int[height][width];
            data1D = new byte[height*width];
            int n = dis.read(data1D);
            if (n != width * height) {
                throw new IOException("Not enough data");
            }

//            for (int row = 0; row < height; row++) {
//                for (int col = 0; col < width; col++) {
//                    data2D[row][col] = (int) (data1D[row*width + col] & 0xff);
//                }
//            }
            /*
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    data2D[row][col] = (int) (dis.readUnsignedByte() * 255 / maxValOfGray);
                }
            }*/

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.toString());
            }
        }

    }


    private int getNumber(String data) {
        if (isNumber(data.trim())) {
            return Integer.parseInt(data);
        } else {
            return -1;
        }
    }

    /**
     * Function to check for comments in file.
     * Comments starts with '#' at the start of line.
     * @param temp  String  line in file
     */
    private boolean readComment(String temp) {
        if (temp.startsWith("#")) {
            stringBuilder.append(temp);
            return true;
        }
        return false;
    }

    /**
     * Function for checking if String of data is a number - integer.
     * @param data String  string with data
     * @return  boolean is integer?
     */
    public static boolean isNumber(String data) {
        try {
            Integer.parseInt(data.trim());
        }
        catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    public String getMagicNumber() {
        return magicNumber;
    }

    public String getComment() {
        return comment;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxValOfGray() {
        return maxValOfGray;
    }

    public byte[] getData1D() {
        return data1D;
    }

    //    public int[][] getData2D() {
//        return data2D;
//    }
//
//    public int getPixel(int row, int column) {
//        return data2D[row][column];
//    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
