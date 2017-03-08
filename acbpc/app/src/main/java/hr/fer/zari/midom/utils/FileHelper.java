package hr.fer.zari.midom.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHelper {

    public static byte[] getBytesFromStream(InputStream is) {

        int len;
        int size = 1024;
        byte[] buf;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        try {
            while((len = is.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        buf = bos.toByteArray();

        return buf;
    }

}
