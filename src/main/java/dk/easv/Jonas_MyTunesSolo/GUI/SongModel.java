package dk.easv.Jonas_MyTunesSolo.GUI;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.BLL.SongManager;

//JAVA IMPORTS
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SongModel {



    private ObservableList<Song> songsToBeViewed;
    private SongManager songManager;

    public SongModel() throws Exception {
        songManager = new SongManager();
        songsToBeViewed = FXCollections.observableArrayList();
        songsToBeViewed.addAll(songManager.getAllSongs());
    }

    public ObservableList<Song> getSongsToBeViewed() { return songsToBeViewed; }

    public void refreshSong() {
        songsToBeViewed.clear();
        songsToBeViewed.addAll(songManager.getAllSongs());
    }
}
