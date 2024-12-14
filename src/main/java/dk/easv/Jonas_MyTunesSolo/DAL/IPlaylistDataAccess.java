package dk.easv.Jonas_MyTunesSolo.DAL;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
//JAVA IMPORTS
import java.sql.SQLException;
import java.util.List;

public interface IPlaylistDataAccess {


    List<Playlist> getAllPlaylists();

    Playlist createPlaylist(Playlist newPlaylist) throws SQLException;

    void updatePlaylist(Playlist playlist) throws SQLException;

    void deletePlaylist(Playlist playlist) throws SQLException;

}
