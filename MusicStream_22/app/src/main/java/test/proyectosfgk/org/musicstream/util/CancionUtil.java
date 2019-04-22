package test.proyectosfgk.org.musicstream.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CancionUtil extends AsyncTask {


    private String album;

    public CancionUtil(String album) {
        this.album = album;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Bitmap bitmap = null;
        try {
            URL imagenUrl = new URL(album);
            HttpURLConnection conn = (HttpURLConnection) imagenUrl.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
