package dk.easv.Jonas_MyTunesSolo.DAL;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Genre;
//JAVA IMPORTS
import java.sql.SQLException;
import java.util.List;

public interface IGenreDataAccess {
    List<Genre> getAllGenres();

    Genre createGenre(Genre newGenre) throws SQLException;

    void deleteGenre(Genre genre) throws SQLException;
}
