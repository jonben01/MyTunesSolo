package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Genre;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.GUI.GenreModel;
import dk.easv.Jonas_MyTunesSolo.GUI.SongModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Optional;
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
            songModel = new SongModel();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void btnHandleMenuAddSong(ActionEvent actionEvent) throws SQLServerException, IOException {

            String title = txtTitle.getText();
            String artist = txtArtist.getText();
            Genre selectedGenre = (Genre) this.cmbGenre.getSelectionModel().getSelectedItem();
            int duration = Integer.parseInt(txtDuration.getText());

            if (txtFilePath.getText() == null || txtFilePath.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please select a file");
                alert.showAndWait();
                return;
            }

            String songDestinationDir = "src/main/resources/Songs";
            Path songDestinationPath = Paths.get(songDestinationDir, new File(txtFilePath.getText()).getName());

            try {
                Files.copy(Paths.get(txtFilePath.getText()), songDestinationPath);
            } catch (FileAlreadyExistsException e) {
                //created an alert if a FileAlreadyExistsException has been caught.
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("FileAlreadyExistsException");
                alert.setHeaderText("File already exists");
                alert.setContentText("Replace existing file with new file?");

                Optional<ButtonType> result = alert.showAndWait();
                //if you click okay, it will override the currently existing file.
                if (result.get() == ButtonType.OK) {
                    //get songToBeDeleted if a song with the file path of the destination path exists
                    Song songToBeDeleted = songModel.getSongByFilePath(songDestinationPath.toString());
                    if (songToBeDeleted != null) {
                        //deletes SongToBeDeleted, so we dont keep a deleted song in the database
                        songModel.deleteSong(songToBeDeleted);
                    }
                    Files.copy(Paths.get(txtFilePath.getText()), songDestinationPath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    return;
                }

            }

            //not doing absolute path, I want it to be able to run on other computers
            String newFilePath = songDestinationPath.toString();

            //TODO FIGURE OUT HOW TO GET DURATION IN A GOOD WAY
            //USE MEDIAPLAYER CLASS TO GET DURATION, lavede det med David fredag d.29, se i discord

            //use ternary operator to let me pass a null value to the createSong method. sindsygt cool if/else statement alternativ
            Integer genreId = (selectedGenre != null) ? selectedGenre.getId() : null;

            Song newSong = new Song(-1, title, artist, genreId, newFilePath, duration);
            songModel.createSong(newSong);
    }

    public void btnHandleFileChooser(ActionEvent actionEvent) {
        //create file chooser object, with filters for relevant file types.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select audio file");
        //TODO MAYBE MAKE A INITIAL DIRECTORY (DESKTOP?)
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
                // Reset the flag after refreshing
                genreDataChanged.set(false);
            }
        });

    }
    public void setDataChangedFlag(SimpleBooleanProperty dataChangedFlag) {
        this.dataChangedFlag = dataChangedFlag;
    }



    private String formatDuration(int durationSeconds) {
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
