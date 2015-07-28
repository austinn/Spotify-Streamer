package com.refect.spotifystreamer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.refect.spotifystreamer.MainActivity;
import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.adapters.ArtistAdapter;
import com.refect.spotifystreamer.async.ArtistDownloader;
import com.refect.spotifystreamer.listeners.OnRecyclerViewItemClickListener;
import com.refect.spotifystreamer.models.ArtistModel;
import com.refect.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ArtistFragment extends Fragment {

    private RecyclerView rvArtists;
    private ArtistAdapter artistAdapter;
    private ArrayList<ArtistModel> artistModels;
    private AutoCompleteTextView editSearch;
    private RelativeLayout searchLayout;

    private Animation animSlideDown;
    private Animation animSlideUp;

    private ArrayAdapter<String> historyAdapter;
    private MenuItem inboxMenuItem;

    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;

    private boolean isTwoPane;

    /**
     *
     * @return A new instance of fragment ArtistFragment.
     */
    public static ArtistFragment newInstance(Map<String, String> prefs) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putString(Utils.INTENT_TWO_PANE, prefs.get(Utils.INTENT_TWO_PANE));
        fragment.setArguments(args);
        Log.d("ArtistFragment", "newInstance");
        return fragment;
    }

    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            artistModels = savedInstanceState.getParcelableArrayList(Utils.KEY_ARTIST_MODELS);
        }

        if(getArguments() != null) {

        }

        Log.d("ArtistFragment", "onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_artists, container, false);
        Log.d("ArtistFragment", "onCreateView");

        isTwoPane = true;

        animSlideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_out_top);
        animSlideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_top);

        setHasOptionsMenu(true);
        initUI(view);

        if(savedInstanceState == null) {
            //load results from the last search
            String searchHistory = Utils.getSetting(Utils.PREFS_SEARCH_HISTORY, "", getActivity());
            String[] searchHistoryArray = Utils.convertStringToArray(searchHistory);

            if(searchHistoryArray.length > 0 && !searchHistoryArray[searchHistoryArray.length - 1].isEmpty()) {
                //getActivity().getActionBar().setTitle(searchHistoryArray[searchHistoryArray.length - 1]);
                new ArtistDownloader(getActivity(), artistAdapter).execute(searchHistoryArray[searchHistoryArray.length - 1]);
            }
        }

        return view;
    }

    /**
     *
     * @param view
     */
    private void initUI(View view) {
        artistAdapter = new ArtistAdapter(getActivity(), isTwoPane);
        if(artistModels != null) {
            artistAdapter.setModels(artistModels);
        }

        rvArtists = (RecyclerView) view.findViewById(R.id.rv_artist);
        setLayoutManager();
        rvArtists.setItemAnimator(new DefaultItemAnimator());
        rvArtists.setAdapter(artistAdapter);

        searchLayout = (RelativeLayout) view.findViewById(R.id.layout_search);
        editSearch = (AutoCompleteTextView) view.findViewById(R.id.edit_search_query);

        //create the search history adapter and add all previous searches
        //TODO: remove duplicates
        //Note: Tried using a Set, but was only saving one item
        historyAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        String searchHistory = Utils.getSetting(Utils.PREFS_SEARCH_HISTORY, "", getActivity());
        historyAdapter.addAll(Utils.convertStringToArray(searchHistory));

        editSearch.setAdapter(historyAdapter);
        editSearch.setThreshold(1);

        editSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSearch(v);
                return true;
            }

        });

        editSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = historyAdapter.getItem(position);
                editSearch.setText(str);
                onSearch(editSearch);
            }
        });

        artistAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<ArtistModel>() {
            @Override
            public void onItemClick(View view, ArtistModel model) {

                HashMap<String, String> params = new HashMap<>();
                params.put(Utils.INTENT_ARTIST_NAME, model.getName());
                params.put(Utils.INTENT_ARTIST_ID, model.getId());
                params.put(Utils.INTENT_ARTIST_IMAGE_URL, model.getUrl());
                params.put(Utils.INTENT_TWO_PANE, isTwoPane + "");

                Fragment topTracksFragment = TopTracksFragment.newInstance(params);
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, topTracksFragment)
                        .addToBackStack("top_tracks")
                        .commit();
            }
        });
    }

    /**
     * Determines which layout manager to use from shared prefs
     * Defaults to Grid (first time or unable to retrieve setting)
     */
    private void setLayoutManager() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        String layoutPrefs = Utils.getSetting(Utils.PREFS_LAYOUT_MANAGER, Utils.PREFS_GRID, getActivity());
        if(layoutPrefs.equals(Utils.PREFS_GRID)) {
            rvArtists.setLayoutManager(gridLayoutManager);
        } else {
            rvArtists.setLayoutManager(linearLayoutManager);
        }
    }

    /**
     * Called from the search button on the toolbar
     * If search view is gone, show the search view
     * If search view is visible, perform search query
     */
    public void onSearch(View v) {
        if(searchLayout.getVisibility() == View.GONE) {
            searchLayout.setVisibility(View.VISIBLE);
            searchLayout.startAnimation(animSlideUp);
            editSearch.setFocusableInTouchMode(true);
            editSearch.requestFocus();
            Utils.showKeyboard(getActivity(), editSearch);
        } else {
            if(editSearch.getText().toString().length() <= 0) {
                searchLayout.setVisibility(View.GONE);
                searchLayout.startAnimation(animSlideDown);
                return;
            }
            searchLayout.setVisibility(View.GONE);
            searchLayout.startAnimation(animSlideDown);

            //add search terms to previous searches
            String searchHistory = Utils.getSetting(Utils.PREFS_SEARCH_HISTORY, "", getActivity());
            searchHistory += "__,__";
            searchHistory += editSearch.getText().toString();
            Utils.storeSetting(Utils.PREFS_SEARCH_HISTORY, searchHistory, getActivity());

            historyAdapter.add(editSearch.getText().toString());
            historyAdapter.notifyDataSetChanged();

            new ArtistDownloader(getActivity(), artistAdapter).execute(editSearch.getText().toString());
            Utils.hideKeyboard(getActivity(), v);
            //getActivity().getActionBar().setTitle(editSearch.getText().toString());
            editSearch.setText("");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        inboxMenuItem = menu.findItem(R.id.action_inbox);
        inboxMenuItem.setActionView(R.layout.menu_item_view);
        inboxMenuItem.getActionView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSearch(v);
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(outState != null && artistAdapter != null) {
            ArrayList<ArtistModel> parceable = new ArrayList<>();
            for (ArtistModel model : artistAdapter.getModels()) {
                parceable.add(model);
            }

            outState.putParcelableArrayList(Utils.KEY_ARTIST_MODELS, parceable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_grid) {
            rvArtists.setLayoutManager(gridLayoutManager);
            Utils.storeSetting(Utils.PREFS_LAYOUT_MANAGER, Utils.PREFS_GRID, getActivity());
            return true;
        } else if(id == R.id.menu_list) {
            rvArtists.setLayoutManager(linearLayoutManager);
            Utils.storeSetting(Utils.PREFS_LAYOUT_MANAGER, Utils.PREFS_LIST, getActivity());
            return true;
        } else if(id == R.id.menu_clear_history) {
            //TODO: Put this in a settings fragment
            Utils.storeSetting(Utils.PREFS_SEARCH_HISTORY, "", getActivity());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
