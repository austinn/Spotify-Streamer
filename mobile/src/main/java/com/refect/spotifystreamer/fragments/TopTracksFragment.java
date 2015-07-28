package com.refect.spotifystreamer.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.adapters.AlbumAdapter;
import com.refect.spotifystreamer.adapters.TrackAdapter;
import com.refect.spotifystreamer.async.TrackDownloader;
import com.refect.spotifystreamer.listeners.OnRecyclerViewItemClickListener;
import com.refect.spotifystreamer.models.AlbumModel;
import com.refect.spotifystreamer.models.TrackModel;
import com.refect.spotifystreamer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.RetrofitError;


public class TopTracksFragment extends Fragment {

    private String artistName;
    private String artistId;
    private String artistImageUrl;

    private RecyclerView rvTopTracks;
    private TrackAdapter trackAdapter;
    private ArrayList<TrackModel> trackModels;

    private ImageView vpArtist;
    private LinearLayout layoutAlbums;

    private ViewPager vpAlbums;
    private AlbumAdapter albumAdapter;

    private boolean isTwoPane;

    /**
     *
     * @return A new instance of fragment ArtistFragment.
     */
    public static TopTracksFragment newInstance(HashMap<String, String> params) {
        TopTracksFragment fragment = new TopTracksFragment();
        Bundle args = new Bundle();
        args.putString(Utils.INTENT_ARTIST_NAME, params.get(Utils.INTENT_ARTIST_NAME));
        args.putString(Utils.INTENT_ARTIST_ID, params.get(Utils.INTENT_ARTIST_ID));
        args.putString(Utils.INTENT_ARTIST_IMAGE_URL, params.get(Utils.INTENT_ARTIST_IMAGE_URL));
        args.putString(Utils.INTENT_TWO_PANE, params.get(Utils.INTENT_TWO_PANE));
        fragment.setArguments(args);
        return fragment;
    }

    public TopTracksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            trackModels = savedInstanceState.getParcelableArrayList(Utils.KEY_TRACK_MODELS);
        }

        if (getArguments() != null) {
            artistName = getArguments().getString(Utils.INTENT_ARTIST_NAME);
            artistId = getArguments().getString(Utils.INTENT_ARTIST_ID);
            artistImageUrl = getArguments().getString(Utils.INTENT_ARTIST_IMAGE_URL);
            isTwoPane = Boolean.parseBoolean(getArguments().getString(Utils.INTENT_TWO_PANE));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_top_tracks_new, container, false);

        setHasOptionsMenu(true);
        initUI(view);

        trackAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<TrackModel>() {
            @Override
            public void onItemClick(View view, TrackModel model) {
                if (isTwoPane) {
                    showPlaybackDialog(trackAdapter.getModels(), model, artistImageUrl);
                } else {
                    Fragment playbackFragment = PlaybackFragment.newInstance(trackAdapter.getModels(), model, artistImageUrl);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, playbackFragment, "playback")
                            .addToBackStack("playback")
                            .commit();
                }
            }
        });

        if(savedInstanceState == null) {
            new AlbumDownloader(getActivity(), albumAdapter, trackAdapter, artistId).execute(artistId);
        }

        return view;
    }

    /**
     *
     * @param models
     * @param model
     * @param artistImageUrl
     */
    private void showPlaybackDialog(ArrayList<TrackModel> models, TrackModel model, String artistImageUrl) {

        // Create and show the dialog.
        DialogFragment newFragment = MyDialogFragment.newInstance(1, models, model, artistImageUrl);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");

    }

    /**
     *
     * @param view
     */
    private void initUI(View view) {
        albumAdapter = new AlbumAdapter(getActivity());
        layoutAlbums = (LinearLayout) view.findViewById(R.id.layout_albums);
        vpArtist = (ImageView) view.findViewById(R.id.iv_artist);

        Picasso.with(getActivity()).load(artistImageUrl).into(vpArtist);

        trackAdapter = new TrackAdapter(getActivity());
        if(trackModels != null) {
            trackAdapter.setModels(trackModels);
        }

        rvTopTracks = (RecyclerView) view.findViewById(R.id.rv_top_tracks);
        rvTopTracks.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTopTracks.setItemAnimator(new DefaultItemAnimator());
        rvTopTracks.setAdapter(trackAdapter);
    }

    /**
     *
     * @param albums
     */
    public void populateAlbums(List<AlbumModel> albums) {
        for(final AlbumModel album : albums) {
            //inflate view
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_album_preview, null);
            ImageView ivAlbumImage = (ImageView) view.findViewById(R.id.iv_album_image);
            TextView tvArtistName = (TextView) view.findViewById(R.id.tv_artist_name);
            TextView tvAlbumName = (TextView) view.findViewById(R.id.tv_album_name);

            Picasso.with(getActivity()).load(album.getUrl()).into(ivAlbumImage);
            tvArtistName.setText(artistName);
            tvAlbumName.setText(album.getName());

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams((Utils.getScreenWidth(getActivity())/3) - 20, LinearLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin = 10;
            params.rightMargin = 10;
            params.bottomMargin = 5;
            view.setLayoutParams(params);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (album.getId().equals("Top Tracks")) {
                        new TrackDownloader(getActivity(), trackAdapter).execute(Utils.GET_TOP_TRACKS, artistId);
                    } else {
                        new TrackDownloader(getActivity(), trackAdapter).execute(Utils.GET_ALBUM_TRACKS, album.getId());
                    }
                }
            });

            layoutAlbums.addView(view);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(outState != null && trackAdapter != null) {
            ArrayList<TrackModel> parceable = new ArrayList<>();
            for (TrackModel model : trackAdapter.getModels()) {
                parceable.add(model);
            }

            outState.putParcelableArrayList(Utils.KEY_TRACK_MODELS, parceable);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_top_tracks, menu);

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Created by anelson on 6/15/15.
     */
    public class AlbumDownloader extends AsyncTask<String, Void, List<AlbumModel>> {

        private TrackAdapter trackAdapter;
        private AlbumAdapter albumAdapter;
        private String artistId;
        private Context mContext;

        public AlbumDownloader(Context ctx, AlbumAdapter albumAdapter, TrackAdapter trackAdapter, String artistId) {
            this.mContext = ctx;
            this.albumAdapter = albumAdapter;
            this.trackAdapter = trackAdapter;
            this.artistId = artistId;
        }

        @Override
        protected void onPreExecute() {
            albumAdapter.clear();
        }

        @Override
        protected List<AlbumModel> doInBackground(String... params) {
            ArrayList<AlbumModel> models = new ArrayList<>();
            AlbumModel topTracksModel = new AlbumModel();
            topTracksModel.setName("Top Tracks");
            topTracksModel.setId("Top Tracks");
            topTracksModel.setUrl(mContext.getResources().getString(R.string.top_tracks));
            models.add(topTracksModel);

            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                Pager<Album> results = spotify.getArtistAlbums(params[0]);

                for(Album album : results.items) {
                    AlbumModel model = new AlbumModel();
                    model.setId(album.id);
                    model.setName(album.name);
                    if(album.images.size() > 0) {
                        model.setUrl(album.images.get(0).url);
                    }
                    models.add(model);
                }
            } catch(RetrofitError error) {
                Log.e("Error: ", error.getMessage());
                return null;
            }

            return models;
        }

        @Override
        protected void onPostExecute(List<AlbumModel> albums) {

            if(albums == null) {
                Toast.makeText(mContext, "Oh no! Something went wrong :( Make sure you have an internet connection", Toast.LENGTH_SHORT).show();
            } else if(albums.isEmpty()) {
                Toast.makeText(mContext, "This artist has no albums. Please redefine your search", Toast.LENGTH_SHORT).show();
            } else {

                populateAlbums(albums);

                if(artistId != null && trackAdapter != null) {
                    new TrackDownloader(mContext, trackAdapter).execute(Utils.GET_TOP_TRACKS, artistId);
                }
            }
        }
    }


}
