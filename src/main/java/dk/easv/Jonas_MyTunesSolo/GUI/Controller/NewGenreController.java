package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

import dk.easv.Jonas_MyTunesSolo.BE.Genre;
import dk.easv.Jonas_MyTunesSolo.GUI.GenreModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class NewGenreController implements Initializable {
    @FXML
    public ListView lstGenres;
    public TextField txtGenreName;

    private GenreModel genreModel;

    public NewGenreController() {
        try {
            genreModel = new GenreModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lstGenres.setItems(genreModel.getGenresToBeViewed());
    }

    public void btnHandleAddGenre(ActionEvent actionEvent) throws Exception {
        String genreName = txtGenreName.getText();
        Genre newGenre = new Genre(-1, genreName);
        genreModel.createGenre(newGenre);
        txtGenreName.clear();
    }
}
