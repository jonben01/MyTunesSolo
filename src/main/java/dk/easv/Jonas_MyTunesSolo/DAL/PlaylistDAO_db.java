package dk.easv.Jonas_MyTunesSolo.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO_db implements IPlaylistDataAccess{
    private DBConnector dbConnector;

    public PlaylistDAO_db() throws IOException {
        dbConnector = new DBConnector();
    }

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

                Playlist playlist = new Playlist(id, name);
                allPlaylists.add(playlist);
            }
            return allPlaylists;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Playlist createPlaylist(Playlist newPlaylist) throws SQLException {
        String sql = "INSERT INTO dbo.Playlist (Name) VALUES(?)";
        try (Connection connection = dbConnector.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, newPlaylist.getName());

            Playlist createdPlaylist = new Playlist(newPlaylist.getId(), newPlaylist.getName());
            pstmt.executeUpdate();
            return createdPlaylist;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePlaylist(Playlist playlistToBeEdited) throws SQLServerException {
        String sql = "UPDATE dbo.Playlist SET Name = ? WHERE Id = ?";
        try (Connection connection = dbConnector.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playlistToBeEdited.getName());
            pstmt.setInt(2, playlistToBeEdited.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deletePlaylist(Playlist playlist) throws SQLServerException {
        String sql = "DELETE FROM dbo.Playlist WHERE Id = ?";
        try(Connection connection = dbConnector.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, playlist.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
