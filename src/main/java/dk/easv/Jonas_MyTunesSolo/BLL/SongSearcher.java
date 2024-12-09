package dk.easv.Jonas_MyTunesSolo.BLL;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Song;
//JAVA IMPORTS
import java.util.ArrayList;
import java.util.List;

public class SongSearcher {



    public List<Song> search(List<Song> searchBase, String searchQuery) {
        List<Song> searchResults = new ArrayList<Song>();

        for (Song song : searchBase) {
            if (compareToTitle(searchQuery, song) || compareToArtist(searchQuery, song) || compareToGenre(searchQuery, song)) {
                searchResults.add(song);
            }
        }
        return searchResults;
    }

    public boolean compareToTitle(String searchQuery, Song song){
        return song.getTitle().toLowerCase().contains(searchQuery.toLowerCase());
    }

    public boolean compareToArtist(String searchQuery, Song song){
        return song.getArtistName().toLowerCase().contains(searchQuery.toLowerCase());
    }

    public boolean compareToGenre(String searchQuery, Song song){
        return song.getGenreName().toLowerCase().contains(searchQuery.toLowerCase());
    }
}
