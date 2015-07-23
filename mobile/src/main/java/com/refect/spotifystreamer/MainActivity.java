package com.refect.spotifystreamer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;

import com.refect.spotifystreamer.adapters.NavigationDrawerAdapter;
import com.refect.spotifystreamer.fragments.ArtistFragment;
import com.refect.spotifystreamer.fragments.PlaybackFragment;
import com.refect.spotifystreamer.fragments.SettingsFragment;
import com.refect.spotifystreamer.models.NavigationModel;
import com.refect.spotifystreamer.utils.CircleTransformation;
import com.refect.spotifystreamer.utils.Utils;
import com.squareup.picasso.Picasso;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * This class creates the navigation drawer and starts
 * all content animations. After the animations are
 * finished, the ArtistFragment is instantiated.
 */
public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private ScrollView mDrawerList;
    private LinearLayout mParentLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar toolbar;
    private TextView tvToolbar;
    private ImageView ivNavigationProfilePicture;

    private MusicController controller;
    public MediaPlaybackService mediaPlaybackService;
    private Intent playbackIntent;
    private boolean isBound = false;

    public View viewMediaController;
    public ImageView ivTrackImage;
    public ImageView ivMediaControllerPlayPause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initMediaController();

        if (savedInstanceState == null) {
            startIntroAnimation();
        }
    }

    /**
     * All the elements that have to be
     * initialized before the start animations
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvToolbar = (TextView) findViewById(R.id.tv_toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void initMediaController() {
        viewMediaController = findViewById(R.id.view_media_controller);
        ivTrackImage = (ImageView) findViewById(R.id.iv_track_image);
        ivMediaControllerPlayPause = (ImageView) findViewById(R.id.iv_media_controller_play_pause);

        /**
         *
         */
        ivMediaControllerPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * Initializes the view elements
     * that don't need to be animated
     * on start
     */
    private void initContentUI() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ScrollView) findViewById(R.id.left_drawer_list);
        mParentLayout = (LinearLayout) findViewById(R.id.navigation_parent_layout);

        setupNavigationViews();

        ivNavigationProfilePicture = (ImageView) findViewById(R.id.iv_navigation_profile_picture);

        Picasso.with(this)
                .load(getResources().getString(R.string.default_profile_picture))
                .transform(new CircleTransformation())
                .into(ivNavigationProfilePicture);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar , R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setupNavigationViews() {
        mParentLayout.addView(createView("Now Playing"));
        mParentLayout.addView(createSettingsView("Settings"));
    }

    private View createView(final String title) {
        View view = View.inflate(getApplicationContext(), R.layout.list_item_navigation, null);

        TextView tvName = (TextView) view.findViewById(R.id.tv_navigation_title);
        tvName.setText(title);

        return view;
    }

    private View createSettingsView(String title) {
        View view = View.inflate(getApplicationContext(), R.layout.list_item_navigation, null);

        TextView tvName = (TextView) view.findViewById(R.id.tv_navigation_title);
        tvName.setText(title);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new SettingsFragment())
                        .addToBackStack("settings")
                        .commit();

                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        return view;
    }


    /**
     * Hide everything off-screen
     * and animate on-screen
     */
    private void startIntroAnimation() {
        int actionbarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionbarSize);
        tvToolbar.setTranslationY(-actionbarSize);
        viewMediaController.setTranslationY(actionbarSize);
        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        tvToolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                        viewMediaController.animate()
                                .translationY(0)
                                .setDuration(ANIM_DURATION_FAB)
                                .start();
                    }
                })
                .start();
    }

    /**
     * Once the intro animations are finished
     * start the transaction
     */
    private void startContentAnimation() {
        initContentUI();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, ArtistFragment.newInstance())
                .commit();

        String firstTime = Utils.getSetting(Utils.PREFS_FIRST_TIME, null, this);
        if(firstTime == null) {
            Utils.storeSetting(Utils.PREFS_FIRST_TIME, "false", this);
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    public ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder)service;
            mediaPlaybackService = binder.getService();

            isBound = true;
            Log.d("isBound", isBound + "");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Log.d("OnStart", "MainActivity");
        if(playbackIntent == null){
            Log.d("PlaybackIntent", "is null");
            playbackIntent = new Intent(this, MediaPlaybackService.class);
            bindService(playbackIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playbackIntent);
        }
    }

    @Override
    public void onDestroy() {
        stopService(playbackIntent);
        mediaPlaybackService = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void pause() {
        mediaPlaybackService.pause();
    }

    @Override
    public void onStop() {
        //controller.hide();
        super.onStop();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlaybackService.seek(pos);
    }

    @Override
    public void start() {
        mediaPlaybackService.play();
    }

    @Override
    public int getDuration() {
        if(mediaPlaybackService != null && isBound && mediaPlaybackService.isPlaying())
            return mediaPlaybackService.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(mediaPlaybackService != null && isBound && mediaPlaybackService.isPlaying())
            return mediaPlaybackService.getPos();
        else return 0;
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlaybackService != null && isBound)
            return mediaPlaybackService.isPlaying();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
