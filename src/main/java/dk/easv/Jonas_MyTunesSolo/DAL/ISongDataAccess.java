package dk.easv.Jonas_MyTunesSolo.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Song;

import java.util.List;

public interface ISongDataAccess {

    //ADD CRUD METHODS TO INTERFACE

    List<Song> getAllSongs() throws SQLServerException;

    Song createSong(Song newSong);

    void updateSong(Song song);

    void deleteSong(Song song);
}


