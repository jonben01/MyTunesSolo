package dk.easv.Jonas_MyTunesSolo.DAL;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Song;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
            String sql = "SELECT * FROM songs";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {

                int id =rs.getInt("Id");
                String title = rs.getString("Title");
                int Artist_id = rs.getInt("ArtistId");
                int GenreId = rs.getInt("GenreId");
                String file_path = rs.getString("File_Path");
            }
            return allSongs;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Song createSong(Song newSong) {
        return null;
    }

    @Override
    public void updateSong(Song song) {

    }

    @Override
    public void deleteSong(Song song) {
    }
}
