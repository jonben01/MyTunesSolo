package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

public class NewSongController {
    @FXML
    public TextField txtFilePath;

    public void btnHandleMenuAddSong(ActionEvent actionEvent) {
        
    }

    public void btnHandleFileChooser(ActionEvent actionEvent) {
        //create file chooser object, with filters for relevant file types.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select audio file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
        //open file chooser dialog window
        File file = fileChooser.showOpenDialog(null);
        //if file exists update text field
        if (file != null) {
            txtFilePath.setText(file.getAbsolutePath());
        }
    }
}
