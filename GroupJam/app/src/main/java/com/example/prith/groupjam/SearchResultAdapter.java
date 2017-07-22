package com.example.prith.groupjam;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.player.Metadata;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by prith on 7/21/2017.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    List<Track> searchResults;

    public SearchResultAdapter(List<Track> tracks){
        Log.d("Created adapter: ", "Hi");
        searchResults = tracks;
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
        String posterURL = mTrack.album.images.get(0).url;
        Picasso.with(holder.posterView.getContext()).load(posterURL).into(holder.posterView);
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
