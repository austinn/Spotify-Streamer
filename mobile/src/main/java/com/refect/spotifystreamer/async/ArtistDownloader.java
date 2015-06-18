package com.refect.spotifystreamer.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.refect.spotifystreamer.adapters.ArtistAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by anelson on 6/15/15.
 */
public class ArtistDownloader extends AsyncTask<String, Void, List<Artist>> {

    private ArtistAdapter artistAdapter;
    private Context mContext;
    //private ProgressDialog progress;

    public ArtistDownloader(Context ctx, ArtistAdapter artistAdapter) {
        this.mContext = ctx;
        this.artistAdapter = artistAdapter;
        //this.progress = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        artistAdapter.clear();

//        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progress.show();
    }

    @Override
    protected List<Artist> doInBackground(String... params) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        ArtistsPager results = spotify.searchArtists(params[0]);

        return results.artists.items;
    }

    @Override
    protected void onPostExecute(List<Artist> artists) {
        //progress.dismiss();
        artistAdapter.setModels(artists);
    }
}
