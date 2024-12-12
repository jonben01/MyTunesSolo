package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Genre;
import dk.easv.Jonas_MyTunesSolo.GUI.GenreModel;
//JAVA IMPORTS
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NewGenreController implements Initializable {
    @FXML public ListView<Genre> lstGenres;
    @FXML public TextField txtGenreName;
    @FXML public Button btnAddGenre;
    @FXML public Button btnDeleteGenre;
    public Button btnCancelGenre;

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
        lstGenres.getSelectionModel().selectedItemProperty().addListener(observable -> {
            txtGenreName.setText(lstGenres.getSelectionModel().getSelectedItem().toString());
        });

    }

    @FXML
    public void btnHandleAddGenre(ActionEvent actionEvent) throws SQLException {
        String genreName = txtGenreName.getText();
        Genre newGenre = new Genre(-1, genreName);

        if (genreName != null && !genreName.isEmpty()) {
        genreModel.createGenre(newGenre);
        txtGenreName.clear();
        if (genreDataChangedFlag != null) {
            genreDataChangedFlag.set(true);
            }
        }
        Stage stage = (Stage) btnAddGenre.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void btnHandleDeleteGenre(ActionEvent actionEvent) throws SQLException {
        Genre genreToBeDeleted = lstGenres.getSelectionModel().getSelectedItem();

        if (genreToBeDeleted != null) {
            genreModel.deleteGenre(genreToBeDeleted);
            lstGenres.setItems(genreModel.getGenresToBeViewed());
            if (dataChangedFlag != null) {
                dataChangedFlag.set(true);
            }
        }
    }

    //used to refresh GUI in other views
    public void setDataChangedFlag(SimpleBooleanProperty dataChangedFlag) {
        this.dataChangedFlag = dataChangedFlag;
    }
    //used to refresh GUI in new song view
    public void setGenreDataChangedFlag(SimpleBooleanProperty genreDataChangedFlag) {
        this.genreDataChangedFlag = genreDataChangedFlag;
    }

    //close the stage
    @FXML
    public void btnHandleCancelGenre(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancelGenre.getScene().getWindow();
        stage.close();
    }
}
