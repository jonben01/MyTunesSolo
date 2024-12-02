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
import javafx.scene.control.*;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.media.Media;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class NewSongController implements Initializable {
    @FXML
    public TextField txtFilePath;
    @FXML
    public TextField txtArtist;
    @FXML
    public TextField txtTitle;
    @FXML
    public ComboBox cmbGenre;
    @FXML
    public TextField txtDuration;
    @FXML
    public Button btnMenuAddSong;
    @FXML
    public Button btnEditSong;
    @FXML
    private Button btnCancelSong;

    private Song songToBeEdited;


    private GenreModel genreModel;
    private SongModel songModel;
    private SimpleBooleanProperty dataChangedFlag;
    private SimpleBooleanProperty genreDataChanged;

    public NewSongController(){

        try {
            genreModel = new GenreModel();
            songModel = new SongModel();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void btnHandleMenuAddSong(ActionEvent actionEvent) throws SQLServerException, IOException {
        //TODO THIS BUTTON SHOULD ONLY SHOW IF YOU CLICKED NEW BUTTON AND NOT IF YOU CLICKED EDIT BUTTON.
            String title = txtTitle.getText();
            String artist = txtArtist.getText();
            Genre selectedGenre = (Genre) this.cmbGenre.getSelectionModel().getSelectedItem();
            int duration = durationParser();

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

            //use ternary operator to let me pass a null value to the createSong method. sindsygt cool if/else statement alternativ
            Integer genreId = (selectedGenre != null) ? selectedGenre.getId() : null;

            Song newSong = new Song(-1, title, artist, genreId, newFilePath, duration);
            songModel.createSong(newSong);
            //refreshes table in main view.
            dataChangedFlag.set(true);
    }

    public void btnHandleFileChooser(ActionEvent actionEvent) {
        //create file chooser object, with filters for relevant file types.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select audio file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
        //open file chooser dialog window to find file
        File file = fileChooser.showOpenDialog(null);
        //if file exists update text field
        if (file != null) {
            txtFilePath.setText(file.getAbsolutePath());

            //try catch in case someone does something illegal when selecting an audio file
            try {
                Media media = new Media(file.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                //after reading documentation it sounds like this might not be the smartest solution? but idk what else to do here
                //medialayer loads the song and gets duration from MediaPlayers getTotalDuration method and then sets it to seconds.
                mediaPlayer.setOnReady(() -> {
                    double duration = mediaPlayer.getTotalDuration().toSeconds();
                    //use format duration method to make it mm:ss  format
                    txtDuration.setText(formatDuration((int) duration));
                    //dispose of the loaded song, we only needed it for duration.
                    mediaPlayer.dispose();
                });
                //Just in case MediaPlayer acts up
                mediaPlayer.setOnError(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("This should never happen");
                });

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to load audio file");
            }
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
        //removes decimals, so it only gets whole minutes
        int minutes = durationSeconds / 60;
        //gets whats left after converting to minutes.
        int seconds = durationSeconds % 60;
        //makes it user-friendly by formatting it to MM:SS
        return String.format("%02d:%02d", minutes, seconds);
    }
    //Doing both formatDuration and then parsing it through this seems inefficient :)
    //having songs go over one hour is unrealistic, so 92830 minutes and 10 seconds will do fine, too bad we only show 2 digits.
    //should fix at some point maybe
    private int durationParser () {
        try {
            String[] parts = txtDuration.getText().split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return (minutes * 60) + seconds;
        } catch (Exception e) {
           //not sure if I should do an alert here
            throw new IllegalArgumentException("Failed to parse duration");
        }
    }

    public void btnHandleEditSong(ActionEvent actionEvent) throws SQLException {
        //TODO CHECK IF BELOW WORKS, i pass on the genreName but not a genreId, why cant I make it work with just the name.
        songToBeEdited.setTitle(txtTitle.getText());
        songToBeEdited.setArtistName(txtArtist.getText());
        songToBeEdited.setGenreName(cmbGenre.getSelectionModel().getSelectedItem().toString());

        songModel.updateSong(songToBeEdited);
        dataChangedFlag.set(true);
    }

    public void btnHandleCancelSong(ActionEvent actionEvent) {
        //close the stage that the button is located in.
        Stage stage = (Stage) btnCancelSong.getScene().getWindow();
        stage.close();

    }
    public void songToBeEditedPasser(Song song) {
        songToBeEdited = song;

        if (song != null) {
            txtTitle.setText(song.getTitle());
            txtArtist.setText(song.getArtistName());
            cmbGenre.getSelectionModel().select(song.getGenreName());
            txtDuration.setText(formatDuration(song.getDuration()));
            txtFilePath.setText(song.getSongFilePath());
        }
    }
}
