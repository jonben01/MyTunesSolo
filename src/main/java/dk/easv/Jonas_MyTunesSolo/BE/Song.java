package dk.easv.Jonas_MyTunesSolo.BE;

public class Song {

    private int songId;
    private String songName;
    private String songArtist;
    private String songGenre;
    private String songFilePath;

    //Getters and setters for variables
    public int getSongID() {return songId;}

    public void setSongID(int songID) {this.songId = songID;}

    public String getSongName() {return songName;}

    public void setSongName(String songName) {this.songName = songName;}

    public String getSongArtist() {return songArtist;}

    public void setSongArtist(String songArtist) {this.songArtist = songArtist;}

    public String getSongGenre() {return songGenre;}

    public void setSongGenre(String songGenre) {this.songGenre = songGenre;}

    public String getSongFilePath() {return songFilePath;}

    public void setSongFilePath(String songFilePath) {this.songFilePath = songFilePath;}

    /**
     *
     * @param songID
     * @param songName
     * @param songArtist
     * @param songGenre
     * @param songFilePath
     */
    public Song(int songID, String songName, String songArtist, String songGenre, String songFilePath) {
        this.songId = songID;
        this.songName = songName;
        this.songArtist = songArtist;
        this.songGenre = songGenre;
        this.songFilePath = songFilePath;
    }
}
