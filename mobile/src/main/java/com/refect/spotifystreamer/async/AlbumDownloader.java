package com.refect.spotifystreamer.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.adapters.AlbumAdapter;
import com.refect.spotifystreamer.adapters.ArtistAdapter;
import com.refect.spotifystreamer.adapters.TrackAdapter;
import com.refect.spotifystreamer.models.AlbumModel;
import com.refect.spotifystreamer.models.ArtistModel;
import com.refect.spotifystreamer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.RetrofitError;

///**
// * Created by anelson on 6/15/15.
// */
//public class AlbumDownloader extends AsyncTask<String, Void, List<AlbumModel>> {
//
//    private TrackAdapter trackAdapter;
//    private AlbumAdapter albumAdapter;
//    private LinearLayout albumLayout;
//    private String artistId;
//    private Context mContext;
//
//    public AlbumDownloader(Context ctx, LinearLayout albumLayout, AlbumAdapter albumAdapter, TrackAdapter trackAdapter, String artistId) {
//        this.mContext = ctx;
//        this.albumLayout = albumLayout;
//        this.albumAdapter = albumAdapter;
//        this.trackAdapter = trackAdapter;
//        this.artistId = artistId;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        albumAdapter.clear();
//    }
//
//    @Override
//    protected List<AlbumModel> doInBackground(String... params) {
//        ArrayList<AlbumModel> models = new ArrayList<>();
//        AlbumModel topTracksModel = new AlbumModel();
//        topTracksModel.setName("Top Tracks");
//        topTracksModel.setId("Top Tracks");
//        topTracksModel.setUrl(mContext.getResources().getString(R.string.top_tracks));
//        models.add(topTracksModel);
//
//        try {
//            SpotifyApi api = new SpotifyApi();
//            SpotifyService spotify = api.getService();
//            Pager<Album> results = spotify.getArtistAlbums(params[0]);
//
//            for(Album album : results.items) {
//                AlbumModel model = new AlbumModel();
//                model.setId(album.id);
//                model.setName(album.name);
//                if(album.images.size() > 0) {
//                    model.setUrl(album.images.get(0).url);
//                }
//                models.add(model);
//            }
//        } catch(RetrofitError error) {
//            Log.e("Error: ", error.getMessage());
//            return null;
//        }
//
//        return models;
//    }
//
//    @Override
//    protected void onPostExecute(List<AlbumModel> albums) {
//
//        if(albums == null) {
//            Toast.makeText(mContext, "Oh no! Something went wrong :( Make sure you have an internet connection", Toast.LENGTH_SHORT).show();
//        } else if(albums.isEmpty()) {
//            Toast.makeText(mContext, "This artist has no albums. Please redefine your search", Toast.LENGTH_SHORT).show();
//        } else {
//            for(AlbumModel album : albums) {
//                //inflate view
//                View view = LayoutInflater.from(mContext).inflate(R.layout.view_album_preview, null);
//                ImageView ivAlbumImage = (ImageView) view.findViewById(R.id.iv_album_image);
//                TextView tvArtistName = (TextView) view.findViewById(R.id.tv_artist_name);
//                TextView tvAlbumName = (TextView) view.findViewById(R.id.tv_album_name);
//
//                Picasso.with(mContext).load(album.getUrl()).into(ivAlbumImage);
//                tvArtistName.setText(album.getName());
//                tvAlbumName.setText(album.getName());
//
//                LinearLayout.LayoutParams params =
//                        new LinearLayout.LayoutParams((Utils.getScreenWidth(mContext)/3) - 20, LinearLayout.LayoutParams.MATCH_PARENT);
//                params.leftMargin = 10;
//                params.rightMargin = 10;
//                view.setLayoutParams(params);
//
//                albumLayout.addView(view);
//            }
//
//            if(artistId != null && trackAdapter != null) {
//                new TrackDownloader(mContext, trackAdapter).execute(Utils.GET_TOP_TRACKS, artistId);
//            }
//        }
//    }
//}
