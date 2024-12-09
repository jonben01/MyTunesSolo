package dk.easv.Jonas_MyTunesSolo.GUI.Controller;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.GUI.PlaylistModel;
//LIBRARY IMPORTS
import com.microsoft.sqlserver.jdbc.SQLServerException;
//JAVA IMPORTS
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
        Stage stage = (Stage) btnMenuAddPlaylist.getScene().getWindow();
        stage.close();
    }

    public void btnHandleMenuEditPlaylist(ActionEvent actionEvent) throws SQLServerException {
        playlistToBeEdited.setName(txtPlaylistName.getText());
        playlistModel.updatePlaylist(playlistToBeEdited);
        dataChangedFlag.set(true);
        Stage stage = (Stage) btnMenuEditPlaylist.getScene().getWindow();
        stage.close();
    }

    public void btnHandleCancelPlaylist(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancelPlaylist.getScene().getWindow();
        stage.close();
    }

    public void playlistToBeEditedPasser(Playlist playlist) {
    playlistToBeEdited = playlist;

        if (playlist!= null) {
            txtPlaylistName.setText(playlist.getName());
        }
    }
}
