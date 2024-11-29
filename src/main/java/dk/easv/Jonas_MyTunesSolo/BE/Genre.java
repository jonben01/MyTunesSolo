package dk.easv.Jonas_MyTunesSolo.BE;

public class Genre {

    private int id;
    private String genreName;

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public int getId() {
        return id;
    }

    public void setId(int genreId) {
        this.id = id;
    }

    public Genre(int id, String genreName) {
        this.id = id;
        this.genreName = genreName;
    }

    @Override
    public String toString() {
        return genreName + "";
    }
}
