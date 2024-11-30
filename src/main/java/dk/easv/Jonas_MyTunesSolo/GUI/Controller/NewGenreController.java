package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

import dk.easv.Jonas_MyTunesSolo.BE.Genre;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.GUI.GenreModel;
import dk.easv.Jonas_MyTunesSolo.GUI.SongModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class NewGenreController implements Initializable {
    @FXML
    public ListView<Genre> lstGenres;
    public TextField txtGenreName;

    private GenreModel genreModel;
    private SimpleBooleanProperty dataChangedFlag;
    private SimpleBooleanProperty genreDataChangedFlag;

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
        if (genreDataChangedFlag != null) {
            genreDataChangedFlag.set(true);
        }

    }

    public void btnHandleDeleteGenre(ActionEvent actionEvent) throws Exception {
        Genre genreToBeDeleted = lstGenres.getSelectionModel().getSelectedItem();

        if (genreToBeDeleted != null) {
            genreModel.deleteGenre(genreToBeDeleted);
            lstGenres.setItems(genreModel.getGenresToBeViewed());
            if (dataChangedFlag != null) {
                dataChangedFlag.set(true);
            }
        }
    }
    public void setDataChangedFlag(SimpleBooleanProperty dataChangedFlag) {
        this.dataChangedFlag = dataChangedFlag;
    }
    public void setGenreDataChangedFlag(SimpleBooleanProperty genreDataChangedFlag) {
        this.genreDataChangedFlag = genreDataChangedFlag;
    }


}
