package dk.easv.Jonas_MyTunesSolo.GUI;

import dk.easv.Jonas_MyTunesSolo.BE.Genre;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.BLL.GenreManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;

public class GenreModel {

    private ObservableList<Genre> genresToBeViewed;
    private GenreManager genreManager;

    public GenreModel() throws IOException {
        genreManager = new GenreManager();
        genresToBeViewed = FXCollections.observableArrayList();
        genresToBeViewed.addAll(genreManager.getAllGenres());
    }

    public Genre createGenre(Genre newGenre) throws Exception {
        Genre genreCreated = genreManager.createGenre(newGenre);
        genresToBeViewed.add(genreCreated);
        return genreCreated;
    }

    public ObservableList<Genre> getGenresToBeViewed() {
        return genresToBeViewed;
    }

    public void deleteGenre(Genre genreToBeDeleted) throws Exception {
        genresToBeViewed.remove(genreToBeDeleted);
        genreManager.deleteGenre(genreToBeDeleted);
    }
    public void refreshGenre() {
        genresToBeViewed.clear();
        genresToBeViewed.addAll(genreManager.getAllGenres());
    }
}
