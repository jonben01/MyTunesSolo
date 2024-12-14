package dk.easv.Jonas_MyTunesSolo.DAL;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Song;
//JAVA IMPORTS
import java.sql.SQLException;
import java.util.List;

public interface ISongDataAccess {

    List<Song> getAllSongs() throws SQLException;

    Song createSong(Song newSong) throws SQLException;

    void updateSong(Song song) throws SQLException;

    void deleteSong(Song song) throws SQLException;
}


