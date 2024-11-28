package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

import dk.easv.Jonas_MyTunesSolo.BE.Song;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class NewSongController {
    @FXML
    public TextField txtFilePath;
    public TextField txtArtist;
    public TextField txtTitle;


    public void btnHandleMenuAddSong(ActionEvent actionEvent) {
        String title = txtTitle.getText();
        String artist = txtArtist.getText();
        String filePath = this.txtFilePath.getText();
        //String genre //FIGURE OUT HOW TO MAKE COMBOBOX WORK
        //FIGURE OUT HOW TO GET DURATION IN A GOOD WAY
        //Song newSong = new Song(-1, title, artist, genre, filePath, duration);
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

            // Create a new stage
            Stage newSongStage = new Stage();
            newSongStage.setTitle("New genre");
            newSongStage.setResizable(false);

            // Set the scene with the loaded FXML file
            newSongStage.setScene(new Scene(root, 500, 400));

            // Show the stage
            newSongStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
