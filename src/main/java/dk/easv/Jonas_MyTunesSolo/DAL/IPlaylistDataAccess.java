package dk.easv.Jonas_MyTunesSolo.DAL;

import dk.easv.Jonas_MyTunesSolo.BE.Playlist;

import java.util.List;

public interface IPlaylistDataAccess {

    //ADD CRUD METHODS FOR PLAYLIST

    List<Playlist> getAllPlaylists();

    Playlist createPlaylist(Playlist newPlaylist);

    void updatePlaylist(Playlist playlist);

    void deletePlaylist(Playlist playlist);

}
