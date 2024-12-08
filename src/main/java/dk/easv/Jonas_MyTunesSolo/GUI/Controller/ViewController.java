package dk.easv.Jonas_MyTunesSolo.GUI.Controller;

//TODO separate project imports and java imports
import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Playlist;
import dk.easv.Jonas_MyTunesSolo.BE.PlaylistSong;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.GUI.PlaylistModel;
import dk.easv.Jonas_MyTunesSolo.GUI.PlaylistSongsModel;
import dk.easv.Jonas_MyTunesSolo.GUI.SongModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    @FXML private Slider volumeSlider;
    @FXML private Button btnPlay;public TextField txtSearcher;
    @FXML private Label lblVolume;
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
    private boolean isPlaying = false;
    private Song currentSong;
    private PlaylistModel playlistModel;
    private PlaylistSongsModel playlistSongsModel;
    private boolean isSliderChanging = false;

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
        colPlaylistName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPlaylistSongs.setCellValueFactory(new PropertyValueFactory<>("songCount"));
        tblPlaylist.setItems(playlistModel.getPlaylistsToBeViewed());
        tblPlaylistSongs.setItems(playlistSongsModel.getPlaylistSongsToBeViewed());
        tblPlaylistSongs.setPlaceholder(new Label("Drag songs here!"));


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

        //TODO playlist songs get selected playlist and that too bruh moment - what the fuck did i write here???
        colPlaylistSongTitle.setCellValueFactory(new PropertyValueFactory<>("SongTitle"));
        //TODO fix this, so that when I delete/edit a song on the selected playlist, it doesnt remove my selection and just updates the list.
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
        dataChanged = new SimpleBooleanProperty(false);
        dataChanged.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Playlist selectedPlaylist = tblPlaylist.getSelectionModel().getSelectedItem();
                songModel.refreshSong();
                playlistModel.refreshPlaylist();

                //TODO make this work lol
                if (selectedPlaylist != null) {
                    playlistSongsModel.refreshPlaylistSongs(selectedPlaylist);
                    tblPlaylist.getSelectionModel().select(selectedPlaylist);
                }
                //reset flag, so code is reusable
                dataChanged.set(false);
            }
        });
        //listener passes "newValue" as a query to the searchSong method.
        txtSearcher.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                songModel.searchSongs(newValue);

            } catch (SQLServerException e) {
                throw new RuntimeException(e);
            }
        });
        //TODO make this work when you use search function, currently always sets text to "⏵"
        tblSong.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue != null && newValue != currentSong) {
               btnPlay.setText("⏵");
           }
           if (newValue == currentSong) {
               btnPlay.setText("⏸");
           }
        });
        //TODO REMEMBER TO DELETE THE LABEL IN FINISHED PRODUCT, DONT NEED THE % TO BE SEEN
        volumeSlider.setMin(5);
        volumeSlider.setValue(25);
        lblVolume.setText((int) volumeSlider.getValue() + "%");
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
                lblVolume.setText((int) volumeSlider.getValue()+"%");
            }
            if (newValue != null) {
                lblVolume.setText((int) volumeSlider.getValue()+"%");
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
                //using javafx dragboard to handle the dragging actions
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
            }
            event.consume();
        });

        tblPlaylistSongs.setOnDragDropped(event -> {
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
                       //refreshes gui.
                       dataChanged.set(true);
                   } catch (SQLServerException e) {
                       throw new RuntimeException(e);
                   }
               }
           }
           event.consume();
        });
        //TODO implement drag and drop on delete buttons for respective table views.

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
        //TODO not sure if i want to add a confirmation for deletion here
        // change this when the table correctly shows playlistSongs
        PlaylistSong playlistSongToBeDeleted = tblPlaylistSongs.getSelectionModel().getSelectedItem();

        if (playlistSongToBeDeleted != null) {
            playlistSongsModel.deleteSongOnPlaylist(playlistSongToBeDeleted);
            System.out.println(dataChanged + "");
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
        //TODO get icons instead maybe, unicode looks ass
        if(muted) {
            btnMute.setText("\uD83D\uDD07");
            return;
        }
        if (volumeSlider.getValue() >=0 && volumeSlider.getValue() < 6) {
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


    //TODO delete this method, make sure there arent any unforeseen consequences.
    public void btnHandleStop(ActionEvent actionEvent) {
        if (mediaPlayer != null) {
            tblSong.getSelectionModel().clearSelection();
            mediaPlayer.stop();
            isPlaying = false;
            btnPlay.setText("⏵");
            currentSong = null;
        }
    }

    public void btnHandlePlay(ActionEvent actionEvent) {
        Song selectedSong = null;
        
        if (tblSong.getSelectionModel().getSelectedItem() != null) {
            selectedSong = tblSong.getSelectionModel().getSelectedItem();
        }
        if (tblPlaylistSongs.getSelectionModel().getSelectedItem() != null) {
            PlaylistSong playlistSong = tblPlaylistSongs.getSelectionModel().getSelectedItem();
            selectedSong = playlistSong.getSong();
        }
        
        Button btnHandlePlay = (Button) actionEvent.getSource();
        //TODO make sure this shit doesnt suck ass
        //could do an alert, but I think its better to do nothing.
        if (selectedSong == null && currentSong == null) {
            return;
        }
        try {
            //if theres a song selected but it isnt equal to the currentSong run this
            //if there is no song selected and it isnt equal to currentSong, do not run this
            if (selectedSong != null && !selectedSong.equals(currentSong)) {
                String mediaURL = selectedSong.getSongFilePath();
                File file = new File(mediaURL);

                //TODO make this work, if you search and select again, it will treat the new song as != to current song.
                if(selectedSong == currentSong) {
                    btnHandlePlay.setText("⏸");
                }

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
                //if a mediaplayer already exists, stop and dispose of the loaded media
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    btnHandlePlay.setText("⏵");
                }
                    playSelectedSong(selectedSong);
                    btnHandlePlay.setText("⏸");
            } else {
                MediaPlayer.Status status = mediaPlayer.getStatus();
                if (mediaPlayer != null) {
                    if (status == MediaPlayer.Status.PLAYING) {
                        mediaPlayer.pause();
                        isPlaying = false;
                        btnHandlePlay.setText("⏵");
                    }
                    if (status == MediaPlayer.Status.PAUSED) {
                        mediaPlayer.seek(mediaPlayer.getCurrentTime());
                        mediaPlayer.play();
                        isPlaying = true;
                        btnHandlePlay.setText("⏸");
                    }
                }
            }
            //TODO make a better catch clause
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //encapsulating the initial play part of previous method, it became way way way too long.
    public void playSelectedSong(Song selectedSong) {
        String mediaURL = selectedSong.getSongFilePath();
        File file = new File(mediaURL);
        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnReady(() -> {
            durationSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            durationSlider.setOnMousePressed(event -> isSliderChanging = true);
            durationSlider.setOnMouseReleased(event -> {
                isSliderChanging = false;
                mediaPlayer.seek(Duration.seconds(durationSlider.getValue()));
            });

            durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (isSliderChanging) {
                    lblCurrentTime.setText(formatDuration(newValue.intValue()));
                }
            });

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!isSliderChanging) {
                    durationSlider.setValue(newValue.toSeconds());
                    lblCurrentTime.setText(formatDuration((int) newValue.toSeconds()));
                }
            });
            mediaPlayer.setVolume(volumeSlider.getValue() / 100 * 0.2);
            mediaPlayer.play();
            updateMuteButton();
            lblSongDuration.setText(formatDuration((int) mediaPlayer.getTotalDuration().toSeconds()));
            isPlaying = true;
            currentSong = selectedSong;
            lblCurrentlyPlaying.setText(currentSong.getTitle());
            lblCurrentArtist.setText(currentSong.getArtistName());
            //TODO explain double colon :: method reference operator.
            //TODO also fix this, right now it just plays next song no matter what.
            // right now it always thinks we're playing from the song table, so if we arent, it will go play the next song
            // on the song table anyway.
            mediaPlayer.setOnEndOfMedia(this::playNextSong);
            tblSong.getSelectionModel().select(currentSong);
        });
    }
    //TODO Fix this method (not here), it makes the pause button play
    private void playNextSong() {
        List<Song> songOrder = songModel.getSongsToBeViewed();
        //should probably dispose of the mediaplayers media, if nothing happens here.
        if (songOrder.isEmpty()) {
            return;
        }
        int currentIndex = songOrder.indexOf(currentSong);
        //first part ensures we get the next index. Modulo only has an effect when we're at the songOrder.size
        //on a six song long list, the last index is 5, and thus it will loop back to 0: (5 + 1) % 6 = 0
        int nextIndex = (currentIndex + 1) % songOrder.size();
        Song nextSong = songOrder.get(nextIndex);
        playSelectedSong(nextSong);
    }
    //TODO IMPLEMENT THIS METHOD
    private void playNextPlaylistSong() {
        List<PlaylistSong> playlistSongOrder = playlistSongsModel.getPlaylistSongsToBeViewed();
    }

    public void btnHandleReset(ActionEvent actionEvent) {
        //TODO IMPLEMENT THIS METHOD
        // make this a part of the btnHandleGoBack instead.
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

}