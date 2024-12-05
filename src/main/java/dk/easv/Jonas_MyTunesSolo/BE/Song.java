package dk.easv.Jonas_MyTunesSolo.BE;

public class Song {

    private int songId;
    private String title;
    private String artistName;
    private String genreName;
    private String songFilePath;
    private int duration;
    private Integer genreId;


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

    //Using Integer genreId, so it can be null.
    public Integer getGenreId() {return genreId;}
    public void setGenreId(Integer genreId) {this.genreId = genreId;}

    public Song(int songId, String title, String artistName, String genreName, String songFilePath, int duration) {
        this.songId = songId;
        this.title = title;
        this.artistName = artistName;
        this.genreName = genreName;
        this.songFilePath = songFilePath;
        this.duration = duration;
    }
    //Using Integer genreId, so it can be null.
    public Song(int songId, String title, String artistName, Integer genreId, String songFilePath, int duration) {
        this.songId = songId;
        this.title = title;
        this.artistName = artistName;
        this.genreId = genreId;
        this.songFilePath = songFilePath;
        this.duration = duration;
    }

    public String getFormattedDuration() {
        //removes decimals, so it only gets whole minutes
        int minutes = this.duration / 60;
        //gets whats left after converting to minutes.
        int seconds = this.duration % 60;
        //makes it user-friendly by formatting it to MM:SS
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return title + " ";
    }

    //Only using toString for the list view now, and I think it looks better with just the title.
    /*
        public String toString() {
        return songId + " | " + title + " | " + artistName + " | " + genreId + " | " + songFilePath + " | " + duration;
    }
     */
}

