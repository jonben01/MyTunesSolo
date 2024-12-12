package dk.easv.Jonas_MyTunesSolo.GUI;

//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.PlaylistSong;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.BLL.PlaylistSongsManager;
//JAVA IMPORTS
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public class PlaylistSongsModel {
    private PlaylistSongsManager playlistSongsManager;
    private ObservableList<PlaylistSong> playlistSongsToBeViewed;

    public PlaylistSongsModel() {
            playlistSongsManager = new PlaylistSongsManager();
            playlistSongsToBeViewed = FXCollections.observableArrayList();

    }

    /**
     * @return observable list of playlistSong objets
     */
    public ObservableList<PlaylistSong> getPlaylistSongsToBeViewed() {
        return playlistSongsToBeViewed;
    }

    /**
     * refresh observable list for GUI for the selected playlist
     * @param playlist selected playlist to refresh
     */
    public void refreshPlaylistSongs(Playlist playlist) {
        if (playlist == null) {
            playlistSongsToBeViewed.clear();
            return;
        }
        playlistSongsToBeViewed.clear();
        playlistSongsToBeViewed.addAll(playlistSongsManager.getAllPlaylistSongs(playlist));
    }

    /**
     * calls moveSongToPlaylist() in BLL and adds to observable list
     * @param songToMove song to move to playlist
     * @param selectedPlaylist playlist to move song to
     * @throws SQLException if db issues
     */
    public void moveSongToPlaylist(Song songToMove, Playlist selectedPlaylist) throws SQLException {
        PlaylistSong playlistSong = playlistSongsManager.moveSongToPlaylist(songToMove, selectedPlaylist);
        playlistSongsToBeViewed.add(playlistSong);
    }

    /**
     * calls deleteSongOnPlaylist in BLL and removes from observable list
     * @param playlistSong song to delete in DAL
     * @throws SQLException if db issues
     */
    public void deleteSongOnPlaylist(PlaylistSong playlistSong) throws SQLException {
        playlistSongsToBeViewed.remove(playlistSong);
        playlistSongsManager.deleteSongOnPlaylist(playlistSong);
    }

    /**
     * calls moveSongOnPlaylistDown() in BLL
     * @param playlistSong song to move down
     * @throws SQLException if db issues
     */
    public void moveSongOnPlaylistDown(PlaylistSong playlistSong) throws SQLException {
        playlistSongsManager.moveSongOnPlaylistDown(playlistSong);
    }

    /**
     * calls moveSongOnPlaylistDown() in BLL
     * @param playlistSong to move up
     * @throws SQLException if db issues
     */
    public void moveSongOnPlaylistUp(PlaylistSong playlistSong) throws SQLException {
        playlistSongsManager.moveSongOnPlaylistUp(playlistSong);
    }

}
