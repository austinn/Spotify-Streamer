package com.refect.spotifystreamer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.adapters.ArtistAdapter;
import com.refect.spotifystreamer.adapters.TrackAdapter;
import com.refect.spotifystreamer.async.TrackDownloader;
import com.refect.spotifystreamer.models.ArtistModel;
import com.refect.spotifystreamer.models.TrackModel;
import com.refect.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;


public class TopTracksFragment extends Fragment {

    private String artistId;
    private String artistImageUrl;

    private RecyclerView rvTopTracks;
    private TrackAdapter trackAdapter;
    private ArrayList<TrackModel> trackModels;

    /**
     *
     * @return A new instance of fragment ArtistFragment.
     */
    public static TopTracksFragment newInstance(HashMap<String, String> params) {
        TopTracksFragment fragment = new TopTracksFragment();
        Bundle args = new Bundle();
        args.putString(Utils.INTENT_ARTIST_ID, params.get(Utils.INTENT_ARTIST_ID));
        args.putString(Utils.INTENT_ARTIST_IMAGE_URL, params.get(Utils.INTENT_ARTIST_IMAGE_URL));
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
            artistId = getArguments().getString(Utils.INTENT_ARTIST_ID);
            artistImageUrl = getArguments().getString(Utils.INTENT_ARTIST_IMAGE_URL);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_top_tracks, container, false);

        setHasOptionsMenu(true);
        initUI(view);

        if(savedInstanceState == null) {
            new TrackDownloader(getActivity(), trackAdapter).execute(artistId);
        }

        return view;
    }

    /**
     *
     * @param view
     */
    private void initUI(View view) {

        trackAdapter = new TrackAdapter(getActivity());
        if(trackModels != null) {
            trackAdapter.setModels(trackModels);
        }

        rvTopTracks = (RecyclerView) view.findViewById(R.id.rv_top_tracks);
        rvTopTracks.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTopTracks.setItemAnimator(new DefaultItemAnimator());
        rvTopTracks.setAdapter(trackAdapter);

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

}
