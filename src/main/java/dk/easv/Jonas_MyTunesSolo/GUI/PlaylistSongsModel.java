package dk.easv.Jonas_MyTunesSolo.GUI;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.PlaylistSong;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.BLL.PlaylistSongsManager;
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

    public ObservableList<PlaylistSong> getPlaylistSongsToBeViewed() {
        //ObservableList<Song> playlistSongs = FXCollections.observableArrayList(playlistSongsManager.getAllPlaylistSongs(playlist));
        return playlistSongsToBeViewed;
    }

    public void refreshPlaylistSongs(Playlist playlist) {
        if (playlist == null) {
            playlistSongsToBeViewed.clear();
            return;
        }
        playlistSongsToBeViewed.clear();
        playlistSongsToBeViewed.addAll(playlistSongsManager.getAllPlaylistSongs(playlist));
    }

    public void moveSongToPlaylist(Song songToMove, Playlist selectedPlaylist) throws SQLServerException {
        PlaylistSong playlistSong = playlistSongsManager.moveSongToPlaylist(songToMove, selectedPlaylist);
        playlistSongsToBeViewed.add(playlistSong);
    }

    public void deleteSongOnPlaylist(PlaylistSong playlistSong) throws SQLServerException {
        playlistSongsToBeViewed.remove(playlistSong.getSong().getSongID());
        playlistSongsManager.deleteSongOnPlaylist(playlistSong);
    }
}
