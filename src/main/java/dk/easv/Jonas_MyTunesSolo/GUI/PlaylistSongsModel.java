package dk.easv.Jonas_MyTunesSolo.GUI;

import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.BLL.PlaylistSongsManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class PlaylistSongsModel {
    private PlaylistSongsManager playlistSongsManager;
    private ObservableList<Song> playlistSongsToBeViewed;

    public PlaylistSongsModel() {
            playlistSongsManager = new PlaylistSongsManager();
            playlistSongsToBeViewed = FXCollections.observableArrayList();

    }

    public ObservableList<Song> getPlaylistSongsToBeViewed() {
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
}
