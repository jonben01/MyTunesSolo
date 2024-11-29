package dk.easv.Jonas_MyTunesSolo.BE;

public class Song {

    private int songId;
    private String title;
    private String artistName;
    private String genreName;
    private String songFilePath;
    private int duration;

    //Getters and setters for variables
    public int getSongID() {return songId;}
    public void setSongID(int songID) {this.songId = songID;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getArtistName() {return artistName;}
    public void setArtistName(String artistName) {this.artistName = artistName;}

    public String getGenreName() {return genreName;}

    public void setGenreName(String genreName) {this.genreName = genreName;}

    public String getSongFilePath() {return songFilePath;}
    public void setSongFilePath(String songFilePath) {this.songFilePath = songFilePath;}

    public int getDuration() {return duration;}
    public void setDuration(int duration) {this.duration = duration;}

    public Song(int songId, String title, String artistName, String genreName, String songFilePath, int duration) {
        this.songId = songId;
        this.title = title;
        this.artistName = artistName;
        this.genreName = genreName;
        this.songFilePath = songFilePath;
        this.duration = duration;
    }
}

