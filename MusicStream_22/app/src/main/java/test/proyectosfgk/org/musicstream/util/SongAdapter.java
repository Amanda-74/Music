package test.proyectosfgk.org.musicstream.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import test.proyectosfgk.org.musicstream.PlaySongActivity;
import test.proyectosfgk.org.musicstream.R;
import test.proyectosfgk.org.musicstream.Song;
import test.proyectosfgk.org.musicstream.SongServices;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private static final String BASE_URL_IMG = "http://jws-app-musicapp.7e14.starter-us-west-2.openshiftapps.com/album/portada?album=";
    private SongServices songServices = new SongServices();
    private List<Song> canciones;

    public SongAdapter(List<Song> list) {
        this.canciones = list;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new SongViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int position) {
        Song song = canciones.get(position);

        songViewHolder.tvTitulo.setText(song.getTitulo());
        try {
            songViewHolder.tvAlbum.setImageBitmap((Bitmap) new CancionUtil(BASE_URL_IMG +
                    song.getCover().replace(" ", "%20")).execute().get());

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        songViewHolder.tvArtista.setText(song.getArtista());
    }

    @Override
    public int getItemCount() {
        return canciones.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircleImageView tvAlbum;
        private TextView tvTitulo;
        private TextView tvArtista;

        public SongViewHolder(View v) {
            super(v);
            itemView.setOnClickListener(this);
            tvAlbum = (CircleImageView) v.findViewById(R.id.ivAlbum);
            tvArtista = (TextView) v.findViewById(R.id.tvArtista);
            tvTitulo = (TextView) v.findViewById(R.id.tvTitulo);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            int position = getAdapterPosition();
            Song cancionSelected = canciones.get(position);
            Song cancion = songServices.buscar(cancionSelected.getTitulo(), cancionSelected.getArtista());

            Intent intent = new Intent(v.getContext(), PlaySongActivity.class);
            intent.putExtra("id", cancion.getId());
            intent.putExtra("titulo", cancion.getTitulo());
            intent.putExtra("artista", cancion.getArtista());
            intent.putExtra("file", cancion.getUrlFile());
            intent.putExtra("cover", cancion.getCover());
            //intent.putExtra(
            intent.putExtra("position", Integer.toString(position));

            context.startActivity(intent);
        }
    }

}
