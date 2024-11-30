package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Genre;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.GUI.GenreModel;
import dk.easv.Jonas_MyTunesSolo.GUI.SongModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewSongController implements Initializable {
    @FXML
    public TextField txtFilePath;
    public TextField txtArtist;
    public TextField txtTitle;
    public ComboBox cmbGenre;
    private GenreModel genreModel;
    private SongModel songModel;
    private SimpleBooleanProperty dataChangedFlag;
    private SimpleBooleanProperty genreDataChanged;
    public TextField txtDuration;

    public NewSongController(){

        try {
            genreModel = new GenreModel();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnHandleMenuAddSong(ActionEvent actionEvent) throws SQLServerException {
        String title = txtTitle.getText();
        String artist = txtArtist.getText();
        String filePath = this.txtFilePath.getText();
        String genre = this.cmbGenre.getSelectionModel().getSelectedItem().toString(); //CHANGE THIS
        int duration = Integer.parseInt(this.txtDuration.getText());

        //TODO FIGURE OUT HOW TO GET DURATION IN A GOOD WAY
        //And make combobox give a genreId instead of the name

        Song newSong = new Song(-1, title, artist, genre, filePath, duration);
        songModel.createSong(newSong);

    }

    public void btnHandleFileChooser(ActionEvent actionEvent) {
        //create file chooser object, with filters for relevant file types.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select audio file");
        //MAYBE MAKE A INITIAL DIRECTORY (DESKTOP?)
        //fileChooser.setInitialDirectory

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
        //open file chooser dialog window to find file
        File file = fileChooser.showOpenDialog(null);
        //if file exists update text field
        if (file != null) {
            txtFilePath.setText(file.getAbsolutePath());
        }
    }

    public void btnHandleAddGenre(ActionEvent actionEvent) {
        try {
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/genre-view.fxml"));
            Parent root = fxmlLoader.load();

            NewGenreController NGController = fxmlLoader.getController();
            NGController.setDataChangedFlag(dataChangedFlag);
            NGController.setGenreDataChangedFlag(genreDataChanged);

            // Create a new stage
            Stage newGenreStage = new Stage();
            newGenreStage.setTitle("New genre");
            newGenreStage.setResizable(false);
            //Sets modality, cant use previous window till this one is closed
            newGenreStage.initModality(Modality.APPLICATION_MODAL);

            // Set the scene with the loaded FXML file
            newGenreStage.setScene(new Scene(root, 500, 400));

            // Show the stage
            newGenreStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbGenre.setItems(genreModel.getGenresToBeViewed());

        genreDataChanged = new SimpleBooleanProperty(false);
        genreDataChanged.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                genreModel.refreshGenre();
                genreDataChanged.set(false); // Reset the flag
            }
        });

    }
    public void setDataChangedFlag(SimpleBooleanProperty dataChangedFlag) {
        this.dataChangedFlag = dataChangedFlag;
    }

}
