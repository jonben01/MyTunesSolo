package dk.easv.Jonas_MyTunesSolo.GUI.Controller;


//TODO separate project imports and java imports
import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.GUI.SongModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewController implements Initializable {
    @FXML
    public Slider volumeSlider;
    @FXML
    TableColumn<Song, Integer> colDuration;
    @FXML
    TableColumn<Song, Integer> colGenre;
    @FXML
    TableColumn<Song, Integer> colArtist;
    @FXML
    TableColumn<Song, String> colTitle;
    @FXML
    TableView<Song> tblSong;

    private SongModel songModel;
    private SimpleBooleanProperty dataChanged;

    private Media media;
    private MediaPlayer mediaPlayer;
    private String mediaURL = "";
    private boolean isPlaying = false;


    public ViewController()  {

        try {
            songModel = new SongModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genreName"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        tblSong.setItems(songModel.getSongsToBeViewed());

        //TODO add comments to this, and in the other controllers
        dataChanged = new SimpleBooleanProperty(false);
        dataChanged.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                songModel.refreshSong();
                //reset flag, so code is reusable
                dataChanged.set(false);
            }
        });
        /*
        media = new Media(mediaURL);
        mediaPlayer = new MediaPlayer(media);

         */

    }

    @FXML
    public void btnHandleNewSong(ActionEvent actionEvent) {
        try {
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/song-view.fxml"));
            Parent root = fxmlLoader.load();

            NewSongController NSController = fxmlLoader.getController();
            NSController.setDataChangedFlag(dataChanged);
            NSController.btnEditSong.setVisible(false);

            // Create a new stage
            Stage newSongStage = new Stage();
            newSongStage.setTitle("Add a new audio file");
            newSongStage.setResizable(false);
            //Sets modality, cant use previous window till this one is closed
            newSongStage.initModality(Modality.APPLICATION_MODAL);

            // Set the scene with the loaded FXML file
            newSongStage.setScene(new Scene(root));

            // Show the stage
            newSongStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void btnHandleDeleteSong(ActionEvent actionEvent) throws SQLServerException {
        Song songToBeDeleted = tblSong.getSelectionModel().getSelectedItem();

        if (songToBeDeleted != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Song deletion confirmation");
            alert.setHeaderText("");
            alert.setContentText("Are you sure you want to delete: " + songToBeDeleted.getTitle() + " by " + songToBeDeleted.getArtistName());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                songModel.deleteSong(songToBeDeleted);

                File fileToDelete = new File(songToBeDeleted.getSongFilePath());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }

                tblSong.setItems(songModel.getSongsToBeViewed());
            }
        }

    }


    //TODO FINISH THIS AFTER DURATION HAS BEEN SORTED
    public void btnHandleEditSong(ActionEvent actionEvent) {


        Song songToBeEdited = tblSong.getSelectionModel().getSelectedItem();

        if (songToBeEdited == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a song to edit");
            alert.showAndWait();
        } else {

            try {
                // Load the FXML file
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/song-view.fxml"));
                Parent root = fxmlLoader.load();

                NewSongController NSController = fxmlLoader.getController();
                NSController.setDataChangedFlag(dataChanged);
                NSController.btnMenuAddSong.setVisible(false);
                //pass the song with the song passer method
                NSController.songToBeEditedPasser(songToBeEdited);

                // Create a new stage
                Stage newSongStage = new Stage();
                newSongStage.setTitle("Add a new audio file");
                newSongStage.setResizable(false);
                //Sets modality, cant use previous window till this one is closed
                newSongStage.initModality(Modality.APPLICATION_MODAL);

                newSongStage.setScene(new Scene(root));
                newSongStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnHandleNewPlaylist(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

    public void btnHandleEditPlaylist(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

    public void btnHandleDeletePlaylist(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

    public void btnHandleMoveSongUp(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

    public void btnHandleMoveSongDown(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

    public void btnHandleDeleteSongOnPlaylist(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

    public void btnHandleMoveSongToPlaylist(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

    public void btnHandleStop(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
        mediaPlayer.stop();

    }

    public void btnHandlePlay(ActionEvent actionEvent) {
        Song selectedSong = tblSong.getSelectionModel().getSelectedItem();
        //TODO make sure this shit doesnt suck ass
        //could do an alert, but it I think its better to just not do anything.
        if (selectedSong == null) {
            return;
        }
        try {
            mediaURL = selectedSong.getSongFilePath();
            File file = new File(mediaURL);

            //in case you delete a song in your song folder, while the program is running.
            //checks if, in this case, the file path exists.
            if (!file.exists()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Song does not exist, deleting from library");
                alert.showAndWait();
                //makes sure to delete the deleted song.
                songModel.deleteSong(selectedSong);
                return;
            }
            if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
            }
            //if a mediaplayer already exists, stop and dispose of the loaded media
            if (mediaPlayer != null) {
                MediaPlayer.Status status = mediaPlayer.getStatus();
                if (status == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    isPlaying = false;
                }
                if (status == MediaPlayer.Status.PAUSED) {
                    mediaPlayer.seek(mediaPlayer.getCurrentTime());
                    mediaPlayer.play();
                    isPlaying = true;
                }
            } else {

                media = new Media(file.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
                isPlaying = true;
            }

            //TODO make a better catch clause
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button btnHandlePlay = (Button) actionEvent.getSource();
        if (isPlaying) {
            btnHandlePlay.setText("Pause");
        } else {
            btnHandlePlay.setText("Play");
        }

    }

    public void btnHandlePause(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
        if (isPlaying) {
            mediaPlayer.pause();
        }
    }

    public void btnHandleReset(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
        mediaPlayer.seek(Duration.seconds(0));
    }

    public void btnHandleSkip(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

    public void btnHandleGoBack(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

    public void btnHandleMute(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

}