package dk.easv.Jonas_MyTunesSolo.DAL;

import dk.easv.Jonas_MyTunesSolo.BE.Genre;

import java.sql.SQLException;
import java.util.List;

public interface IGenreDataAccess {
    List<Genre> getAllGenres();

    Genre createGenre(Genre newGenre) throws Exception;


    void deleteGenre(Genre genre) throws SQLException;
}
