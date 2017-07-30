package com.example.prith.groupjam;

import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchOnSpotify extends AppCompatActivity {
    public static final String GroupIDandAccessToken = "";
    FirebaseAuth auth;
    FirebaseDatabase mDatabase;
    DatabaseReference mAllGroupsReference;
    DatabaseReference mThisGroupReference;
    SpotifyApi mApi;

    RecyclerView mRecyclerView;

    List<Track> mTracks;

    SearchResultAdapter mSearchAdapter;

    String groupID;
    String userAccessToken;

    ImageButton searchSpotify;
    EditText query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_on_spotify);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set up firebase
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mAllGroupsReference = mDatabase.getReference("Groups");

        //get the group id and the access token
        groupID = getIntent().getStringExtra(GroupIDandAccessToken).split(" ")[0];
        userAccessToken = getIntent().getStringExtra(GroupIDandAccessToken).split(" ")[1];
        Log.d("Group ID", groupID);
        Log.d("userAccessToken", userAccessToken);
        mTracks = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.spotifyResults);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSearchAdapter = new SearchResultAdapter(mTracks, groupID);
        mRecyclerView.setAdapter(mSearchAdapter);

        //to search we are using kaaes's spotify api
        mApi = new SpotifyApi();
        mApi.setAccessToken(userAccessToken);
        final SpotifyService spotify = mApi.getService();

        searchSpotify = (ImageButton) findViewById(R.id.searchSpotifyButton);
        query = (EditText) findViewById(R.id.spotifySearch);
        searchSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sQuery = query.getText().toString();
                spotify.searchTracks(sQuery, new Callback<TracksPager>() {
                    @Override
                    public void success(TracksPager tracksPager, Response response) {
                        List<Track> tracks = tracksPager.tracks.items;
                        mTracks.clear();
                        for(Track t: tracks){
                            mTracks.add(t);
                        }
                        mSearchAdapter.notifyDataSetChanged();
                        if(tracks == null){
                            Log.d("NO TRACKS", "NO tracks");
                        }
                        else{
                            List<ArtistSimple> artists = tracks.get(0).artists;
                            if(artists == null){
                                Log.d("Artists", "bail");
                            }
                            Log.d("Image URL", "" + tracks.get(0).album.images.get(2).url + " Size " + tracks.get(0).album.images.get(2).height);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
