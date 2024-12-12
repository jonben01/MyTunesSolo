package dk.easv.Jonas_MyTunesSolo.BLL;

//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.DAL.IPlaylistDataAccess;
import dk.easv.Jonas_MyTunesSolo.DAL.PlaylistDAO_db;
//JAVA IMPORTS
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PlaylistManager {

    private IPlaylistDataAccess playlistDAO;


    public PlaylistManager() throws IOException {
        playlistDAO = new PlaylistDAO_db();
    }

    //this class is literally only used to pass stuff from DAL to GUI layer

    public Playlist createPlaylist(Playlist newPlaylist) throws SQLException {
        return playlistDAO.createPlaylist(newPlaylist);
    }

    public List<Playlist> getAllPlaylists() {
        return playlistDAO.getAllPlaylists();
    }

    public void updatePlaylist(Playlist playlistToBeEdited) throws SQLException {
        playlistDAO.updatePlaylist(playlistToBeEdited);
    }

    public void deletePlaylist(Playlist playlistToBeDeleted) throws SQLException {
        playlistDAO.deletePlaylist(playlistToBeDeleted);
    }
}
