package dk.easv.Jonas_MyTunesSolo.GUI;
//PROJECT IMPORTS
import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.BLL.SongManager;

//JAVA IMPORTS
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

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

    public Song createSong(Song newSong) throws SQLServerException {
        Song songCreated = songManager.createSong(newSong);
        songsToBeViewed.add(songCreated);
        return songCreated;
    }

    public Song getSongByFilePath(String filePath) {
        //go through all songs
        for (Song song : songManager.getAllSongs()) {
            //Check if there's a song with the provided file path
            if (song.getSongFilePath().equals(filePath)) {
                //if there is, return that song
                return song;
            }
        }
        return null;
    }

    public void deleteSong(Song songToBeDeleted) throws SQLServerException {
        songsToBeViewed.remove(songToBeDeleted);
        songManager.deleteSong(songToBeDeleted);
    }

    public void updateSong(Song songToBeEdited) throws SQLException {
        songManager.updateSong(songToBeEdited);
        //might be missing something here, but code seems functional
    }
}
