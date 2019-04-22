package test.proyectosfgk.org.musicstream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.concurrent.ExecutionException;
import test.proyectosfgk.org.musicstream.util.CancionUtil;

public class PlaySongActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://jws-app-musicapp.7e14.starter-us-west-2.openshiftapps.com/cancion/stream/";
    private static final String BASE_URL_IMG = "http://jws-app-musicapp.7e14.starter-us-west-2.openshiftapps.com/album/portada?album=";
    private String songId = "";
    private String titulo = "";
    private String artista = "";
    private String urlFile = "";
    private String cover = "";
    private String url = "";
    private String position = "";
    private MediaPlayer player = null;
    private int musicPosition = 0;
    private Button btnPlay = null;
    private NotificationManagerCompat nmc;

    private TextView reaming, progreso;
    private SeekBar bar;
    private Runnable runnable;
    private Handler handler = new Handler();
    private SongServices songServices = new SongServices();
    private MediaSessionCompat mediaSession;
    private static final String CHANNEL_2 = "channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        nmc = NotificationManagerCompat.from(this);
        btnPlay = findViewById(R.id.btnPlay);
        progreso = findViewById(R.id.progreso);
        reaming = findViewById(R.id.reaming);

        data();
        displaySong(titulo, artista, cover);
        mediaSession = new MediaSessionCompat(this, "tag");

        bar = findViewById(R.id.seekBar);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void createNotification(){
        Notification.MediaStyle style = new Notification.MediaStyle();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.disco1);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.play)
                .setContentTitle(titulo)
                .setContentText(artista)
                .setLargeIcon(bitmap)
                .setWhen(0)
                .setStyle(style);

        builder
                .addAction(R.drawable.previous, "Previous", null)
                .addAction(R.drawable.play, "Play", null)
                .addAction(R.drawable.next, "Next", null);
        style.setShowActionsInCompactView(0,1,2);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void Play() {
        bar.setProgress(player.getCurrentPosition());

        if (player.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    Play();
                    int progress = player.getCurrentPosition();
                    String restante = Tiempo(player.getDuration() - progress);
                    String trancurso = Tiempo(progress);
                    progreso.setText(trancurso);
                    reaming.setText(restante);
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    public String Tiempo(int duracion){

        String segundos = "";
        int tiempo = duracion;
        duracion -= 1000;
        int min = duracion/1000/60;
        int sec = duracion/1000%60;

        if (sec < 10)segundos += "0";
        segundos += sec;

        return min + ":" + segundos;
    }

    public void playNext(View view) {
        System.out.println("AQUI SIGUIENTE " + songId);
        Song nextSong = null;

        if (Integer.parseInt(position) != (songServices.lista.size() - 1)) {
            nextSong = songServices.nextSong(position);
            position = String.valueOf(Integer.parseInt(position) + 1);

        }

        if (nextSong != null) {
            songId = nextSong.getId();
            titulo = nextSong.getTitulo();
            artista = nextSong.getArtista();
            urlFile = nextSong.getUrlFile();
            cover = nextSong.getCover();

            url = BASE_URL + urlFile;

            displaySong(titulo, artista, cover);
            stopActivities();
            playOrPause(view);
        } else {
            System.out.println("NO SIRVE ESTO");
        }
    }

    public void playPrev(View view) {
        System.out.println("AQUI ANTERIOR " + songId);
        Song prevSong = null;
        if (Integer.parseInt(position) > 0) {
            prevSong = songServices.prevSong(position);
            position = String.valueOf(Integer.parseInt(position) - 1);
        }

        if (prevSong != null) {
            songId = prevSong.getId();
            titulo = prevSong.getTitulo();
            artista = prevSong.getArtista();
            urlFile = prevSong.getUrlFile();
            cover = prevSong.getCover();

            url = BASE_URL + urlFile;
            System.out.println("URL " + url);

            displaySong(titulo, artista, cover);
            stopActivities();
            playOrPause(view);
        } else System.out.println("NO SIRVE ESTO");
    }

    public void stopActivities() {
        System.out.println("deteniendo todo ");
        if (player != null) {
            btnPlay.setBackgroundResource(R.drawable.pause_white);
            musicPosition = 0;
            setTitle(titulo + " - " + artista);
            player.stop();
            player.release();
            player = null;
        }
    }

    public void playOrPause(View view) {

        System.out.println("Preparando " + url);
        if (player == null) {
            preparePlayer();
        }
        if (!player.isPlaying()) {
            if (musicPosition > 0) {
                player.seekTo(musicPosition);
            }
            player.start();
            System.out.println("Se esta reproduciendo");
            bar.setMax(player.getDuration());
            Play();
            btnPlay.setBackgroundResource(R.drawable.pause_white);
            createNotification();

            setTitle(titulo + " - " + artista);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    bar.setMax(mp.getDuration());
                    //Play();
                    //createNotification();
                }
            });

            endSong();
        } else {
            player.pause();
            musicPosition = player.getCurrentPosition();

            btnPlay.setBackgroundResource(R.drawable.white_play);
        }
    }

    public void endSong() {
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (player != null) {
                    btnPlay.setBackgroundResource(R.drawable.white_play);
                    musicPosition = 0;
                    Play();
                }
            }
        });
    }

    private void preparePlayer() {

        player = new MediaPlayer();
        player.setScreenOnWhilePlaying(true);
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(url);
            player.prepare();
            System.out.println("Reproduciendo ");
        } catch (IOException e) {
            System.out.println("Se produjo un error :  " + e.getMessage());
        }
    }

    private void displaySong(String titulo, String artista, String cover){
        System.out.println("Album: " + cover);

        TextView txtTitulo = findViewById(R.id.txtTitulo);
        TextView txtArtista = findViewById(R.id.txtArtista);
        txtTitulo.setText(titulo);
        txtArtista.setText(artista);
        ImageView img = findViewById(R.id.imgCover);
        CancionUtil util = new CancionUtil(BASE_URL_IMG + cover.replace(" ", "%20"));

        try {
            img.setImageBitmap((Bitmap) util.execute().get());
            createNotification();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void data() {

        songId = getIntent().getStringExtra("id");
        titulo = getIntent().getStringExtra("titulo");
        artista = getIntent().getStringExtra("artista");
        urlFile = getIntent().getStringExtra("file");
        cover = getIntent().getStringExtra("cover");
        position = getIntent().getStringExtra("position");
        System.out.println("Posicion recibida " + position);
        url = BASE_URL + urlFile;
    }

}
