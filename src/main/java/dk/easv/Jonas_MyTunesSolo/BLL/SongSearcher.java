package dk.easv.Jonas_MyTunesSolo.BLL;
//PROJECT IMPORTS
import dk.easv.Jonas_MyTunesSolo.BE.Song;
//JAVA IMPORTS
import java.util.ArrayList;
import java.util.List;

public class SongSearcher {


    /**
     * searches all song objects in DB to see if they match the query in either Title, Artist or Genre.
     * @param searchBase list of song objects, from DB
     * @param searchQuery the query to compare with
     * @return returns a list of song objects that contain the query in either title, artist or genre.
     */
    public List<Song> search(List<Song> searchBase, String searchQuery) {
        List<Song> searchResults = new ArrayList<>();

        for (Song song : searchBase) {
            if (compareToTitle(searchQuery, song) || compareToArtist(searchQuery, song) || compareToGenre(searchQuery, song)) {
                searchResults.add(song);
            }
        }
        return searchResults;
    }

    /**
     * compares query to song Title
     * @param searchQuery the query we are comparing with
     * @param song used to get Title to compare to
     */
    public boolean compareToTitle(String searchQuery, Song song){
        return song.getTitle().toLowerCase().contains(searchQuery.toLowerCase());
    }

    /**
     * compares query to song Artist
     * @param searchQuery the query we are comparing with
     * @param song used to get Artist to compare to
     */
    public boolean compareToArtist(String searchQuery, Song song){
        return song.getArtistName().toLowerCase().contains(searchQuery.toLowerCase());
    }

    /**
     * compares query to song Genre
     * @param searchQuery the query we are comparing with
     * @param song used to get Genre to compare to
     */
    public boolean compareToGenre(String searchQuery, Song song){
        return song.getGenreName().toLowerCase().contains(searchQuery.toLowerCase());
    }
}
