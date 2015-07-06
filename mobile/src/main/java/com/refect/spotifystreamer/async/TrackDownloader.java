package com.refect.spotifystreamer.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.refect.spotifystreamer.adapters.ArtistAdapter;
import com.refect.spotifystreamer.adapters.TrackAdapter;
import com.refect.spotifystreamer.models.TrackModel;
import com.refect.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * Created by anelson on 6/15/15.
 */
public class TrackDownloader extends AsyncTask<String, Void, ArrayList<TrackModel>> {

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
    protected ArrayList<TrackModel> doInBackground(String... params) {

        if(params[0].equals(Utils.GET_ALBUM_TRACKS)) {
            return getAlbumTracks(params);
        } else if(params[0].equals(Utils.GET_TOP_TRACKS)) {
            return getTopTracks(params);
        } else {
            return null;
        }

    }

    /**
     *
     * @param params
     * @return
     */
    public ArrayList<TrackModel> getAlbumTracks(String[] params) {
        ArrayList<TrackModel> results = new ArrayList<>();

        try {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Pager<Track> albumTracks = spotify.getAlbumTracks(params[1]);

            for (Track track : albumTracks.items) {
                TrackModel trackModel = new TrackModel();
                trackModel.setId(track.id);
                trackModel.setTitle(track.name);
                trackModel.setPreviewUrl(track.preview_url);

//                trackModel.setAlbum(track.album.name);
//
//                if (track.artists.size() > 0) {
//                    trackModel.setArtist(track.artists.get(0).name);
//                }
//
//                if (track.album.images.size() > 0) {
//                    trackModel.setUrl(track.album.images.get(0).url);
//                }

                results.add(trackModel);
            }
        } catch(RetrofitError error) {
            Log.e("Error: ", error.getMessage());
            return null;
        }
        return results;
    }

    /**
     *
     * @param params
     * @return
     */
    public ArrayList<TrackModel> getTopTracks(String[] params) {
        ArrayList<TrackModel> results = new ArrayList<>();

        try {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            Map<String, Object> country = new HashMap<>();
            country.put("country", "US");
            Tracks topTracks = spotify.getArtistTopTrack(params[1], country);

            for (Track track : topTracks.tracks) {
                TrackModel trackModel = new TrackModel();
                trackModel.setId(track.id);
                trackModel.setTitle(track.name);
                trackModel.setAlbum(track.album.name);
                trackModel.setPreviewUrl(track.preview_url);

                if (track.artists.size() > 0) {
                    trackModel.setArtist(track.artists.get(0).name);
                }

                if (track.album.images.size() > 0) {
                    trackModel.setUrl(track.album.images.get(0).url);
                }

                results.add(trackModel);
            }
        } catch(RetrofitError error) {
            Log.e("Error: ", error.getMessage());
            return null;
        }
        return results;
    }

    @Override
    protected void onPostExecute(ArrayList<TrackModel> tracks) {

        if(tracks == null) {
            Toast.makeText(mContext, "Oh no! Something went wrong :( Make sure you have an internet connection", Toast.LENGTH_SHORT).show();
        }else if(tracks.isEmpty()) {
            Toast.makeText(mContext, "No top tracks found for this artist. Please redefine your search", Toast.LENGTH_SHORT).show();
        } else {
            trackAdapter.setModels(tracks);
        }
    }
}
