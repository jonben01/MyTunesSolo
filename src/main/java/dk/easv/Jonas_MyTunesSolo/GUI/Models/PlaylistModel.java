package dk.easv.Jonas_MyTunesSolo.GUI.Models;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BLL.PlaylistManager;
//JAVA IMPORTS
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.sql.SQLException;

public class PlaylistModel {
    private PlaylistManager playlistManager;;
    private ObservableList<Playlist> playlistsToBeViewed;

    public PlaylistModel() throws IOException {

        playlistManager = new PlaylistManager();
        playlistsToBeViewed = FXCollections.observableArrayList();
        playlistsToBeViewed.addAll(playlistManager.getAllPlaylists());
    }

    /**
     * cals createPlaylist() in BLL and adds newPlaylist to observable list
     * @param newPlaylist to pass to DAL
     * @throws SQLException if db issues
     */
    public void createNewPlaylist(Playlist newPlaylist) throws SQLException {
        Playlist createdPlaylist = playlistManager.createPlaylist(newPlaylist);
        playlistsToBeViewed.add(createdPlaylist);
    }

    /**
     * @return observable list of playlists
     */
    public ObservableList<Playlist> getPlaylistsToBeViewed() {
        return playlistsToBeViewed;
    }

    //used to refresh GUI
    public void refreshPlaylist() {
        playlistsToBeViewed.clear();
        playlistsToBeViewed.addAll(playlistManager.getAllPlaylists());
    }

    /**
     * calls updatePlaylist() in BLL
     * @param playlistToBeEdited to pass to DAL
     * @throws SQLException if db issues
     */
    public void updatePlaylist(Playlist playlistToBeEdited) throws SQLException {
        playlistManager.updatePlaylist(playlistToBeEdited);
    }

    /**
     * calls deletePlaylist() in BLL and removes the playlistToBeDeleted from the observable list.
     * @param playlistToBeDeleted to pass to DAL
     * @throws SQLException if db issues
     */
    public void deletePlaylist(Playlist playlistToBeDeleted) throws SQLException {
        playlistsToBeViewed.remove(playlistToBeDeleted);
        playlistManager.deletePlaylist(playlistToBeDeleted);
    }

}
