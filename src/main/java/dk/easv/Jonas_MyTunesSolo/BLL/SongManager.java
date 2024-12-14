package dk.easv.Jonas_MyTunesSolo.BLL;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.DAL.SongsDAO_db;
//JAVA IMPORTS
import java.io.IOException;
import java.sql.SQLException;

import java.util.List;

public class SongManager {

    private SongSearcher searcher = new SongSearcher();
    private SongsDAO_db SongsDAO;

    public SongManager() throws IOException {
        SongsDAO = new SongsDAO_db();
    }

    //this class is literally only used to pass stuff from DAL to GUI layer except the call to the SongSearcher class
    //but should probably have significantly more responsibility, I think I wouldve maybe moved my play methods here
    //but refactoring now is difficult.

    public List<Song> getAllSongs() {
        return SongsDAO.getAllSongs();
    }

    public Song createSong(Song newSong) throws SQLException {
        return SongsDAO.createSong(newSong);
    }

    public void deleteSong(Song songToBeDeleted) throws SQLException {
        SongsDAO.deleteSong(songToBeDeleted);
    }

    public void updateSong(Song songToBeEdited) throws SQLException {
        SongsDAO.updateSong(songToBeEdited);
    }

    /**
     * calls the search() method on the SongSearcher class with a query from the GUI.
     * @param searchQuery query from gui to compare song titles, artists and genres with
     * @return a list of songs containing the query
     */
    public List<Song> searchSongs(String searchQuery) {
        List<Song> allSongs = SongsDAO.getAllSongs();
        List<Song> searchedResults = searcher.search(allSongs, searchQuery);
        return searchedResults;
    }
}
