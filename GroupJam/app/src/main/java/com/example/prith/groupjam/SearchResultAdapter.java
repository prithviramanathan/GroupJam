package com.example.prith.groupjam;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.player.Metadata;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by prith on 7/21/2017.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    List<Track> searchResults;
    String groupID;
    FirebaseDatabase mDatabase;
    DatabaseReference mAllGroupsReference;
    DatabaseReference mThisGroupReference;
    DatabaseReference mQueue;

    public SearchResultAdapter(List<Track> tracks, String mID){
        Log.d("Created adapter: ", "Hi");
        searchResults = tracks;
        groupID = mID;
        mDatabase = FirebaseDatabase.getInstance();
        mAllGroupsReference = mDatabase.getReference("Groups");
        mThisGroupReference = mAllGroupsReference.child(groupID);
        mQueue = mThisGroupReference.child("Queue");

    }

    //populates song results
    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // a LayoutInflater turns a layout XML resource into a View object.
        final View songListItem = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.search_result_item, parent, false);
        return new ViewHolder(songListItem);
    }



    //loads data for that song into the holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Track mTrack = searchResults.get(position);
        Log.d("Song", mTrack.name);
        holder.titleView.setText(mTrack.name);
        holder.artistView.setText(mTrack.artists.get(0).name);
        final String posterURL = mTrack.album.images.get(0).url;
        Picasso.with(holder.posterView.getContext()).load(posterURL).into(holder.posterView);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTitle = mTrack.name;
                String mArtist = mTrack.artists.get(0).name;
                String mAlbum = mTrack.album.name;
                String mURL = mTrack.uri;
                Song mSong = new Song(mTitle, mAlbum, mArtist, posterURL, mURL);
                mQueue.push().setValue(mSong);
                Toast.makeText(view.getContext(), mTitle + " was added to this group's queue", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }


    /**
     * Viewholder class that stores the data for each movie
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public TextView titleView;
        public TextView artistView;
        public ImageView posterView;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            titleView = (TextView) itemView.findViewById(R.id.titleTextView);
            artistView = (TextView) itemView.findViewById(R.id.singerTextView);
            posterView = (ImageView) itemView.findViewById(R.id.albumArtView);
        }
    }
}
