package dk.easv.Jonas_MyTunesSolo.BLL;

//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Genre;
import dk.easv.Jonas_MyTunesSolo.DAL.GenreDAO_db;
import dk.easv.Jonas_MyTunesSolo.DAL.IGenreDataAccess;
//JAVA IMPORTS
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GenreManager {

    private IGenreDataAccess genreDAO;

    //this class is literally only used to pass stuff from DAL to GUI layer

    public GenreManager() throws IOException {
        genreDAO = new GenreDAO_db();
    }

    public Genre createGenre(Genre newGenre) throws SQLException {
        return genreDAO.createGenre(newGenre);
    }

    public List<Genre> getAllGenres() {
        return genreDAO.getAllGenres();
    }

    public void deleteGenre(Genre genreToBeDeleted) throws SQLException {
        genreDAO.deleteGenre(genreToBeDeleted);
    }
}
