package dk.easv.Jonas_MyTunesSolo.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.PlaylistSong;
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


    public ObservableList<PlaylistSong> getAllPlaylistSongs(Playlist playlist) {
        ObservableList<PlaylistSong> allPlaylistSongs = FXCollections.observableArrayList();
        String sql = "SELECT s.*, ps.* " +
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

                int psId = rs.getInt("PlaylistSongId");
                int playlistId = rs.getInt("PlaylistId");
                int orderIndex = rs.getInt("OrderIndex");

                PlaylistSong playlistSong = new PlaylistSong(psId, playlistId, song, orderIndex);
                allPlaylistSongs.add(playlistSong);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allPlaylistSongs;
    }

    public PlaylistSong moveSongToPlaylist(Song songToMove, Playlist selectedPlaylist) throws SQLServerException {
        //COALESCE runs through the column to find non-null entries, if there are none its null and will default to 0 (+1)
        //MAX finds the maximum value (if there are any valid values) combined these find the maximum non-null value
        //This ensures we will always have a valid "nextOrderIndex" as if there are 0 songs, it will be 1, if there are 10 songs it will be 11
        String getOrderIndexSQL = "SELECT COALESCE(MAX(OrderIndex), 0) + 1 AS NextOrderIndex " +
                                  "FROM PlaylistSongs ps " +
                                  "WHERE ps.PlaylistId = ?;";

        String sql = "INSERT INTO dbo.PlaylistSongs (SongId, PlaylistId, OrderIndex) VALUES (?,?,?);";

        String increaseSongCountSQL =  "UPDATE dbo.Playlist " +
                                       "SET SongCount = SongCount + 1" +
                                       "WHERE Id = ?;";

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
            try (PreparedStatement pstmt = connection.prepareStatement(increaseSongCountSQL)) {
                pstmt.setInt(1, selectedPlaylist.getId());
                pstmt.executeUpdate();
            }

            Integer generatedId = null;
            try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, songToMove.getSongID());
                pstmt.setInt(2, selectedPlaylist.getId());
                //TODO make entry id work
                // put it before
                pstmt.setInt(3, nextOrderIndex);
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        //songToMove.setPSId(Id);
                    } else {
                        throw new SQLException("No ID generated for song to move");
                    }
                }
            }

            PlaylistSong playlistSong = new PlaylistSong(generatedId, selectedPlaylist.getId(), songToMove, nextOrderIndex);

            return playlistSong;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //TODO make sure this works when we start editing the order of a playlist
    public void deleteSongOnPlaylist(PlaylistSong playlistSong) throws SQLServerException {
        String deletionSQL = "DELETE FROM dbo.PlaylistSongs " +
                             "WHERE PlaylistSongId = ?;";

        String decreaseSongCountSQL = "UPDATE dbo.Playlist " +
                                      "SET SongCount = SongCount - 1" +
                                      "WHERE Id = ?;";


        String orderUpdateSQL = "UPDATE dbo.PlaylistSongs " +
                                "SET OrderIndex = OrderIndex - 1 " +
                                "WHERE PlaylistId = ? AND OrderIndex > ?;";

        try (Connection connection = dbConnector.getConnection()) {

            try (PreparedStatement pstmt = connection.prepareStatement(deletionSQL)) {
                pstmt.setInt(1, playlistSong.getPsId());
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = connection.prepareStatement(decreaseSongCountSQL)) {
                pstmt.setInt(1, playlistSong.getPlaylistId());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = connection.prepareStatement(orderUpdateSQL)) {
                pstmt.setInt(1, playlistSong.getPlaylistId());
                pstmt.setInt(2, playlistSong.getOrderIndex());
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
