package test.proyectosfgk.org.musicstream;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SongServices {
    public List<Song> lista = new ArrayList<>();

    public SongServices() {
        try {
            lista = (List<Song>) new prepareSong().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Song buscar(String titulo, String artista) {
        Song cancion = new Song();
        System.out.println("La lista tiene " + lista.size());
        for (Song ca : lista
                ) {
            System.out.println("Nombre: " + ca.getTitulo());
            if (ca.getArtista().equals(artista) && ca.getTitulo().equals(titulo)) {
                cancion = ca;
            }

        }
        return cancion;
    }

    public Song nextSong(String position) {
        int id = Integer.parseInt(position);
        System.out.println("Me enviaste "+position);
        Song cancion = new Song();

            if (id < lista.size() - 1) {
                cancion = lista.get(id + 1);
            }

        return cancion;

    }

    public Song prevSong(String id) {
        Song cancion = new Song();
        System.out.println("Me enviaste "+ id);

            if ((Integer.parseInt(id) > 0)) {
                cancion = lista.get(Integer.parseInt(id) - 1);
            }

        return cancion;

    }
}

class prepareSong extends AsyncTask {
    private List<Song> canciones = new ArrayList<Song>();


    @Override
    protected Object doInBackground(Object[] objects) {

        HttpClient client = new DefaultHttpClient();
        HttpGet peticion = new HttpGet("http://jws-app-musicapp.7e14.starter-us-west-2.openshiftapps.com/cancion/ver");
        peticion.setHeader("content-type", "application/json");

        try {
            HttpResponse response = client.execute(peticion);

            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String linea = "";
            String resultado = "";

            while ((linea = reader.readLine()) != null) {
                resultado += linea;
            }

            JSONArray json = new JSONArray(resultado);

            for (int i = 0; i < json.length(); i++) {
                Song cancion = new Song();
                cancion.setId(json.getJSONObject(i).get("id").toString());
                System.out.println("ID: " + json.getJSONObject(i).get("id").toString());
                cancion.setTitulo(json.getJSONObject(i).get("nombre").toString());
                cancion.setCover(json.getJSONObject(i).getJSONObject("album").get("nombre").toString());
                cancion.setUrlFile(json.getJSONObject(i).get("id").toString());
                cancion.setGenero(json.getJSONObject(i).getJSONObject("genero").get("genero").toString());
                cancion.setArtista(json.getJSONObject(i).getJSONObject("album").getJSONObject("artista").get("nombre").toString());

                System.out.println("Artista " + json.getJSONObject(i).getJSONObject("album").getJSONObject("artista").get("nombre").toString());
                canciones.add(cancion);
                System.out.println("lista " + canciones.size());
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return canciones;
    }
}

