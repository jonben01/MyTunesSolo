package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.GUI.PlaylistModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NewPlaylistController implements Initializable {

    @FXML Button btnCancelPlaylist;
    @FXML Button btnMenuEditPlaylist;
    @FXML TextField txtPlaylistName;
    @FXML Button btnMenuAddPlaylist;
    @FXML PlaylistModel playlistModel;
    @FXML SimpleBooleanProperty dataChangedFlag;

    private Playlist playlistToBeEdited;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public NewPlaylistController(){

        try {
            playlistModel = new PlaylistModel();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setDataChangedFlag(SimpleBooleanProperty dataChangedFlag) {
        this.dataChangedFlag = dataChangedFlag;
    }

    public void btnMenuHandleAddPlaylist(ActionEvent actionEvent) throws SQLException {
        String name = txtPlaylistName.getText();
        Playlist newPlaylist = new Playlist(-1, name);

        if (name != null && !name.isEmpty()) {
            playlistModel.createNewPlaylist(newPlaylist);
            txtPlaylistName.clear();
            dataChangedFlag.set(true);
        }
    }

    public void btnHandleMenuEditPlaylist(ActionEvent actionEvent) throws SQLServerException {
        playlistToBeEdited.setName(txtPlaylistName.getText());
        playlistModel.updatePlaylist(playlistToBeEdited);
        dataChangedFlag.set(true);
    }

    public void btnHandleCancelPlaylist(ActionEvent actionEvent) {

    }

    public void playlistToBeEditedPasser(Playlist playlist) {
    playlistToBeEdited = playlist;

        if (playlist!= null) {
            txtPlaylistName.setText(playlist.getName());
        }
    }
}
