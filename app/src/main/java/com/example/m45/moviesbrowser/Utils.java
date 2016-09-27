package com.example.m45.moviesbrowser;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.io.ByteStreams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by m45 on 8/6/16.
 * the methods in this file are adapted from different sources
 */
public class Utils {


    public static String getResponse(String url) throws IOException {
        // using HttpClient which is deprecated but because for some reason
        // HttpURLConnection did not retrieve data from the API when
        // loading a single result. Even tried simulating the request as if it
        // is a web browser, but it didn't work.

        HttpClient httpclient = new DefaultHttpClient();
        // Prepare a request object
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("accept", "application/json");
        // Execute the request
        HttpResponse response;
        response = httpclient.execute(httpget);

        // Get hold of the response entity
        HttpEntity entity = response.getEntity();
        // If the response does not enclose an entity, there is no need
        // to worry about connection release

        StringBuilder st = new StringBuilder();

        if (entity != null) {

            // A Simple JSON Response Read
            InputStream inputStream = entity.getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                st.append(line);
            }
            bufferedReader.close();
            inputStream.close();
        }

        return st.toString();
    }

    public static void saveImage(Context context, Bitmap bitmap, String imageName) {

        FileOutputStream outputStream;

        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bytes = bos.toByteArray();

            outputStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            outputStream.write(bytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap loadImage(Context context, String imageName) {
        Bitmap bitmap = null;
        try {
            FileInputStream inputStream = context.openFileInput(imageName);
            byte[] bytes = ByteStreams.toByteArray(inputStream);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
