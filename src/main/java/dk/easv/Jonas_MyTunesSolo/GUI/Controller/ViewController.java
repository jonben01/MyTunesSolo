package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.GUI.SongModel.SongModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewController implements Initializable {


    @FXML
    TableColumn<Song, Integer> colGenre;
    @FXML
    TableColumn<Song, Integer> colArtist;
    @FXML
    TableColumn<Song, String> colTitle;
    @FXML
    TableView<Song> tblSong;

    public ViewController() throws Exception {
        SongModel songModel = new dk.easv.Jonas_MyTunesSolo.GUI.SongModel.SongModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void btnHandleNewSong(ActionEvent actionEvent) {
        try {
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/song-view.fxml"));
            Parent root = fxmlLoader.load();

            // Create a new stage
            Stage newSongStage = new Stage();
            newSongStage.setTitle("New Song");
            newSongStage.setResizable(false);

            // Set the scene with the loaded FXML file
            newSongStage.setScene(new Scene(root, 300, 400));

            // Show the stage
            newSongStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void btnHandleNewPlaylist(ActionEvent actionEvent) {
    }

    public void btnHandleEditPlaylist(ActionEvent actionEvent) {
    }

    public void btnHandleDeletePlaylist(ActionEvent actionEvent) {
    }

    public void btnHandleMoveSongUp(ActionEvent actionEvent) {
    }

    public void btnHandleMoveSongDown(ActionEvent actionEvent) {
    }

    public void btnHandleDeleteSongOnPlaylist(ActionEvent actionEvent) {
    }

    public void btnHandleEditSong(ActionEvent actionEvent) {
    }

    public void btnHandleDeleteSong(ActionEvent actionEvent) {
    }

    public void btnHandleMoveSongToPlaylist(ActionEvent actionEvent) {
    }


}