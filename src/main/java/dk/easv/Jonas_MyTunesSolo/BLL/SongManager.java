package dk.easv.Jonas_MyTunesSolo.BLL;

import dk.easv.Jonas_MyTunesSolo.BE.Song;
import dk.easv.Jonas_MyTunesSolo.DAL.SongsDAO_db;

import java.io.IOException;

public class SongManager {

    private SongsDAO_db SongsDAO;

    public SongManager() throws IOException {
        SongsDAO = new SongsDAO_db();
    }

    public Song getAllSongs() {
        return SongsDAO.getAllSongs()
    }
}
