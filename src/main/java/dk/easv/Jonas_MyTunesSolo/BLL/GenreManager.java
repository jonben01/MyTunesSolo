package dk.easv.Jonas_MyTunesSolo.BLL;

import dk.easv.Jonas_MyTunesSolo.BE.Genre;
import dk.easv.Jonas_MyTunesSolo.DAL.GenreDAO_db;
import dk.easv.Jonas_MyTunesSolo.DAL.IGenreDataAccess;

import java.io.IOException;
import java.util.List;

public class GenreManager {

    private IGenreDataAccess genreDAO;

    public GenreManager() throws IOException {
        genreDAO = new GenreDAO_db();
    }

    public Genre createGenre(Genre newGenre) throws Exception {
        return genreDAO.createGenre(newGenre);
    }

    public List<Genre> getAllGenres() {
        return genreDAO.getAllGenres();
    }

    public void deleteGenre(Genre genreToBeDeleted) throws Exception {
        genreDAO.deleteGenre(genreToBeDeleted);
    }
}
