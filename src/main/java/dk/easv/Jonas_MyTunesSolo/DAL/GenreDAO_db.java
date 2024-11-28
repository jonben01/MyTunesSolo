package dk.easv.Jonas_MyTunesSolo.DAL;

import dk.easv.Jonas_MyTunesSolo.BE.Genre;
import dk.easv.Jonas_MyTunesSolo.BE.Song;


import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO_db implements IGenreDataAccess {

    private DBConnecter dbConnector;

    public GenreDAO_db() throws IOException {
        dbConnector = new DBConnecter();
    }

    @Override
    public List<Genre> getAllGenres() {
        ArrayList<Genre> allGenres  = new ArrayList<Genre>();
        //try with resources, auto closes database connection.
        try (Connection connection = dbConnector.getConnection(); Statement stmt = connection.createStatement()) {
            String sql = "SELECT * FROM dbo.Genre ";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {

                int Id =rs.getInt("Id");
                String genreName = rs.getString("GenreName");

                Genre genre = new Genre(Id, genreName);
                allGenres.add(genre);
            }
            return allGenres;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Genre createGenre(Genre newGenre) throws Exception {
        String sql = "INSERT INTO dbo.Genre (genreName) VALUES (?);";

        try (Connection conn = dbConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Bind parameters
            stmt.setString(1,newGenre.getGenreName());

            // Run the specified SQL statement
            stmt.executeUpdate();

            // Get the generated ID from the DB
            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;

            if (rs.next()) {
                id = rs.getInt(1);
            }

            // Create movie object and send up the layers
            Genre genreCreated = new Genre(id, newGenre.getGenreName());
            return genreCreated ;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            throw new Exception("Could not create movie", ex);
        }

    }

    @Override
    public void deleteGenre(Genre genre) {

    }

}
