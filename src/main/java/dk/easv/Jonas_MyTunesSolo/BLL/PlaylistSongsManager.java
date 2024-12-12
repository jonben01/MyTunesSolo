package dk.easv.Jonas_MyTunesSolo.BLL;

//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.PlaylistSong;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.DAL.PlaylistSongsDAO_db;
//LIBRARY IMPORTS
import com.microsoft.sqlserver.jdbc.SQLServerException;
//JAVA IMPORTS
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.List;

public class PlaylistSongsManager {

    private PlaylistSongsDAO_db playlistSongsDAO;

    public PlaylistSongsManager() {
        playlistSongsDAO = new PlaylistSongsDAO_db();
    }

    //this class is literally only used to pass stuff from DAL to GUI layer

    public ObservableList<PlaylistSong> getAllPlaylistSongs(Playlist playlist) {
        return playlistSongsDAO.getAllPlaylistSongs(playlist);
    }

    public PlaylistSong moveSongToPlaylist(Song songToMove, Playlist selectedPlaylist) throws SQLServerException {
        return playlistSongsDAO.moveSongToPlaylist(songToMove, selectedPlaylist);
    }

    public void deleteSongOnPlaylist(PlaylistSong playlistSong) throws SQLServerException {
        playlistSongsDAO.deleteSongOnPlaylist(playlistSong);
    }


    public void moveSongOnPlaylistDown(PlaylistSong playlistSong) throws SQLException {
        playlistSongsDAO.moveSongOnPlaylistDown(playlistSong);
    }

    public void moveSongOnPlaylistUp(PlaylistSong playlistSong) throws SQLServerException {
        playlistSongsDAO.moveSongOnPlaylistUp(playlistSong);
    }
}
