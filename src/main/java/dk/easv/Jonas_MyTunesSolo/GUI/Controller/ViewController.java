package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.PlaylistSong;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.GUI.PlaylistModel;
import dk.easv.Jonas_MyTunesSolo.GUI.PlaylistSongsModel;
import dk.easv.Jonas_MyTunesSolo.GUI.SongModel;
//LIBRARY IMPORTS
import com.microsoft.sqlserver.jdbc.SQLServerException;
//JAVA IMPORTS
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewController implements Initializable {
    @FXML public TableView<PlaylistSong> tblPlaylistSongs;
    @FXML public TableColumn<Song, String> colPlaylistSongTitle;
    @FXML public TableColumn<Playlist, Integer> colPlaylistSongs;
    @FXML public Label lblCurrentArtist;
    @FXML public TableColumn<PlaylistSong, Integer> colOrderIndex;
    @FXML public Button btnCloseApplication;
    @FXML public Button btnMinimizeApplication;
    @FXML public AnchorPane titlePane;
    @FXML private Slider volumeSlider;
    @FXML private Button btnPlay;public TextField txtSearcher;
    @FXML private Slider durationSlider;
    @FXML private Label lblCurrentTime;
    @FXML private Label lblSongDuration;
    @FXML private Button btnMute;
    @FXML private Label lblCurrentlyPlaying;
    @FXML TableColumn<Playlist, String> colPlaylistName;
    @FXML TableColumn<Song, String> colDuration;
    @FXML TableColumn<Song, Integer> colGenre;
    @FXML TableColumn<Song, Integer> colArtist;
    @FXML TableColumn<Song, String> colTitle;
    @FXML TableView<Song> tblSong;
    @FXML TableView<Playlist> tblPlaylist;

    private double savedVolume;
    private boolean muted = false;
    private SongModel songModel;
    private SimpleBooleanProperty dataChanged;
    private Media media;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private PlaylistModel playlistModel;
    private PlaylistSongsModel playlistSongsModel;
    private boolean isSliderChanging = false;
    private PlaylistSong currentPlaylistSong = null;
    private boolean isPlayingFromPlaylist = false;
    private double xOffset = 0;
    private double yOffset = 0;

    public ViewController()  {

        try {
            songModel = new SongModel();
            playlistModel = new PlaylistModel();
            playlistSongsModel = new PlaylistSongsModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO INITIALIZE PLAYLIST TABLE add total duration and number of songs
        //sets the playlist table columns and items
        colPlaylistName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPlaylistSongs.setCellValueFactory(new PropertyValueFactory<>("songCount"));
        tblPlaylist.setItems(playlistModel.getPlaylistsToBeViewed());

        //sets the playlistSongs table columns and items
        tblPlaylistSongs.setItems(playlistSongsModel.getPlaylistSongsToBeViewed());
        //changing the placeholder, when the table is empty
        tblPlaylistSongs.setPlaceholder(new Label("Drag songs here!"));

        tblPlaylistSongs.setId("Playlist-Songs");
        colOrderIndex.setCellValueFactory(new PropertyValueFactory<>("formattedOrderIndex"));

        tblSong.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue != null) {
               tblPlaylistSongs.getSelectionModel().clearSelection();
           }
        });
        tblPlaylistSongs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tblSong.getSelectionModel().clearSelection();
            }
        });

        colPlaylistSongTitle.setCellValueFactory(new PropertyValueFactory<>("SongTitle"));
        //TODO fix this, so that when I delete/edit a song on the selected playlist, it doesnt remove my selection and just updates the list.
        // if thats possible, might just live without it
        tblPlaylist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                    playlistSongsModel.refreshPlaylistSongs((Playlist) newValue);
            }
            else {
                tblPlaylistSongs.setItems(playlistSongsModel.getPlaylistSongsToBeViewed());
                playlistSongsModel.refreshPlaylistSongs(null);
            }
        });

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genreName"));
        //cellData.getValue gets a song. formats duration to MM:SS, uses javafx property to auto update.
        colDuration.setCellValueFactory(cellData -> {
            Song song = cellData.getValue();
            return new SimpleStringProperty(song.getFormattedDuration());
        });
        tblSong.setItems(songModel.getSongsToBeViewed());

        //TODO add comments to this, and in the other controllers
        //TODO fix selections dropping after changing anything.
        //refreshes gui when data has changed, I regret making it this way now, its taking a big toll on performance
        // make it only run for the areas affect, edited a song? only refresh the two tables containing songs
        // moving a song on a playlist, only update song on playlist
        dataChanged = new SimpleBooleanProperty(false);
        dataChanged.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
                songModel.refreshSong();
                playlistModel.refreshPlaylist();

                //TODO if i delete a song it should refresh the selected playlists songs, currently it doesnt update when
                // i delete a song.
                if (selectedPlaylist != null) {
                    playlistSongsModel.refreshPlaylistSongs(selectedPlaylist);
                    tblPlaylist.getSelectionModel().select(selectedPlaylist);
                }
                //reset flag, so code is reusable
                dataChanged.set(false);
            }
        });

        //listener passes "newValue" as a query to the searchSong method.
        //I liked this till i realized how laggy it is :)
        txtSearcher.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                songModel.searchSongs(newValue);

            } catch (SQLServerException e) {
                throw new RuntimeException(e);
            }
        });

        volumeSlider.setMin(5);
        volumeSlider.setValue(25);
        savedVolume = volumeSlider.getValue()/100 *0.2;


        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                //When I scale my double value (volume), the application refuses to play audible volume on 5% (depends on multiplier)
                //I assume this is due to the value being <1, therefore I am limiting how quiet the volume can be, before its actually 0.
                if(volumeSlider.getValue() < 5 && volumeSlider.getValue() > 0) {
                    mediaPlayer.setVolume(0);
                    muted = true;
                } else {
                    mediaPlayer.setVolume(volumeSlider.getValue()/100 * 0.2);
                }
            }
            if (newValue != null) {
                savedVolume = volumeSlider.getValue()/100 * 0.2;
                muted = false;
                updateMuteButton();
            }
        });


        //TODO test if this could brick something somewhere
        //Drag and drop song -> playlist start
        tblSong.setOnDragDetected(event -> {
            Song selectedSong = tblSong.getSelectionModel().getSelectedItem();
            //making sure you're actually dragging something.
            if (selectedSong != null) {
                //using javafx dragboard to handle the dragging actions, using copy as we want to keep the song on both tables
                Dragboard dragboard = tblSong.startDragAndDrop(TransferMode.COPY);
                //passing song by Id(String.valueOf) as content to the dragboard.
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(selectedSong.getSongID()));
                dragboard.setContent(content);
                //stops the event from getting picked up by other listeners
                event.consume();
            }
        });
        //making the table accept the drag and drop
        tblPlaylistSongs.setOnDragOver(event -> {

            //checks if the dragboard has a string.
            if(event.getDragboard().hasString()) {
                //makes tblPlaylistSongs accept the data transfer
                event.acceptTransferModes(TransferMode.COPY);
            }tblPlaylistSongs.setCursor(Cursor.CLOSED_HAND);
            //stops the event from getting picked up by other listeners
            event.consume();
        });

        tblPlaylistSongs.setOnDragDropped(event -> {
            tblPlaylistSongs.setCursor(Cursor.DEFAULT);
           Dragboard dragboard = event.getDragboard();
            //check if dragboard has a string.
           if (dragboard.hasString()) {
               //uses getSongById to get a song to move.
               String songId = dragboard.getString();
               Song song = songModel.getSongByID(Integer.parseInt(songId));
               Playlist playlist = tblPlaylist.getSelectionModel().getSelectedItem();
               if (playlist != null && song != null) {
                   try {
                       //runs moveSongToPlayList as long as conditions of if statement are met.
                       playlistSongsModel.moveSongToPlaylist(song, playlist);
                       //refreshes gui after drag and drop success.
                       dataChanged.set(true);
                   } catch (SQLServerException e) {
                       throw new RuntimeException(e);
                   }
               }
           }
            //stops the event from getting picked up by other listeners
           event.consume();
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
                newSongStage.setTitle("Edit a song");
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
        try {
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/playlist-view.fxml"));
            Parent root = fxmlLoader.load();

            NewPlaylistController NPController = fxmlLoader.getController();
            NPController.setDataChangedFlag(dataChanged);
            NPController.btnMenuEditPlaylist.setVisible(false);

            // Create a new stage
            Stage newSongStage = new Stage();
            newSongStage.setTitle("Add a new playlist");
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

    public void btnHandleEditPlaylist(ActionEvent actionEvent) {
        Playlist playlistToBeEdited = (Playlist) tblPlaylist.getSelectionModel().getSelectedItem();

        if (playlistToBeEdited == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a playlist to edit");
            alert.showAndWait();
        } else {
                try {
                    // Load the FXML file
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/playlist-view.fxml"));
                    Parent root = fxmlLoader.load();

                    NewPlaylistController NPController = fxmlLoader.getController();
                    NPController.setDataChangedFlag(dataChanged);
                    NPController.btnMenuAddPlaylist.setVisible(false);
                    NPController.playlistToBeEditedPasser(playlistToBeEdited);

                    // Create a new stage
                    Stage newSongStage = new Stage();
                    newSongStage.setTitle("Edit a playlist");
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
    }

    public void btnHandleDeletePlaylist(ActionEvent actionEvent) throws SQLServerException {
        Playlist playlistToBeDeleted = tblPlaylist.getSelectionModel().getSelectedItem();

        if (playlistToBeDeleted != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Playlist deletion confirmation");
            alert.setHeaderText("");
            alert.setContentText("Are you sure you want to delete playlist: " + playlistToBeDeleted.getName());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                playlistModel.deletePlaylist(playlistToBeDeleted);
            }
            tblPlaylist.setItems(playlistModel.getPlaylistsToBeViewed());
        }
    }

    public void btnHandleMoveSongUp(ActionEvent actionEvent) throws SQLServerException {
        PlaylistSong playlistSong = tblPlaylistSongs.getSelectionModel().getSelectedItem();
        List<PlaylistSong> playlistSongList = new ArrayList<PlaylistSong>(tblPlaylistSongs.getSelectionModel().getSelectedItems());
        playlistSongsModel.moveSongOnPlaylistUp(playlistSong, playlistSongList);
        dataChanged.set(true);
    }

    public void btnHandleMoveSongDown(ActionEvent actionEvent) throws SQLException {
        PlaylistSong playlistSong = tblPlaylistSongs.getSelectionModel().getSelectedItem();
        List<PlaylistSong> playlistSongList = new ArrayList<PlaylistSong>(tblPlaylistSongs.getSelectionModel().getSelectedItems());
        playlistSongsModel.moveSongOnPlaylistDown(playlistSong, playlistSongList);
        dataChanged.set(true);
    }

    public void btnHandleDeleteSongOnPlaylist(ActionEvent actionEvent) throws SQLServerException {
        PlaylistSong playlistSongToBeDeleted = tblPlaylistSongs.getSelectionModel().getSelectedItem();

        if (playlistSongToBeDeleted != null) {
            playlistSongsModel.deleteSongOnPlaylist(playlistSongToBeDeleted);
            dataChanged.set(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a song to delete from your playlist");
        }
    }

    //Legacy code lmao. Added drag and drop to handle this instead, but leaving it here anyway. button is invisible in the fxml
    public void btnHandleMoveSongToPlaylist(ActionEvent actionEvent) throws SQLServerException {
        Song songToMove = tblSong.getSelectionModel().getSelectedItem();

            Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
            if (songToMove != null && selectedPlaylist != null) {
                playlistSongsModel.moveSongToPlaylist(songToMove, selectedPlaylist);
                dataChanged.set(true);
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please a song and a playlist");
            }
    }

    public void updateMuteButton() {
        if(muted) {
            btnMute.setText("\uD83D\uDD07");
            return;
        }
        //small issue here, you can get the mute icon to show while the song still has audio, but it doesnt really matter
        //I think.
        if (volumeSlider.getValue() >=0 && volumeSlider.getValue() <= 5) {
                    btnMute.setText("\uD83D\uDD07");

        }
        if (volumeSlider.getValue() >=6 && volumeSlider.getValue() < 35) {
                    btnMute.setText("\uD83D\uDD08");

        }
        if (volumeSlider.getValue() >=35 && volumeSlider.getValue() < 70) {
                    btnMute.setText("🔉");

        }
        if(volumeSlider.getValue() >= 70 && volumeSlider.getValue() < 101) {
                    btnMute.setText("🔊");
        }

    }

    public void btnHandlePlay(ActionEvent e) {

        if (mediaPlayer != null) {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            }
            if (status == MediaPlayer.Status.PAUSED) {
                mediaPlayer.seek(mediaPlayer.getCurrentTime());
                mediaPlayer.play();
            }
        }
    }
    public void doubleClickedSong(MouseEvent mouseEvent) {
        Song selectedSong = null;
        //stupid workaround to avoid the issue of not being able to manually select a song and then press play on it
        //which you could before implementing double click function
        if (mediaPlayer == null) {
            selectedSong = tblSong.getSelectionModel().getSelectedItem();
        }
        //if mouse has been pressed twice on a tblSong item set selected song to that item.
        if (mouseEvent.getClickCount() == 2 && tblSong.getSelectionModel().getSelectedItem() != null) {
            selectedSong = tblSong.getSelectionModel().getSelectedItem();
            isPlayingFromPlaylist = false;
        }
        //dont do anything if somehow you got here without selecting a song or having one loaded?
        if (selectedSong == null && currentSong == null) {
            return;
        }
        //play selected song, and it is not from a playlist
        if(selectedSong != null) {
            play(selectedSong, false);
        }
    }

    public void doubleClickedPlaylistSong(MouseEvent mouseEvent) {
        PlaylistSong selectedPlaylistSong = null;
        Song selectedSong = null;
        //stupid workaround to avoid the issue of not being able to manually select a song and then press play on it
        //which you could before implementing double click function
        if (mediaPlayer == null) {
            selectedPlaylistSong = tblPlaylistSongs.getSelectionModel().getSelectedItem();
            selectedSong = selectedPlaylistSong.getSong();
        }
        //if mouse has been pressed twice on a tblPlaylistSong item set selected song to that item.
        if (mouseEvent.getClickCount() == 2 && tblPlaylistSongs.getSelectionModel().getSelectedItem() != null) {
            selectedPlaylistSong = tblPlaylistSongs.getSelectionModel().getSelectedItem();
            selectedSong = selectedPlaylistSong.getSong();
            isPlayingFromPlaylist = true;
            currentPlaylistSong = selectedPlaylistSong;
        }
        //dont do anything if somehow you got here without selecting a song or having one loaded?
        if (selectedSong == null && currentSong == null) {
            return;
        }
        //play selected song, and it is from a playlist
        if(selectedSong != null) {
            play(selectedSong, true);
        }
    }
    public void play(Song selectedSong, boolean isPlayingFromPlaylist) {
        if (selectedSong == null && currentSong == null) {
            return;
        }
        try {
            //if theres a song selected but it isnt equal to the currentSong run this
            //if there is no song selected and it isnt equal to currentSong, do not run this
            if (selectedSong != null && !selectedSong.equals(currentSong)) {
                String mediaURL = selectedSong.getSongFilePath();
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
                    dataChanged.set(true);
                    return;
                }
                //if a mediaplayer already exists, stop and dispose
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                }
                playSelectedSong(selectedSong, isPlayingFromPlaylist);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //encapsulating the initial play part of previous method, it became way way way too long - still is.
    //this could be made a lot better still, but imagine this being inside the btnHandlePlay() - should've done it more tbh.
    //Tried making an overloaded method, but adding a boolean parameter made it easier to work with
    public void playSelectedSong(Song selectedSong, boolean playingFromPlaylist) {
        //boolean check for whether we are playing from a playlist or not, single-handedly controls autoplay function
        //from playlist
        isPlayingFromPlaylist = playingFromPlaylist;
        //create everything relevant for the mediaPlayer - including itself.
        String mediaURL = selectedSong.getSongFilePath();
        File file = new File(mediaURL);
        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        //when the mediaPlayer is ready to play, set durations sliders "length", and handle mouse events.
        mediaPlayer.setOnReady(() -> {
            durationSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            durationSlider.setOnMousePressed(event -> isSliderChanging = true);
            durationSlider.setOnMouseReleased(event -> {
                isSliderChanging = false;
                //when mouse is released makes mediaPlayer seek where you dropped it
                mediaPlayer.seek(Duration.seconds(durationSlider.getValue()));
            });
            //This listener makes it so, if you drag the slider, it will update the label to show where in the song
            //the slider thumb is at - when thumb is dropped the listener a few lines up will make the media player seek.
            durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (isSliderChanging) {
                    lblCurrentTime.setText(formatDuration(newValue.intValue()));
                }
            });
            //this listener makes the slider thumb follow the songs' playtime.
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!isSliderChanging) {
                    durationSlider.setValue(newValue.toSeconds());
                    lblCurrentTime.setText(formatDuration((int) newValue.toSeconds()));
                }
            });
            //maybe having 1 billion listeners in this method is a bad idea, idk
            //sets play button depending on mediaPlayer status with switch statement
            mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
                switch (newValue) {
                    case PLAYING:
                        btnPlay.setText("⏸");
                        break;
                    case PAUSED:
                        btnPlay.setText("⏵");
                        break;
                    default:
                        break;
                }
            });
            //muted check, will keep the auto play from suddenly turning back on sound.
            if (muted) {
                mediaPlayer.setVolume(0);
            } else {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100 * 0.2);
            }

            mediaPlayer.play();
            lblSongDuration.setText(formatDuration((int) mediaPlayer.getTotalDuration().toSeconds()));
            //save the selected song as the currentSong
            currentSong = selectedSong;
            //displays title and artist(s)
            lblCurrentlyPlaying.setText(currentSong.getTitle());
            lblCurrentArtist.setText(currentSong.getArtistName());

            if (isPlayingFromPlaylist) {
                //when set on end of media is triggered, mediaPlayer calls playNextPlaylistSong
                mediaPlayer.setOnEndOfMedia(this::playNextPlaylistSong);
                //select the currently playing song
                tblPlaylistSongs.getSelectionModel().select(currentPlaylistSong);
            } else{
                //when set on end of media is triggered, mediaPlayer calls playNextSong
                mediaPlayer.setOnEndOfMedia(this::playNextSong);
                //select the currently playing song
                tblSong.getSelectionModel().select(currentSong);
            }
        });
    }

    private void playNextSong() {
        List<Song> songOrder = songModel.getSongsToBeViewed();
        if (songOrder.isEmpty()) {
            return;
        }
        int currentIndex = songOrder.indexOf(currentSong);
        //first part ensures we get the next index. Modulo only has an effect when we're at the songOrder.size
        //on a six song long list, the last index is 5, and thus it will loop back to 0: (5 + 1) % 6 = 0
        int nextIndex = (currentIndex + 1) % songOrder.size();
        //creates the nextSong, based on the nextIndex and then plays it
        Song nextSong = songOrder.get(nextIndex);
        playSelectedSong(nextSong, false);
    }
    private void playNextPlaylistSong() {
        List<PlaylistSong> playlistSongOrder = playlistSongsModel.getPlaylistSongsToBeViewed();
        if (playlistSongOrder.isEmpty()) {
            return;
        }
        int currentIndex = playlistSongOrder.indexOf(currentPlaylistSong);
        //first part ensures we get the next index. Modulo only has an effect when we're at the songOrder.size
        //on a six song long list, the last index is 5, and thus it will loop back to 0: (5 + 1) % 6 = 0
        int nextIndex = (currentIndex + 1) % playlistSongOrder.size();
        //creates the nextPlaylistSong, based on the nextIndex and then plays it
        PlaylistSong nextPlaylistSong = playlistSongOrder.get(nextIndex);
        currentPlaylistSong = nextPlaylistSong;
        playSelectedSong(nextPlaylistSong.getSong(), true);
    }

    //TODO figure out if I should use OrderIndex of the playlistSongs instead, might help with handling deletion of a playlistSong.
    private void playPreviousPlaylistSong() {
        List<PlaylistSong> playlistSongOrder = playlistSongsModel.getPlaylistSongsToBeViewed();
        if (playlistSongOrder.isEmpty()) {
            return;
        }

        int currentIndex = playlistSongOrder.indexOf(currentPlaylistSong);
        //works same as in the other methods like this one just works the other way around
        //on a 6 song playlist, we're playing song index 4 so we get (4 - 1 + 6) % 6 = 3
        // 0 - 1 + 6 = 5 and 5%6 is 5, so it will loop back to the bottom.
        int nextIndex = (currentIndex - 1 + playlistSongOrder.size()) % playlistSongOrder.size();
        //creates the nextPlaylistSong, based on the nextIndex and then plays it. should've maybe made the name a bit better
        //but technically if  you call this method, the next song is the previous song lol
        PlaylistSong nextPlaylistSong = playlistSongOrder.get(nextIndex);
        currentPlaylistSong = nextPlaylistSong;
        playSelectedSong(nextPlaylistSong.getSong(), true);
    }

    private void playPreviousSong() {
        List<Song> songOrder = songModel.getSongsToBeViewed();
        if (songOrder.isEmpty()) {
            return;
        }
        int currentIndex = songOrder.indexOf(currentSong);
        //works same as in the other methods like this one just works the other way around
        //on a 6 song playlist, we're playing song index 4 so we get (4 - 1 + 6) % 6 = 3
        // 0 - 1 + 6 = 5 and 5%6 is 5, so it will loop back to the bottom.
        int nextIndex = (currentIndex - 1 + songOrder.size()) % songOrder.size();
        //creates the nextSong, based on the nextIndex and then plays it. should've maybe made the name a bit better
        //but technically if  you call this method, the next song is the previous song lol
        Song nextSong = songOrder.get(nextIndex);
        playSelectedSong(nextSong, false);
    }
    //just plays the next song, as long as a mediaPlayer exists.
    public void btnHandleSkip(ActionEvent actionEvent) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            if (isPlayingFromPlaylist) {
                playNextPlaylistSong();
            } else {
                playNextSong();
            }
        }
    }
    //shouldve given it a better name
    public void btnHandleGoBack(ActionEvent actionEvent) {
        //Resets song back to start if you pressed it while further than 5 seconds into the song
        //this is the reset button too
        if (mediaPlayer != null && mediaPlayer.getCurrentTime().toSeconds() >= 5) {
            mediaPlayer.seek(Duration.seconds(0));
            //select the playing song.
            if (isPlayingFromPlaylist) {
                tblPlaylistSongs.getSelectionModel().select(currentPlaylistSong);
            } else {
                tblSong.getSelectionModel().select(currentSong);
            }
            return;
        }
        //get rid of an existing mediaPlayer before creating a new one
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            //play previous song depending on where you are playing from
            if (isPlayingFromPlaylist) {
                playPreviousPlaylistSong();
            } else {
                playPreviousSong();
            }
        }
    }
    //mutes the media.
    public void btnHandleMute(ActionEvent actionEvent) {
        if (mediaPlayer != null) {
            if (!muted) {
                muted = true;
                mediaPlayer.setVolume(0);
                updateMuteButton();
            } else {
                mediaPlayer.setVolume(savedVolume);
                muted = false;
                updateMuteButton();
            }
        }
    }

    private String formatDuration(int durationSeconds) {
        //removes decimals, so it only gets whole minutes
        int minutes = durationSeconds / 60;
        //gets whats left after converting to minutes.
        int seconds = durationSeconds % 60;
        //makes it user-friendly by formatting it to MM:SS
        return String.format("%02d:%02d", minutes, seconds);
    }
    //closes the program
    public void btnHandleCloseApplication(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCloseApplication.getScene().getWindow();
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
        stage.close();
    }
    //"Iconifies" the main window
    public void btnHandleMinimizeApplication(ActionEvent actionEvent) {
        Stage stage = (Stage) btnMinimizeApplication.getScene().getWindow();
        stage.setIconified(true);
    }
    //These methods let you drag the window by mousePressed and mouseDragged actions.
    public void handleClicked(MouseEvent mouseEvent) {
        Stage stage = (Stage) titlePane.getScene().getWindow();
        //stores the difference between mouse and window positions
        xOffset = stage.getX() - mouseEvent.getScreenX();
        yOffset = stage.getY() - mouseEvent.getScreenY();

    }
    public void handleMoved(MouseEvent mouseEvent) {
        Stage stage = (Stage) titlePane.getScene().getWindow();
        //sets the x and y value of the window as you drag it.
        stage.setX(mouseEvent.getScreenX() + xOffset);
        stage.setY(mouseEvent.getScreenY() + yOffset);
    }
}

