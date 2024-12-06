package dk.easv.Jonas_MyTunesSolo.BLL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.PlaylistSong;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.DAL.PlaylistSongsDAO_db;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public class PlaylistSongsManager {

    private PlaylistSongsDAO_db playlistSongsDAO;

    public PlaylistSongsManager() {
        playlistSongsDAO = new PlaylistSongsDAO_db();
    }

    public ObservableList<PlaylistSong> getAllPlaylistSongs(Playlist playlist) {
        return playlistSongsDAO.getAllPlaylistSongs(playlist);
    }

    public PlaylistSong moveSongToPlaylist(Song songToMove, Playlist selectedPlaylist) throws SQLServerException {
        return playlistSongsDAO.moveSongToPlaylist(songToMove, selectedPlaylist);
    }

    public void deleteSongOnPlaylist(PlaylistSong playlistSong) throws SQLServerException {
        playlistSongsDAO.deleteSongOnPlaylist(playlistSong);
    }


    public void moveSongOnPlaylistDown(PlaylistSong playlistSong, List<PlaylistSong> playlistSongList) throws SQLException {
        playlistSongsDAO.moveSongOnPlaylistDown(playlistSong, playlistSongList);
    }

    public void moveSongOnPlaylistUp(PlaylistSong playlistSong, List<PlaylistSong> playlistSongList) throws SQLServerException {
        playlistSongsDAO.moveSongOnPlaylistUp(playlistSong, playlistSongList);
    }
}
