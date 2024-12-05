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
        String sql = "SELECT s.* FROM dbo.Song s " +
                     "JOIN PlaylistSongs ps ON s.Id = ps.SongId " +
                     "WHERE ps.PlaylistId = ?";

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
        //MAX finds the maximum value (if there are any valid values) combined
        //This ensures we will always have a valid "nextOrderIndex" as if there are 0 songs, it will be 1, if there are 10 songs it will be 11
        String getOrderIndexSQL = "SELECT COALESCE(MAX(OrderIndex), 0) + 1 AS NextOrderIndex " +
                                  "From PlaylistSongs ps " +
                                  "Where ps.PlaylistId = ? ";

        String sql = "INSERT INTO dbo.PlaylistSongs (SongId, PlaylistId, OrderIndex) VALUES (?,?,?)";

        try(Connection connection = dbConnector.getConnection()) {
            //not sure if needed due to wording in getOrderIndexSQL, but if it is it will set a default value of 1
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
}
