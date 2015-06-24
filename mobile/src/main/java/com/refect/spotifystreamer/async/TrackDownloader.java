package com.refect.spotifystreamer.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.refect.spotifystreamer.adapters.ArtistAdapter;
import com.refect.spotifystreamer.adapters.TrackAdapter;
import com.refect.spotifystreamer.models.TrackModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by anelson on 6/15/15.
 */
public class TrackDownloader extends AsyncTask<String, Void, List<TrackModel>> {

    private TrackAdapter trackAdapter;
    private Context mContext;

    public TrackDownloader(Context ctx, TrackAdapter trackAdapter) {
        this.mContext = ctx;
        this.trackAdapter = trackAdapter;
    }

    @Override
    protected void onPreExecute() {
        trackAdapter.clear();
    }

    @Override
    protected List<TrackModel> doInBackground(String... params) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        Map<String, Object> country = new HashMap<>();
        country.put("country", "US");
        Tracks tracks = spotify.getArtistTopTrack(params[0], country);
        ArrayList<TrackModel> results = new ArrayList<>();

        for(Track track : tracks.tracks) {
            TrackModel trackModel = new TrackModel();
            trackModel.setId(track.id);
            trackModel.setTitle(track.name);
            trackModel.setAlbum(track.album.name);

            if(track.artists.size() > 0) {
                trackModel.setArtist(track.artists.get(0).name);
            }

            if(track.album.images.size() > 0) {
                trackModel.setUrl(track.album.images.get(0).url);
            }

            results.add(trackModel);
        }

        return results;
    }

    @Override
    protected void onPostExecute(List<TrackModel> tracks) {

        if(tracks.isEmpty()) {
            Toast.makeText(mContext, "No top tracks found for this artist. Please redefine your search", Toast.LENGTH_SHORT).show();
        } else {
            trackAdapter.setModels(tracks);
        }
    }
}
