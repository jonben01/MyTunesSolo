package dk.easv.Jonas_MyTunesSolo.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaylistSongsDAO_db {

    private DBConnector dbConnector;

    public PlaylistSongsDAO_db() {
        try {
            dbConnector = new DBConnector();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public ObservableList<Song> getAllPlaylistSongs(Playlist playlist) {
        ObservableList<Song> allSongs = FXCollections.observableArrayList();
        String sql = "SELECT s.* " +
                     "FROM dbo.Song s " +
                     "JOIN PlaylistSongs ps " +
                     "ON s.Id = ps.SongId " +
                     "WHERE ps.PlaylistId = ?;";

        try (Connection connection = dbConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, playlist.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {

                int id = rs.getInt("Id");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                int genreId = rs.getInt("GenreId");

                int duration = rs.getInt("Duration");
                String file_path = rs.getString("File_Path");

                Song song = new Song(id, title, artist, genreId, file_path, duration);
                allSongs.add(song);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allSongs;
    }

    public void moveSongToPlaylist(Song songToMove, Playlist selectedPlaylist) throws SQLServerException {
        //COALESCE runs through the column to find non-null entries, if there are none its null and will default to 0 (+1)
        //MAX finds the maximum value (if there are any valid values) combined these find the maximum non-null value
        //This ensures we will always have a valid "nextOrderIndex" as if there are 0 songs, it will be 1, if there are 10 songs it will be 11
        String getOrderIndexSQL = "SELECT COALESCE(MAX(OrderIndex), 0) + 1 AS NextOrderIndex " +
                                  "FROM PlaylistSongs ps " +
                                  "WHERE ps.PlaylistId = ?;";

        String sql = "INSERT INTO dbo.PlaylistSongs (SongId, PlaylistId, OrderIndex) VALUES (?,?,?);";

        try(Connection connection = dbConnector.getConnection()) {
            int nextOrderIndex = 1;
            try(PreparedStatement pstmt = connection.prepareStatement(getOrderIndexSQL)) {
                //runs orderIndex sql where the selected playlists id matches.
                pstmt.setInt(1, selectedPlaylist.getId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    nextOrderIndex = rs.getInt("NextOrderIndex");
                }
            }
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, songToMove.getSongID());
                pstmt.setInt(2, selectedPlaylist.getId());
                pstmt.setInt(3, nextOrderIndex);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //TODO figure out why this shit doesnt run sometimes?
    public void deleteSongOnPlaylist(Song playlistSongToBeDeleted, Playlist playlistToDeleteFrom) throws SQLServerException {
        String deletionSQL = "DELETE FROM dbo.PlaylistSongs " +
                             "WHERE PlaylistId = ? AND SongId = ? AND OrderIndex = ?;";

        String getOrderIndexSQL = "SELECT OrderIndex " +
                                  "FROM dbo.PlaylistSongs " +
                                  "WHERE PlaylistId = ? AND SongId = ?;";

        String orderUpdateSQL = "UPDATE dbo.PlaylistSongs SET OrderIndex = OrderIndex -1 " +
                                "WHERE SongId = ? AND PlaylistId = ? AND OrderIndex > ?;";

        try (Connection connection = dbConnector.getConnection()) {
            //placeholder value
            int orderIndexToDelete = -1;
            try(PreparedStatement pstmt = connection.prepareStatement(getOrderIndexSQL)) {
                pstmt.setInt(1, playlistSongToBeDeleted.getSongID());
                pstmt.setInt(2, playlistToDeleteFrom.getId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    orderIndexToDelete = rs.getInt("OrderIndex");
                }
            }
            try (PreparedStatement pstmt = connection.prepareStatement(deletionSQL)) {
                pstmt.setInt(1, playlistToDeleteFrom.getId());
                pstmt.setInt(2, playlistSongToBeDeleted.getSongID());
                pstmt.setInt(3, orderIndexToDelete);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = connection.prepareStatement(orderUpdateSQL)) {
                pstmt.setInt(1, playlistSongToBeDeleted.getSongID());
                pstmt.setInt(2, playlistToDeleteFrom.getId());
                pstmt.setInt(3, orderIndexToDelete);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
