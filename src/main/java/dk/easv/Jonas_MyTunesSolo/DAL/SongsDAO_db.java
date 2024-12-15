package dk.easv.Jonas_MyTunesSolo.DAL;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Song;
//JAVA IMPORTS
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class SongsDAO_db implements ISongDataAccess {

    private DBConnector dbConnector;

    public SongsDAO_db() throws IOException {
        dbConnector = new DBConnector();
    }


    /**
     * gets all songs, and joins a genreName to the song, so it can be displayed directly
     * @return a list of song Objects to be added to observable list.
     */
    @Override
    public List<Song> getAllSongs() {
        ArrayList<Song> allSongs = new ArrayList<>();
        //try with resources, auto closes database connection.
        try (Connection connection = dbConnector.getConnection(); Statement stmt = connection.createStatement()) {
            //left join to get results from the genre table only where it matches, but still getting all songs.
            String sql = "SELECT s.Id, s.Title, s.Artist, s.GenreId, g.GenreName AS genreName, s.Duration, s.File_Path " +
                         "FROM dbo.Song s " +
                         "LEFT JOIN dbo.Genre g ON s.GenreId = g.Id ";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                int id = rs.getInt("Id");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                String genreName = rs.getString("GenreName");
                if (genreName == null) {
                    genreName = "Unknown";
                }
                int duration = rs.getInt("Duration");
                String file_path = rs.getString("File_Path");

                Song song = new Song(id, title, artist, genreName, file_path, duration);
                allSongs.add(song);
            }
            return allSongs;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * adds an entry to the database and pass that entry up the chain
     * @param newSong to be created
     * @return song created
     * @throws SQLException in case of db issues
     */
    @Override
    public Song createSong(Song newSong) throws SQLException {
        String sql = "INSERT INTO dbo.Song (Title, Artist, GenreId, Duration, File_Path ) VALUES (?,?,?,?,?);";

        try (Connection connection = dbConnector.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, newSong.getTitle());
            pstmt.setString(2, newSong.getArtistName());

            //If genreId is null it sets the sql INTEGER column value on my new row to null, if not null proceeds as normal
            if (newSong.getGenreId() == null) {
                pstmt.setNull(3, Types.INTEGER);
            } else {
                pstmt.setInt(3, newSong.getGenreId());
            }
            pstmt.setInt(4, newSong.getDuration());
            pstmt.setString(5, newSong.getSongFilePath());

            Song songCreated = new Song(newSong.getSongID(), newSong.getTitle(), newSong.getArtistName(), newSong.getGenreName(),
                                        newSong.getSongFilePath(), newSong.getDuration());
            pstmt.executeUpdate();

            return songCreated;

        } catch (SQLException e) {
            throw new SQLException("Could not create new song", e);
        }
    }

    /**
     * updates a song entry in the database, with either a new title, artist or genreId
     * @param songToBeEdited object to be edited in database
     * @throws SQLException in case of db issues
     */
    @Override
    public void updateSong(Song songToBeEdited) throws SQLException {

        //this works 😂
        songToBeEdited.setGenreId(getGenreIdByName(songToBeEdited.getGenreName()));
        String sql = "UPDATE dbo.Song SET title = ?, Artist = ?, GenreId = ? WHERE Id = ?";
        try (Connection connection = dbConnector.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, songToBeEdited.getTitle());
            pstmt.setString(2, songToBeEdited.getArtistName());

            if (songToBeEdited.getGenreId() == null) {
                pstmt.setNull(3, Types.INTEGER);
            } else {
                pstmt.setInt(3, songToBeEdited.getGenreId());
            }
            pstmt.setInt(4, songToBeEdited.getSongID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Could not update song",e);
        }
    }

    //I wish I didnt need this right now, but i cant figure out how to make things work without it
    //this still works if the genreName isnt in the database, because of if statement in update method
    public Integer getGenreIdByName(String genreName) throws SQLException {
        String sql = "SELECT Id FROM dbo.Genre WHERE GenreName = ?";
        try (Connection connection = dbConnector.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, genreName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Id");
            }
        }
        return null;
    }

    /**
     * Deletes a song from the song table, and also updates the song count of playlists if the song is on the playlistSong table.
     * @param songToBeDeleted object to be removed from db
     * @throws SQLException in case of db issues
     */
    @Override
    public void deleteSong(Song songToBeDeleted) throws SQLException {
        Connection connection = null;

        String getAffectPlaylistsSQL =
                        "SELECT PlaylistId, COUNT(*) AS Occurrences " +
                        "FROM dbo.PlaylistSongs WHERE SongID = ? " +
                        "GROUP BY PlaylistId;";
        String updateSongCountsSQL =
                        "UPDATE dbo.Playlist " +
                        "SET SongCount = SongCount - ? " +
                        "WHERE Id = ?;";
        String deleteSongSQL =
                        "DELETE FROM dbo.Song " +
                        "WHERE Id = ?;";
        try {
            //due to multiple queries in here I want to manually manage the connection, so I can rollback in case
            //of an exception.
            connection = dbConnector.getConnection();
            connection.setAutoCommit(false);
            Map<Integer, Integer> affectedSongCounts = new HashMap<>();

            try (PreparedStatement pstmt = connection.prepareStatement(getAffectPlaylistsSQL)) {
                pstmt.setInt(1, songToBeDeleted.getSongID());
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("PlaylistId");
                    int count = rs.getInt("Occurrences");
                    affectedSongCounts.put(id, count);
                }
                rs.close();
            }
            //uses the key-value hashmap to update song count on affected playlists
            //the map ensures an affected playlist is updated the correct amount of times, if a song is on it multiple times.
            if (!affectedSongCounts.isEmpty()) {
                try (PreparedStatement pstmt = connection.prepareStatement(updateSongCountsSQL)) {
                    for (Map.Entry<Integer, Integer> entry : affectedSongCounts.entrySet()) {
                        int id = entry.getKey();
                        int count = entry.getValue();

                        pstmt.setInt(1, count);
                        pstmt.setInt(2, id);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }
            try(PreparedStatement pstmt = connection.prepareStatement(deleteSongSQL)) {
                pstmt.setInt(1, songToBeDeleted.getSongID());
                pstmt.executeUpdate();
            }
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Could not delete song, rollback failed",ex);
                }
                throw new SQLException("Could not delete song", e);
            }
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
}