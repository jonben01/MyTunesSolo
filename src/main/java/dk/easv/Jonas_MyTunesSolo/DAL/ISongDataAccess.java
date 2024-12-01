package dk.easv.Jonas_MyTunesSolo.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Song;

import java.sql.SQLException;
import java.util.List;

public interface ISongDataAccess {

    //ADD CRUD METHODS TO INTERFACE

    List<Song> getAllSongs() throws SQLServerException;

    Song createSong(Song newSong) throws SQLServerException;

    void updateSong(Song song) throws SQLException;

    void deleteSong(Song song) throws SQLServerException;
}


