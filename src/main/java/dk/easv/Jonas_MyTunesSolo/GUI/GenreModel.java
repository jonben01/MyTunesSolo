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

    /**
     * calls createGenre() in BLL and adds the new genre to the observable list
     * @param newGenre to pass to DAL
     * @throws SQLException if db issues
     */
    public void createGenre(Genre newGenre) throws SQLException {
        Genre genreCreated = genreManager.createGenre(newGenre);
        genresToBeViewed.add(genreCreated);
    }

    /**
     * @return observable list of genres
     */
    public ObservableList<Genre> getGenresToBeViewed() {
        return genresToBeViewed;
    }

    /**
     * calls deleteGenre() and removes from observable list
     * @param genreToBeDeleted to pass to DAL
     * @throws SQLException if db issues
     */
    public void deleteGenre(Genre genreToBeDeleted) throws SQLException {
        genresToBeViewed.remove(genreToBeDeleted);
        genreManager.deleteGenre(genreToBeDeleted);
    }

    //used to update GUI
    public void refreshGenre() {
        genresToBeViewed.clear();
        genresToBeViewed.addAll(genreManager.getAllGenres());
    }
}
