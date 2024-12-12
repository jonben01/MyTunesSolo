package dk.easv.Jonas_MyTunesSolo.BE;

public class PlaylistSong {
    private Integer psId;
    private int playlistId;
    private Song song;
    private int orderIndex;

    public Integer getPsId() {return psId;}
    public void setPsId(Integer psId) {this.psId = psId;}

    public int getPlaylistId() {return playlistId;}
    public void setPlaylistId(int playlistId) {this.playlistId = playlistId;}

    public Song getSong() {return song;}
    public void setSong(Song song) {this.song = song;}

    public int getOrderIndex() {return orderIndex;}
    public void setOrderIndex(int orderIndex) {this.orderIndex = orderIndex;}

    public PlaylistSong(Integer psId, int playlistId, Song song, int orderIndex) {
        this.psId = psId;
        this.playlistId = playlistId;
        this.song = song;
        this.orderIndex = orderIndex;
    }

    //used by playlistSong table view column
    public String getSongTitle() {
        return song != null ? song.getTitle() : "";
    }

    //used by playlistSong table view column - changing orderIndex, so it starts at 1, not 0.
    public String getFormattedOrderIndex() {
        return orderIndex + 1 + ".";
    }
}
