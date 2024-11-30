package dk.easv.Jonas_MyTunesSolo.BLL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.DAL.SongsDAO_db;

import java.io.IOException;
import java.util.List;

public class SongManager {

    private SongsDAO_db SongsDAO;

    public SongManager() throws IOException {
        SongsDAO = new SongsDAO_db();
    }

    public List<Song> getAllSongs() {
        return SongsDAO.getAllSongs();
    }

    public Song createSong(Song newSong) throws SQLServerException {
        return SongsDAO.createSong(newSong);
    }
}
