package dk.easv.Jonas_MyTunesSolo.DAL;

import dk.easv.Jonas_MyTunesSolo.BE.Playlist;

import java.util.List;

public class PlaylistDAO_db implements IPlaylistDataAccess{
    @Override
    public List<Playlist> getAllPlaylists() {
        return List.of();
    }

    @Override
    public Playlist createPlaylist(Playlist newPlaylist) {
        return null;
    }

    @Override
    public void updatePlaylist(Playlist playlist) {

    }

    @Override
    public void deletePlaylist(Playlist playlist) {

    }
}
