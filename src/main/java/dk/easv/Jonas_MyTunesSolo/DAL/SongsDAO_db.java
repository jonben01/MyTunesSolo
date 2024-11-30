package dk.easv.Jonas_MyTunesSolo.DAL;


import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Song;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongsDAO_db implements ISongDataAccess {

    private DBConnecter dbConnecter;

    public SongsDAO_db() throws IOException {
        dbConnecter = new DBConnecter();
    }


    @Override
    public List<Song> getAllSongs() {
        ArrayList<Song> allSongs = new ArrayList<>();
        //try with resources, auto closes database connection.
        try (Connection connection = dbConnecter.getConnection(); Statement stmt = connection.createStatement()) {
            String sql = "SELECT s.Id, s.Title, s.Artist, s.GenreId, g.GenreName AS genreName, s.Duration, s.File_Path " +
                         "FROM dbo.Song s " +
                         "LEFT JOIN dbo.Genre g ON s.GenreId = g.Id ";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {

                int id =rs.getInt("Id");
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
    public Song createSong(Song newSong) throws SQLServerException {
    String sql ="INSERT INTO dbo.Song (Title, Artist, ) VALUES (?,?,?,?,?,?);";

    try(Connection connection = dbConnecter.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, newSong.getTitle());
        pstmt.setString(2, newSong.getArtistName());
        pstmt.setString(3, newSong.getGenreName());
        pstmt.setString(4, newSong.getSongFilePath());
        pstmt.setInt(5, newSong.getDuration());

        //TODO fix genreName and genreId, so that we input into the song table, the id, but we're using



    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
        return null;
    }

    @Override
    public void updateSong(Song song) {

    }

    @Override
    public void deleteSong(Song song) {
    }
}
