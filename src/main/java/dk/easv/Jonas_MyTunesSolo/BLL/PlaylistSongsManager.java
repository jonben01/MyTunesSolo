package dk.easv.Jonas_MyTunesSolo.BLL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.DAL.PlaylistSongsDAO_db;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class PlaylistSongsManager {

    private PlaylistSongsDAO_db playlistSongsDAO;

    public PlaylistSongsManager() {
        playlistSongsDAO = new PlaylistSongsDAO_db();
    }

    public ObservableList<Song> getAllPlaylistSongs(Playlist playlist) {
        return playlistSongsDAO.getAllPlaylistSongs(playlist);
    }

    public void moveSongToPlaylist(Song songToMove, Playlist selectedPlaylist) throws SQLServerException {
        playlistSongsDAO.moveSongToPlaylist(songToMove, selectedPlaylist);
    }

    public void deleteSongOnPlaylist(Song playlistSongToBeDeleted, Playlist playlistToDeleteFrom) throws SQLServerException {
        playlistSongsDAO.deleteSongOnPlaylist(playlistSongToBeDeleted, playlistToDeleteFrom);
    }
}
