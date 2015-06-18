package com.refect.spotifystreamer;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.refect.spotifystreamer.adapters.TrackAdapter;
import com.refect.spotifystreamer.async.TrackDownloader;
import com.refect.spotifystreamer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Tracks;

public class TracksActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvTracks;
    private RecyclerView.LayoutManager linearLayoutManager;
    private TrackAdapter trackAdapter;
    private ImageView ivArtistImage;

    private String artistId;
    private String artistImageUrl;

    private View viewTransparentBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        artistId = getIntent().getStringExtra("artistId");
        artistImageUrl = getIntent().getStringExtra("artistImageUrl");

        initUI();
        doStuff();
    }

    private void doStuff() {
        if(artistImageUrl != null) {
            Picasso.with(this)
                    .load(artistImageUrl)
                    .into(ivArtistImage);
        }

        new TrackDownloader(this, trackAdapter).execute(artistId);
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        viewTransparentBackground = findViewById(R.id.view_transparent_background);

        RelativeLayout.LayoutParams transparentParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        transparentParams.width = Utils.getScreenWidth(this)/2;
        transparentParams.height = Utils.getScreenWidth(this)/2;
        transparentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        viewTransparentBackground.setLayoutParams(transparentParams);

        trackAdapter = new TrackAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this);
        rvTracks = (RecyclerView) findViewById(R.id.rv_tracks);
        rvTracks.hasFixedSize();
        rvTracks.setLayoutManager(linearLayoutManager);
        rvTracks.setItemAnimator(new DefaultItemAnimator());
        rvTracks.setAdapter(trackAdapter);
        ivArtistImage = (ImageView) findViewById(R.id.iv_artist_image);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                Utils.getScreenWidth(this), Utils.getScreenWidth(this));
        ivArtistImage.setLayoutParams(params);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_play);

        RelativeLayout.LayoutParams param =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        param.topMargin = Utils.getScreenWidth(this) - (Utils.dpToPx(56/2));
        param.rightMargin = Utils.dpToPx(16);
        param.width = Utils.dpToPx(56);
        param.height = Utils.dpToPx(56);
        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        fab.setLayoutParams(param);
    }

    private void initUILand() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        viewTransparentBackground = findViewById(R.id.view_transparent_background);

        trackAdapter = new TrackAdapter(this);
        rvTracks = (RecyclerView) findViewById(R.id.rv_tracks);
        rvTracks.setLayoutManager(new LinearLayoutManager(this));
        rvTracks.setItemAnimator(new DefaultItemAnimator());

        rvTracks.setAdapter(trackAdapter);
        ivArtistImage = (ImageView) findViewById(R.id.iv_artist_image);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_tracks_land);

            linearLayoutManager = new LinearLayoutManager(this);
            rvTracks = (RecyclerView) findViewById(R.id.rv_tracks);
            rvTracks.hasFixedSize();
            rvTracks.setLayoutManager(linearLayoutManager);
            rvTracks.setItemAnimator(new DefaultItemAnimator());
            rvTracks.setAdapter(trackAdapter);


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_tracks);

            linearLayoutManager = new LinearLayoutManager(this);
            rvTracks = (RecyclerView) findViewById(R.id.rv_tracks);
            rvTracks.hasFixedSize();
            rvTracks.setLayoutManager(linearLayoutManager);
            rvTracks.setItemAnimator(new DefaultItemAnimator());
            rvTracks.setAdapter(trackAdapter);

        }
    }

    @Override
    public void onDestroy() {
        Log.d("DESTROY", "DESTROY");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.d("PAUSED", "PAUSED");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d("RESUMED", "RESUMED");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
