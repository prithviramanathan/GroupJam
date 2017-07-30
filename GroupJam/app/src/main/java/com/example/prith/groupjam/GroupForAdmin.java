package com.example.prith.groupjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.picasso.Picasso;

public class GroupForAdmin extends AppCompatActivity implements
        Player.NotificationCallback, ConnectionStateCallback {
    public static final String mAccessCode = "";
    String accessCode;
    String groupName;
    String groupID;
    String spotifyAccessToken;
    FirebaseAuth auth;
    FirebaseDatabase mDatabase;
    DatabaseReference allGroupsReference;
    DatabaseReference mThisGroup;
    DatabaseReference mQueue;
    final int SPOTIFY_SIGN_IN_CODE = 1337;

    final String SPOTIFY_CLIENT_ID = "b65b55195dfe4a1b99d1422eb6014ceb";
    final String SPOTIFY_URI = "myGroupJam://callback";
    private Player mPlayer;

    TextView accessCodeView;
    TextView capacityView;
    RecyclerView mSongRecycler;
    FirebaseRecyclerAdapter<Song, SongCollectionViewHolder> mAdapter;

    FloatingActionButton searchOnSpotify;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_for_admin);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accessCode = getIntent().getStringExtra(mAccessCode);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        allGroupsReference = mDatabase.getReference("Groups");


        accessCodeView = (TextView) findViewById(R.id.accessCodeDisplay);
        capacityView = (TextView) findViewById(R.id.memberCountDisplay);
        String accessCodeDisplay = "Access Code: " + accessCode;
        accessCodeView.setText(accessCodeDisplay);

        mSongRecycler = (RecyclerView) findViewById(R.id.songsForAdmins);
        mSongRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        allGroupsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot group: dataSnapshot.getChildren()){
                    Log.d("group", "" + group.child("accessCode").getValue());
                    if(accessCode.equals(group.child("accessCode").getValue())){
                        groupID = group.getRef().getKey();

                        allGroupsReference.child(groupID).child("members").child(auth.getCurrentUser().getUid()).setValue("member");
                        String cap = "Members: " + group.child("members").getChildrenCount();
                        toolbar.setTitle("" + group.child("name").getValue());
                        capacityView.setText(cap);
                    }
                }
                mThisGroup = allGroupsReference.child(groupID);
                mQueue = mThisGroup.child("Queue");
                mThisGroup.keepSynced(true);
                mQueue.keepSynced(true);
                mAdapter = new FirebaseRecyclerAdapter<Song, SongCollectionViewHolder>(Song.class,
                        R.layout.search_result_item, SongCollectionViewHolder.class, mQueue) {
                    @Override
                    protected void populateViewHolder(SongCollectionViewHolder viewHolder, Song model, int position) {
                        viewHolder.titleView.setText(model.getSongTitle());
                        viewHolder.artistView.setText(model.getArtistName());
                        Picasso.with(viewHolder.posterView.getContext()).load(model.getPosterURL()).into(viewHolder.posterView);
                    }
                };
                mSongRecycler.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //spotify log in on activity start
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(SPOTIFY_CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                SPOTIFY_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, SPOTIFY_SIGN_IN_CODE, request);

        searchOnSpotify = (FloatingActionButton) findViewById(R.id.fabSearchSpotify);
        searchOnSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(getApplicationContext(), SearchOnSpotify.class);
                String extra = groupID + " " + spotifyAccessToken;
                search.putExtra(SearchOnSpotify.GroupIDandAccessToken, extra);
                Log.d("Start Search", spotifyAccessToken);
                startActivity(search);
            }
        });
    }


    //log out button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflator = getMenuInflater();
        menuInflator.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logoutButton){
            auth.signOut();
            Intent restart = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(restart);;
        }
        return true;
    }






    //all the activity results possible for this activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPOTIFY_SIGN_IN_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            spotifyAccessToken = response.getAccessToken();
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), SPOTIFY_CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(GroupForAdmin.this);
                        mPlayer.addNotificationCallback(GroupForAdmin.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }

    }

    @Override
    public void onLoggedIn() {
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Could not log in");
    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {

    }

    @Override
    public void onPlaybackError(Error error) {

    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    public static class SongCollectionViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView artistView;
        public ImageView posterView;

        public SongCollectionViewHolder(View view) {
            super(view);
            titleView = (TextView) itemView.findViewById(R.id.titleTextView);
            artistView = (TextView) itemView.findViewById(R.id.singerTextView);
            posterView = (ImageView) itemView.findViewById(R.id.albumArtView);
        }

    }
}
