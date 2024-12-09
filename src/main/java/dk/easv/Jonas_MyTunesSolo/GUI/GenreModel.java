package dk.easv.Jonas_MyTunesSolo.GUI;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Genre;
import dk.easv.Jonas_MyTunesSolo.BLL.GenreManager;
//JAVA IMPORTS
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.sql.SQLException;

public class GenreModel {

    private ObservableList<Genre> genresToBeViewed;
    private GenreManager genreManager;

    public GenreModel() throws IOException {
        genreManager = new GenreManager();
        genresToBeViewed = FXCollections.observableArrayList();
        genresToBeViewed.addAll(genreManager.getAllGenres());
    }

    public Genre createGenre(Genre newGenre) throws SQLException {
        Genre genreCreated = genreManager.createGenre(newGenre);
        genresToBeViewed.add(genreCreated);
        return genreCreated;
    }

    public ObservableList<Genre> getGenresToBeViewed() {
        ObservableList<Genre> genres = FXCollections.observableArrayList(genresToBeViewed);
        return genresToBeViewed;
    }

    public void deleteGenre(Genre genreToBeDeleted) throws SQLException {
        genresToBeViewed.remove(genreToBeDeleted);
        genreManager.deleteGenre(genreToBeDeleted);
    }
    public void refreshGenre() {
        genresToBeViewed.clear();
        genresToBeViewed.addAll(genreManager.getAllGenres());
    }
}
