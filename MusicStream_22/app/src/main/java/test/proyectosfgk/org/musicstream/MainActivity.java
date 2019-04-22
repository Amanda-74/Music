package test.proyectosfgk.org.musicstream;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import java.util.List;
import java.util.concurrent.ExecutionException;

import test.proyectosfgk.org.musicstream.util.SongAdapter;

public class MainActivity extends AppCompatActivity {


    private List<Song> canciones;
    private RecyclerView listado;
    private SongAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listado = findViewById(R.id.rvCanciones);
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        listado.setLayoutManager(lim);

        data();
        iniciarAdaptador();
    }

    public void data() {
        try {
            canciones = (List<Song>) new prepareSong().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void iniciarAdaptador(){
        adapter = new SongAdapter(canciones);
        listado.setAdapter(adapter);
    }
}
