package dk.easv.Jonas_MyTunesSolo.DAL;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.PlaylistSong;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
//JAVA IMPORTS
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


    /**
     * gets all playlistSongs, adds them to observable list, and orders by orderIndex
     * @param playlist used to get songs through the junction table, where playlistId matches the input playlist
     * @return
     */
    public ObservableList<PlaylistSong> getAllPlaylistSongs(Playlist playlist)  {
        ObservableList<PlaylistSong> allPlaylistSongs = FXCollections.observableArrayList();
        //links a song to playlist through the PlaylistSong table
        //this retrieves all songs that are tied to the selected playlists id on the PlaylistSong table
        //and orders it by the OrderIndex column.
        String sql = "SELECT s.*, ps.* " +
                     "FROM dbo.Song s " +
                     "JOIN PlaylistSongs ps " +
                     "ON s.Id = ps.SongId " +
                     "WHERE ps.PlaylistId = ?" +
                     "ORDER BY OrderIndex;";

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
            //throwing runtime instead of SQL because I would have to try catch all my listeners in viewController.
            // "have to"
            throw new RuntimeException("Could not get all playlist songs", e);
        }
        return allPlaylistSongs;
    }

    /**
     *
     * @param songToMove object to be added to playlist
     * @param selectedPlaylist playlist that will get the song
     * @return a PlaylistSong object, which holds a reference to the Song object and a playlistId
     * @throws SQLException in case of db issues
     */
    public PlaylistSong moveSongToPlaylist(Song songToMove, Playlist selectedPlaylist) throws SQLException {
        Connection connection = null;
        //COALESCE runs through the column to find non-null entries, if there are none its null and will default to -1 (+1)
        //MAX finds the maximum value (if there are any valid values) combined these find the maximum non-null value
        //This ensures it will always have a valid "nextOrderIndex"
        String getOrderIndexSQL = "SELECT COALESCE(MAX(OrderIndex), -1) + 1 AS NextOrderIndex " +
                                  "FROM PlaylistSongs ps " +
                                  "WHERE ps.PlaylistId = ?;";

        //add a new entry to PlaylistSong table
        String sql = "INSERT INTO dbo.PlaylistSongs (SongId, PlaylistId, OrderIndex) VALUES (?,?,?);";

        //when this method runs, increase the songCount on the selected playlist by one
        String increaseSongCountSQL =  "UPDATE dbo.Playlist " +
                                       "SET SongCount = SongCount + 1" +
                                       "WHERE Id = ?;";
        try {
            connection = dbConnector.getConnection();
            connection.setAutoCommit(false);
            int nextOrderIndex = 0;

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
                pstmt.setInt(3, nextOrderIndex);
                pstmt.executeUpdate();

                connection.commit();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
            PlaylistSong playlistSong = new PlaylistSong(generatedId, selectedPlaylist.getId(), songToMove, nextOrderIndex);
            return playlistSong;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Could not move song to playlist, rollback failed", ex);
                }
            }
            throw new SQLException("Could not move song to playlist", e);
            //need to use a finally block, cause if the method runs well, my connection stays open :)
            //dont actually need to do this, couldve just used another try block, but I cba changing this now
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new SQLException("Could not close connection", e);
                }
            }
        }
    }

    /**
     * deletes the selected PlaylistSong object on the PlaylistSong table in the database.
     * @param playlistSong to be deleted
     * @throws SQLException in case of db issues
     */
    public void deleteSongOnPlaylist(PlaylistSong playlistSong) throws SQLException {
        Connection connection = null;
        String deletionSQL = "DELETE FROM dbo.PlaylistSongs " +
                             "WHERE PlaylistSongId = ?;";

        String decreaseSongCountSQL = "UPDATE dbo.Playlist " +
                                      "SET SongCount = SongCount - 1" +
                                      "WHERE Id = ?;";

        String orderUpdateSQL = "UPDATE dbo.PlaylistSongs " +
                                "SET OrderIndex = OrderIndex - 1 " +
                                "WHERE PlaylistId = ? AND OrderIndex > ?;";

        try {
            connection = dbConnector.getConnection();
            connection.setAutoCommit(false);

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
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Could not delete song on playlist, rollback failed", ex);
                }
                throw new SQLException("Could not delete song on playlist", e);
            }
            //need to use a finally block, cause if the method runs well, my connection stays open :)
            //actually i dont need this, if I just did another try block inside
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new SQLException("Could not close connection", e);
                }
            }
        }
    }

    /**
     * reduces the OrderIndex value on a selected songId on and increases the OrderIndex on the song above it.
     * @param playlistSong to be moved up on the playlist (reduced OrderIndex value)
     * @throws SQLException in case of db isses
     */
    public void moveSongOnPlaylistUp(PlaylistSong playlistSong) throws SQLException {
        Connection connection = null;
        //Cant move a song into the abyss.
        if (playlistSong.getOrderIndex() <= 0) {
            return;
        }
        int aboveOrderIndex = playlistSong.getOrderIndex() - 1;
        int selectedSongOrderIndex = playlistSong.getOrderIndex();


        String updateAboveSQL = "UPDATE dbo.PlaylistSongs " +
                                "SET OrderIndex = ?" +
                                "WHERE PlaylistId = ? AND OrderIndex = ?;";

        String updateSelectedSQL = "UPDATE dbo.PlaylistSongs " +
                                   "SET OrderIndex = ? " +
                                   "WHERE PlaylistSongId = ?;";

        try {
            //due to multiple queries in here I want to manually manage the connection, so I can rollback in case
            //of an exception.
            connection = dbConnector.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(updateAboveSQL)) {
                pstmt.setInt(1, selectedSongOrderIndex);
                pstmt.setInt(2, playlistSong.getPlaylistId());
                pstmt.setInt(3, aboveOrderIndex);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = connection.prepareStatement(updateSelectedSQL)) {
                pstmt.setInt(1, aboveOrderIndex);
                pstmt.setInt(2, playlistSong.getPsId());
                pstmt.executeUpdate();
            }
            playlistSong.setOrderIndex(aboveOrderIndex);
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Could not move song on playlist UP, rollback failed", ex);
                }
            }
            throw new SQLException("Could not move song on playlist UP", e);
            //need to use a finally block, cause if the method runs well, my connection stays open :)
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new SQLException("Could not close connection", e);
                }
            }
        }
    }

    /**
     * increases the OrderIndex value on a selected songId on and decreases the OrderIndex on the song below it.
     * @param playlistSong to be moved down on the playlist (increased OrderIndex value)
     * @throws SQLException in case of db isses
     */
    public void moveSongOnPlaylistDown(PlaylistSong playlistSong) throws SQLException {
        int maxOrderIndex;
        int belowOrderIndex = playlistSong.getOrderIndex() + 1;
        int selectedSongOrderIndex = playlistSong.getOrderIndex();
        Connection connection = null;

        String updateBelowSQL = "UPDATE dbo.PlaylistSongs " +
                                "SET OrderIndex = ?" +
                                "WHERE PlaylistId = ? AND OrderIndex = ?;";
        //get max order index for the playlist, so our song cant go above it.
        String maxOrderIndexSQL = "SELECT MAX(OrderIndex) AS MaxOrderIndex FROM dbo.PlaylistSongs " +
                                  "Where PlaylistId = ?;";

        String updateSelectedSQL = "UPDATE dbo.PlaylistSongs " +
                                   "SET OrderIndex = ? " +
                                   "WHERE PlaylistSongId = ?;";
        try {
            connection = dbConnector.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement pstmt = connection.prepareStatement(maxOrderIndexSQL)) {
                pstmt.setInt(1, playlistSong.getPlaylistId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        maxOrderIndex = rs.getInt("MaxOrderIndex");
                    } else {
                        return;
                    }
                }
            }
            if (playlistSong.getOrderIndex() >= maxOrderIndex) {
                return;
            }
            try (PreparedStatement pstmt = connection.prepareStatement(updateBelowSQL)) {
                pstmt.setInt(1, selectedSongOrderIndex);
                pstmt.setInt(2, playlistSong.getPlaylistId());
                pstmt.setInt(3, belowOrderIndex);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = connection.prepareStatement(updateSelectedSQL)) {
                pstmt.setInt(1, belowOrderIndex);
                pstmt.setInt(2, playlistSong.getPsId());
                pstmt.executeUpdate();
            }
            playlistSong.setOrderIndex(belowOrderIndex);
            connection.commit();
            //rolling back if an error occurs - i noticed sometimes when I spam some methods here, there's a gap in my orderIndex.
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Could not move song on playlist DOWN, rollback failed", ex);
                }
            }
            throw new SQLException("Could not move song on playlist DOWN", e);
            //need to use a finally block, cause if the method runs well, my connection stays open :)
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new SQLException("Could not move song on playlist DOWN 2", e);
                }
            }
        }
    }
}
