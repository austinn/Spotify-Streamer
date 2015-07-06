package com.refect.spotifystreamer.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;

import com.refect.spotifystreamer.MediaPlaybackService;
import com.refect.spotifystreamer.MusicController;
import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.models.TrackModel;
import com.refect.spotifystreamer.utils.Utils;

import java.util.ArrayList;


public class PlaybackFragment extends Fragment implements MediaController.MediaPlayerControl {

    private ImageView ivTrack;
    private ImageButton ibPlayPause;

    private TrackModel trackModel;
    private ArrayList<TrackModel> trackModels;

    private MusicController controller;
    public MediaPlaybackService mediaPlaybackService;
    private Intent playbackIntent;
    private boolean isBound = false;

    /**
     *
     * @return A new instance of fragment ArtistFragment.
     */
    public static PlaybackFragment newInstance(ArrayList<TrackModel> models, TrackModel model) {
        PlaybackFragment fragment = new PlaybackFragment();
        Bundle args = new Bundle();
        args.putParcelable(Utils.INTENT_TRACK_MODEL, model);
        args.putParcelableArrayList(Utils.INTENT_TRACK_MODELS, models);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaybackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            trackModel = getArguments().getParcelable(Utils.INTENT_TRACK_MODEL);
            trackModels = getArguments().getParcelableArrayList(Utils.INTENT_TRACK_MODELS);

            if(trackModels == null) {
                trackModels = new ArrayList<>();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_playback, container, false);

        setHasOptionsMenu(true);
        initUI(view);
        setController(view);

        ibPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isPlaying()) {
                    ibPlayPause.setImageResource(android.R.drawable.ic_media_play);
                    pause();
                } else {
                    ibPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    start();
                }
            }
        });

        return view;
    }

    /**
     *
     * @param view
     */
    private void initUI(View view) {

        ivTrack = (ImageView) view.findViewById(R.id.iv_track_image);
        ibPlayPause = (ImageButton) view.findViewById(R.id.ib_play_pause);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void setController(View view){
        controller = new MusicController(getActivity());

        controller.setMediaPlayer(this);
        controller.setAnchorView(view.findViewById(R.id.toolbar));
        controller.setEnabled(true);
    }

    public ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder)service;
            mediaPlaybackService = binder.getService();
            mediaPlaybackService.setTracks(trackModels);
            mediaPlaybackService.setTrack(0);
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
        Log.d("OnStart", "Playback Fragment");
        if(playbackIntent == null){
            Log.d("PlaybackIntent", "is null");
            playbackIntent = new Intent(getActivity(), MediaPlaybackService.class);
            getActivity().bindService(playbackIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playbackIntent);
        }
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(playbackIntent);
        mediaPlaybackService = null;
        super.onDestroy();
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

    @Override
    public void pause() {
        mediaPlaybackService.pause();
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
