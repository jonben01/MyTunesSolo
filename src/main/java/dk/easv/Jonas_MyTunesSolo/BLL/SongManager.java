package dk.easv.Jonas_MyTunesSolo.BLL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.DAL.SongsDAO_db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongManager {

    private SongSearcher searcher = new SongSearcher();
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

    public void deleteSong(Song songToBeDeleted) throws SQLServerException {
        SongsDAO.deleteSong(songToBeDeleted);
    }

    public void updateSong(Song songToBeEdited) throws SQLException {
        SongsDAO.updateSong(songToBeEdited);
    }
    public List<Song> searchSongs(String searchQuery) throws SQLServerException {
        List<Song> allSongs = SongsDAO.getAllSongs();
        List<Song> searchedResults = searcher.search(allSongs, searchQuery);
        return searchedResults;
    }
}
