package dk.easv.Jonas_MyTunesSolo.BE;

public class Playlist {
    private int id;
    private String name;
    private int songCount;

    public int getSongCount() {return songCount;}
    public void setSongCount(int songCount) {this.songCount = songCount;}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public Playlist(int id, String name, int songCount) {
        this.id = id;
        this.name = name;
        this.songCount = songCount;
    }
    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
