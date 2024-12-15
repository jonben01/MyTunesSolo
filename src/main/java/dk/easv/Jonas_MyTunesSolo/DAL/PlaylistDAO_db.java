package dk.easv.Jonas_MyTunesSolo.DAL;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
//JAVA IMPORTS
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO_db implements IPlaylistDataAccess{
    private DBConnector dbConnector;

    public PlaylistDAO_db() throws IOException {
        dbConnector = new DBConnector();
    }

    /**
     * gets all playlists in the database and pass them back up to be added to the observable list for playlists
     * @return a list of playlists in the database.
     */
    @Override
    public List<Playlist> getAllPlaylists() {
        ArrayList<Playlist> allPlaylists  = new ArrayList<Playlist>();
        //try with resources, auto closes database connection.
        try (Connection connection = dbConnector.getConnection(); Statement stmt = connection.createStatement()) {
            String sql = "SELECT * FROM dbo.Playlist ";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {

                int id =rs.getInt("Id");
                String name = rs.getString("Name");
                int songCount = rs.getInt("SongCount");

                Playlist playlist = new Playlist(id, name, songCount);
                allPlaylists.add(playlist);
            }
            return allPlaylists;

        } catch (SQLException e) {
            //throwing runtime here so I dont have to try catch listeners in view Controller
            // "have to"
            throw new RuntimeException("Could not get playlists",e);
        }
    }

    /**
     * creates a playlist in the database, and passes it back up to be added to the observable list
     * @param newPlaylist to be created in the database
     * @return the newly created playlist
     * @throws SQLException in case of db issues
     */
    @Override
    public Playlist createPlaylist(Playlist newPlaylist) throws SQLException {
        String sql = "INSERT INTO dbo.Playlist (Name) VALUES(?)";
        try (Connection connection = dbConnector.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPlaylist.getName());

            Playlist createdPlaylist = new Playlist(newPlaylist.getId(), newPlaylist.getName(), newPlaylist.getSongCount());
            pstmt.executeUpdate();
            return createdPlaylist;

        } catch (SQLException e) {
            throw new SQLException("Could not create new playlist", e);
        }
    }

    /**
     * Updates a playlist in the database.
     * @param playlistToBeEdited to be edited
     * @throws SQLException in case of db issues
     */
    @Override
    public void updatePlaylist(Playlist playlistToBeEdited) throws SQLException {
        String sql = "UPDATE dbo.Playlist SET Name = ? WHERE Id = ?";
        try (Connection connection = dbConnector.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playlistToBeEdited.getName());
            pstmt.setInt(2, playlistToBeEdited.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException("Could not update playlist", e);
        }

    }

    /**
     * Deletes a playlist from the database
     * @param playlist to be deleted
     * @throws SQLException in case of db issues
     */
    @Override
    public void deletePlaylist(Playlist playlist) throws SQLException {
        String sql = "DELETE FROM dbo.Playlist WHERE Id = ?";
        try(Connection connection = dbConnector.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, playlist.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException("Could not delete playlist", e);
        }
    }
}
