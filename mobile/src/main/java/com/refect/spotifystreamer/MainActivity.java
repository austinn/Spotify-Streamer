package com.refect.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.refect.spotifystreamer.adapters.ArtistAdapter;
import com.refect.spotifystreamer.async.ArtistDownloader;
import com.refect.spotifystreamer.listeners.OnRecyclerViewItemClickListener;
import com.refect.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Artist;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvToolbar;
    private RecyclerView rvArtists;
    private ArtistAdapter artistAdapter;
    private ImageView ivSearch;
    private AutoCompleteTextView editSearch;
    private RelativeLayout view;

    private Animation animSlideDown;
    private Animation animSlideUp;

    private ArrayAdapter<String> historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animSlideDown = AnimationUtils.loadAnimation(this, R.anim.abc_slide_out_top);
        animSlideUp = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_top);
        initUI();
    }

    private void initUI() {
        artistAdapter = new ArtistAdapter(this);
        rvArtists = (RecyclerView) findViewById(R.id.rv_artist);
        rvArtists.setLayoutManager(new GridLayoutManager(this, 2));
        rvArtists.setItemAnimator(new DefaultItemAnimator());
        rvArtists.setAdapter(artistAdapter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvToolbar = (TextView) findViewById(R.id.tv_toolbar);
        ivSearch = (ImageView) findViewById(R.id.iv_search);

        view = (RelativeLayout) findViewById(R.id.layout_search);
        editSearch = (AutoCompleteTextView) findViewById(R.id.edit_search_query);

        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        historyAdapter.addAll(Utils.getSetting("search_history", new HashSet<String>(), this));
        editSearch.setAdapter(historyAdapter);
        editSearch.setThreshold(1);

        editSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    ivSearch.performClick();
                }
                return false;
            }
        });

        editSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = historyAdapter.getItem(position);
                editSearch.setText(str);
            }
        });

        artistAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<Artist>() {
            @Override
            public void onItemClick(View view, Artist model) {
                Intent intent = new Intent(MainActivity.this, TracksActivity.class);
                intent.putExtra("artistId", model.id);
                if(model.images.size() > 0) {
                    intent.putExtra("artistImageUrl", model.images.get(0).url);
                }
                startActivity(intent);
            }
        });
    }

    /**
     * Called from the search button on the toolbar
     * If search view is gone, show the search view
     * If search view is visible, perform search query
     * @param v
     */
    public void onSearch(View v) {
        if(view.getVisibility() == View.GONE) {
            ivSearch.setImageResource(android.R.drawable.ic_menu_send);
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animSlideUp);
        } else {
            if(editSearch.getText().toString().length() <= 0) {
                Toast.makeText(this, "You need to enter a search term", Toast.LENGTH_SHORT).show();
                return;
            }
            view.setVisibility(View.GONE);
            ivSearch.setImageResource(R.drawable.abc_ic_search_api_mtrl_alpha);
            view.startAnimation(animSlideDown);

            //store the previous searches
            Set<String> historySet = Utils.getSetting("search_history", new HashSet<String>(), this);
            historySet.add(editSearch.getText().toString());
            historyAdapter.add(editSearch.getText().toString());
            Utils.storeSetting("search_history", historySet, this);

            tvToolbar.setText(editSearch.getText().toString());
            new ArtistDownloader(this, artistAdapter).execute(editSearch.getText().toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
