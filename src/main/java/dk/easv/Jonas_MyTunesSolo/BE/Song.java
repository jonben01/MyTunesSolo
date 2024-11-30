package dk.easv.Jonas_MyTunesSolo.BE;

public class Song {

    private int songId;
    private String title;
    private String artistName;
    private String genreName;
    private String songFilePath;
    private int duration;
    private int genreId;


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

    public int getGenreId() {return genreId;}
    public void setGenreId(int genreId) {this.genreId = genreId;}

    public Song(int songId, String title, String artistName, String genreName, String songFilePath, int duration) {
        this.songId = songId;
        this.title = title;
        this.artistName = artistName;
        this.genreName = genreName;
        this.songFilePath = songFilePath;
        this.duration = duration;
    }
    public Song(int songId, String title, String artistName, int genreId, String songFilePath, int duration) {
        this.songId = songId;
        this.title = title;
        this.artistName = artistName;
        this.genreId = genreId;
        this.songFilePath = songFilePath;
        this.duration = duration;

    }

    @Override
    public String toString() {
        return songId + " | " + title + " | " + artistName + " | " + genreId + " | " + songFilePath + " | " + duration;
    }
}

