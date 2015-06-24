package com.refect.spotifystreamer.async;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.refect.spotifystreamer.adapters.ArtistAdapter;
import com.refect.spotifystreamer.models.ArtistModel;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by anelson on 6/15/15.
 */
public class ArtistDownloader extends AsyncTask<String, Void, List<ArtistModel>> {

    private ArtistAdapter artistAdapter;
    private Context mContext;

    public ArtistDownloader(Context ctx, ArtistAdapter artistAdapter) {
        this.mContext = ctx;
        this.artistAdapter = artistAdapter;
    }

    @Override
    protected void onPreExecute() {
        artistAdapter.clear();
    }

    @Override
    protected List<ArtistModel> doInBackground(String... params) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        ArtistsPager results = spotify.searchArtists(params[0]);
        ArrayList<ArtistModel> models = new ArrayList<>();
        for(Artist artist : results.artists.items) {

            ArtistModel model = new ArtistModel();
            model.setName(artist.name);
            model.setId(artist.id);
            if(artist.images.size() > 0) {
                model.setUrl(artist.images.get(0).url);
            }
            if(artist.genres.size() > 0) {
                model.setGenre(artist.genres.get(0));
            }
            models.add(model);
        }

        return models;
    }

    @Override
    protected void onPostExecute(List<ArtistModel> artists) {

        if(artists.isEmpty()) {
            Toast.makeText(mContext, "No artists match this search. Please redefine your search", Toast.LENGTH_SHORT).show();
        } else {
            artistAdapter.setModels(artists);
        }
    }
}
