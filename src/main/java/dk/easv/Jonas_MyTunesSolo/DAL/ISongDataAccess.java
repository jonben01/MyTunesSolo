package dk.easv.Jonas_MyTunesSolo.DAL;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Song;
//LIBRARY IMPORTS
import com.microsoft.sqlserver.jdbc.SQLServerException;
//JAVA IMPORTS
import java.sql.SQLException;
import java.util.List;

public interface ISongDataAccess {

    //ADD CRUD METHODS TO INTERFACE

    List<Song> getAllSongs() throws SQLServerException;

    Song createSong(Song newSong) throws SQLException;

    void updateSong(Song song) throws SQLException;

    void deleteSong(Song song) throws SQLException;
}


