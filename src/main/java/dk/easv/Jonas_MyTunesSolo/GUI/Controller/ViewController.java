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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewController implements Initializable {

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

    private SimpleBooleanProperty dataChanged;

    private SongModel songModel;

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

    }

    @FXML
    public void btnHandleNewSong(ActionEvent actionEvent) {
        try {
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/song-view.fxml"));
            Parent root = fxmlLoader.load();

            NewSongController NSController = fxmlLoader.getController();
            NSController.setDataChangedFlag(dataChanged);

            // Create a new stage
            Stage newSongStage = new Stage();
            newSongStage.setTitle("New Song");
            newSongStage.setResizable(false);
            //Sets modality, cant use previous window till this one is closed
            newSongStage.initModality(Modality.APPLICATION_MODAL);

            // Set the scene with the loaded FXML file
            newSongStage.setScene(new Scene(root, 300, 400));

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

    public void btnHandleEditSong(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }

    public void btnHandleMoveSongToPlaylist(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
    }


}