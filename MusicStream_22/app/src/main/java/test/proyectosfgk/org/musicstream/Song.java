package test.proyectosfgk.org.musicstream;

public class Song {
    private String id;
    private String titulo;
    private String artista;
    private String urlFile;
    private String genero;

    private String cover;

    public Song(String id, String titulo, String artista, String urlFile, String cover, String genero) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.cover = cover;
        this.urlFile = urlFile;
        this.genero = genero;

    }
    public Song(){

    }


    //Getter and Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }


}
