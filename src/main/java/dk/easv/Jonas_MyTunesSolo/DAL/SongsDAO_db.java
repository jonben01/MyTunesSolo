package dk.easv.Jonas_MyTunesSolo.DAL;


import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Song;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongsDAO_db implements ISongDataAccess {

    private DBConnector dbConnector;

    public SongsDAO_db() throws IOException {
        dbConnector = new DBConnector();
    }


    @Override
    public List<Song> getAllSongs() {
        ArrayList<Song> allSongs = new ArrayList<>();
        //try with resources, auto closes database connection.
        try (Connection connection = dbConnector.getConnection(); Statement stmt = connection.createStatement()) {
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

    @Override
    public Song createSong(Song newSong) {
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
            throw new RuntimeException(e);
        }
    }

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

    @Override
    public void deleteSong(Song songToBeDeleted) throws SQLServerException {
        String sql = "DELETE FROM dbo.Song WHERE Id = ?";

        // TODO Update song count on playlists that contain the deleted song, as many times as the song is on there :)






        try (Connection connection = dbConnector.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, songToBeDeleted.getSongID());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}