package com.example.prith.groupjam;

/**
 * Created by prith on 6/12/2017.
 */

public class Song {

    private String songTitle;
    private String ablumName;
    private String artistName;
    private String posterURL;
    private String URL;

    public Song(String songTitle, String ablumName, String artistName, String posterURL, String URL) {
        this.songTitle = songTitle;
        this.ablumName = ablumName;
        this.artistName = artistName;
        this.posterURL = posterURL;
        this.URL = URL;
    }

    public Song(){
        //do nothing
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getAblumName() {
        return ablumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getURL() {
        return URL;
    }
}
