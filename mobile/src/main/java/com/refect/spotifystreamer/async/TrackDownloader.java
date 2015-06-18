package com.refect.spotifystreamer.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.refect.spotifystreamer.adapters.ArtistAdapter;
import com.refect.spotifystreamer.adapters.TrackAdapter;

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
public class TrackDownloader extends AsyncTask<String, Void, List<Track>> {

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
    protected List<Track> doInBackground(String... params) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        Map<String, Object> country = new HashMap<>();
        country.put("country", "US");
        Tracks tracks = spotify.getArtistTopTrack(params[0], country);

        return tracks.tracks;
    }

    @Override
    protected void onPostExecute(List<Track> tracks) {
        trackAdapter.setModels(tracks);
    }
}
