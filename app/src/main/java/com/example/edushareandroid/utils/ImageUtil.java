package com.example.edushareandroid.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImageUtil {

    public static byte[] uriToBinary(Uri uri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public static String getImageExtensionFromUri(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String mimeType = contentResolver.getType(uri);
        String extension = mime.getExtensionFromMimeType(mimeType);

        if (extension == null) {
            extension = "jpg"; // Por defecto, si no se detecta
        }
        return extension;
    }


    // Convertir array de bytes binarios a Bitmap
    public static Bitmap binaryToBitmap(byte[] binaryData) {
        return BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
    }
}