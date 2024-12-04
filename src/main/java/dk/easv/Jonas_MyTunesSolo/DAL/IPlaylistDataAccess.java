package dk.easv.Jonas_MyTunesSolo.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;

import java.sql.SQLException;
import java.util.List;

public interface IPlaylistDataAccess {


    List<Playlist> getAllPlaylists();

    Playlist createPlaylist(Playlist newPlaylist) throws SQLException;

    void updatePlaylist(Playlist playlist) throws SQLServerException;

    void deletePlaylist(Playlist playlist);

}
