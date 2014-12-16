package com.friends.android.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FriendUtils {
    public static byte [] inputStreamToByte(InputStream is) {   
        try {
            ByteArrayOutputStream bAOutputStream = new ByteArrayOutputStream();   
            int ch;   
            while((ch = is.read() ) != -1){   
                bAOutputStream.write(ch);   
            }   
            byte data [] =bAOutputStream.toByteArray();   
            bAOutputStream.close();   
            return data;   
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }
    
    public static String is2String(InputStream input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        try {
            while ((i = input.read()) != -1) {
                baos.write(i);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return baos.toString();
    }
    
    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }
    
    

}
