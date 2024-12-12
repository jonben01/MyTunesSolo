package dk.easv.Jonas_MyTunesSolo.GUI;
//PROJECT IMPORTS
import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.BLL.SongManager;
//JAVA IMPORTS
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.List;

public class SongModel {

    private ObservableList<Song> songsToBeViewed;
    private SongManager songManager;

    public SongModel() throws Exception {
        songManager = new SongManager();
        songsToBeViewed = FXCollections.observableArrayList();
        songsToBeViewed.addAll(songManager.getAllSongs());
    }

    /**
     * @return observable list of song objects
     */
    public ObservableList<Song> getSongsToBeViewed() { return songsToBeViewed; }

    //used to refresh observable list when changes are made.
    public void refreshSong() {
        songsToBeViewed.clear();
        songsToBeViewed.addAll(songManager.getAllSongs());
    }

    /**
     * calls createSong in BLL and adds the newSong to the observable list
     * @param newSong song to be created in DAL
     */
    public void createSong(Song newSong) throws SQLException {
        Song songCreated = songManager.createSong(newSong);
        songsToBeViewed.add(songCreated);
    }

    /**
     * used to get a song object from its file path.
     * @param filePath to compare to songs
     * @return song with matching file path
     */
    public Song getSongByFilePath(String filePath) {
        //go through all songs
        for (Song song : songManager.getAllSongs()) {
            //Check if there's a song with the provided file path
            if (song.getSongFilePath().equals(filePath)) {
                //if there is, return that song
                return song;
            }
        }
        return null;
    }

    /**
     * used to get a song object from its id.
     * @param id to compare to song id
     * @return the song with matching id
     */
    public Song getSongByID(int id) {
        for (Song song : songManager.getAllSongs()) {
            if (song.getSongID() == id) {
                return song;
            }
        }
        return null;
    }

    /**
     * calls deleteSong() in BLL and removes the song to be deleted from the observable list
     * @param songToBeDeleted to pass selected object to DAL
     * @throws SQLServerException if db issues
     */
    public void deleteSong(Song songToBeDeleted) throws SQLException {
        songsToBeViewed.remove(songToBeDeleted);
        songManager.deleteSong(songToBeDeleted);
    }

    /**
     * calls updateSong() in BLL
     * @param songToBeEdited to pass selected object to DAL
     * @throws SQLException if db issues
     */
    public void updateSong(Song songToBeEdited) throws SQLException {
        songManager.updateSong(songToBeEdited);
    }

    /**
     * takes a query from the search txt field in viewController to pass to BLL
     * @param searchQuery query to compare songs to
     * @throws SQLServerException if issues with getting songs from db
     */
    public void searchSongs(String searchQuery) throws SQLServerException {
        List<Song> searchResults = songManager.searchSongs(searchQuery);
        songsToBeViewed.clear();
        songsToBeViewed.addAll(searchResults);
    }
}
