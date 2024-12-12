package dk.easv.Jonas_MyTunesSolo.DAL;

//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Genre;
//JAVA IMPORTS
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO_db implements IGenreDataAccess {

    private DBConnector dbConnector;

    public GenreDAO_db() throws IOException {
        dbConnector = new DBConnector();
    }

    /**
     * @return list of all Genre objects stored in the database
     * I dont know if I should call it a "genre object" in this case
     */
    @Override
    public List<Genre> getAllGenres() {
        ArrayList<Genre> allGenres  = new ArrayList<Genre>();
        //try with resources, auto closes database connection.
        try (Connection connection = dbConnector.getConnection(); Statement stmt = connection.createStatement()) {
            //gets all columns from Genre table
            String sql = "SELECT * FROM dbo.Genre ";

            ResultSet rs = stmt.executeQuery(sql);
            //as long as there a more rows of data, create Genre objects from the database info, and add them to the list.
            while(rs.next()) {

                int Id =rs.getInt("Id");
                String genreName = rs.getString("GenreName");

                Genre genre = new Genre(Id, genreName);
                allGenres.add(genre);
            }
            return allGenres;

        } catch (SQLException e) {
            //throwing runtime instead of SQL because I would have to try catch all my listeners in viewController.
            throw new RuntimeException("Could not get all genres", e);
        }
    }

    /**
     * @param newGenre to be added to the database
     * @return genreCreated.
     * @throws SQLException if db issues
     */
    @Override
    public Genre createGenre(Genre newGenre) throws SQLException {
        //SQL for inserting a new genre with the given name from the view.
        String sql = "INSERT INTO dbo.Genre (genreName) VALUES (?);";

        try (Connection conn = dbConnector.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,newGenre.getGenreName());

            Genre genreCreated = new Genre(newGenre.getId(), newGenre.getGenreName());

            stmt.executeUpdate();
            return genreCreated ;
        }
        catch (SQLException e)
        {
            throw new SQLException("Could not create genre", e);
        }
    }

    //deletes a genre.
    @Override
    public void deleteGenre(Genre genreToBeDeleted) throws SQLException {
        String sql = "DELETE FROM dbo.Genre WHERE Id = ?;";

        try (Connection conn = dbConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, genreToBeDeleted.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new SQLException("Could not delete genre", e);
        }
    }
}
