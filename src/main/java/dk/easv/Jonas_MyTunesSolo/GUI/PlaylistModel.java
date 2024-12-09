package dk.easv.Jonas_MyTunesSolo.GUI;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BLL.PlaylistManager;
//LIBRARY IMPORTS
import com.microsoft.sqlserver.jdbc.SQLServerException;
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

    public void createNewPlaylist(Playlist newPlaylist) throws SQLException {
        Playlist createdPlaylist = playlistManager.createPlaylist(newPlaylist);
        playlistsToBeViewed.add(createdPlaylist);
    }

    public ObservableList<Playlist> getPlaylistsToBeViewed() {
        ObservableList<Playlist> playlists = FXCollections.observableArrayList(playlistsToBeViewed);
        return playlistsToBeViewed;
    }

    public void refreshPlaylist() {
        playlistsToBeViewed.clear();
        playlistsToBeViewed.addAll(playlistManager.getAllPlaylists());
    }

    public void updatePlaylist(Playlist playlistToBeEdited) throws SQLServerException {
        playlistManager.updatePlaylist(playlistToBeEdited);
    }

    public void deletePlaylist(Playlist playlistToBeDeleted) throws SQLServerException {
        playlistsToBeViewed.remove(playlistToBeDeleted);
        playlistManager.deleteSong(playlistToBeDeleted);
    }

}
