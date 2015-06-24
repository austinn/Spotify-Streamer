package com.refect.spotifystreamer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.refect.spotifystreamer.adapters.NavigationDrawerAdapter;
import com.refect.spotifystreamer.fragments.ArtistFragment;
import com.refect.spotifystreamer.models.NavigationModel;
import com.refect.spotifystreamer.utils.CircleTransformation;
import com.refect.spotifystreamer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates the navigation drawer and starts
 * all content animations. After the animations are
 * finished, the ArtistFragment is instantiated.
 */
public class MainActivity extends AppCompatActivity {

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private NavigationDrawerAdapter navigationAdapter;
    private List<NavigationModel> navigationOptions;
    private RecyclerView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar toolbar;
    private TextView tvToolbar;
    private ImageView ivNavigationProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

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

    /**
     * Initializes the view elements
     * that don't need to be animated
     * on start
     */
    private void initContentUI() {
        navigationOptions = new ArrayList<>();
        navigationOptions.add(new NavigationModel("Listen Now", R.drawable.mr_ic_pause_dark));
        navigationOptions.add(new NavigationModel("Playlists", R.drawable.mr_ic_pause_dark));
        navigationOptions.add(new NavigationModel("Starred", R.drawable.mr_ic_pause_dark));
        navigationOptions.add(new NavigationModel("Profile", R.drawable.mr_ic_pause_dark));
        navigationOptions.add(new NavigationModel("Settings", R.drawable.mr_ic_pause_dark));
        navigationOptions.add(new NavigationModel(" ", R.drawable.mr_ic_pause_dark));
        navigationOptions.add(new NavigationModel("Playlist 1", R.drawable.mr_ic_pause_dark));
        navigationOptions.add(new NavigationModel("Playlist 2", R.drawable.mr_ic_pause_dark));
        navigationOptions.add(new NavigationModel("Playlist 3", R.drawable.mr_ic_pause_dark));
        navigationOptions.add(new NavigationModel("Playlist 4", R.drawable.mr_ic_pause_dark));
        navigationOptions.add(new NavigationModel("Playlist 5", R.drawable.mr_ic_pause_dark));

        navigationAdapter = new NavigationDrawerAdapter(this);
        navigationAdapter.setModels(navigationOptions);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer_list);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));
        mDrawerList.setItemAnimator(new DefaultItemAnimator());

        ivNavigationProfilePicture = (ImageView) findViewById(R.id.iv_navigation_profile_picture);

        Picasso.with(this)
                .load(getResources().getString(R.string.default_profile_picture))
                .transform(new CircleTransformation())
                .into(ivNavigationProfilePicture);

        // Set the adapter for the list view
        mDrawerList.setAdapter(navigationAdapter);

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

    /**
     * Hide everything off-screen
     * and animate on-screen
     */
    private void startIntroAnimation() {
        int actionbarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionbarSize);
        tvToolbar.setTranslationY(-actionbarSize);
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

    @Override
    public void onResume() {
        super.onResume();
    }
}
