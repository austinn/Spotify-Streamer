package com.refect.spotifystreamer.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.refect.spotifystreamer.MediaPlaybackService;
import com.refect.spotifystreamer.MusicController;
import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.models.TrackModel;
import com.refect.spotifystreamer.utils.CircleTransformation;
import com.refect.spotifystreamer.utils.Utils;
import com.squareup.picasso.Picasso;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;


public class PlaybackFragment extends Fragment implements MediaController.MediaPlayerControl {

    private ImageView ivTrack;
    private MorphButton ibPlayPause;
    private ImageButton ibNext;
    private ImageButton ibPrevious;

    private TrackModel trackModel;
    private ArrayList<TrackModel> trackModels;

    private TextView tvNowPlayingTrack;
    private TextView tvNowPlayingArtist;

    private MusicController controller;
    public MediaPlaybackService mediaPlaybackService;
    private Intent playbackIntent;
    private boolean isBound = false;

    Animation an;

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

                if (isPlaying()) {
                    pause();
                    an.cancel();

                } else {
                    an.start();

                    if (mediaPlaybackService.isPaused()) {
                        start();
                    } else {
                        mediaPlaybackService.playTrack();
                    }
                }
            }
        });

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlaybackService.playNext();

                tvNowPlayingTrack.setText(trackModels.get(mediaPlaybackService.getCurrentPos()).getTitle());
                tvNowPlayingArtist.setText(trackModels.get(mediaPlaybackService.getCurrentPos()).getArtist());

                Picasso.with(getActivity()).load(trackModels.get(mediaPlaybackService.getCurrentPos()).getUrl())
                        .transform(new CircleTransformation())
                        .into(ivTrack);
            }
        });

        ibPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlaybackService.playPrevious();

                tvNowPlayingTrack.setText(trackModels.get(mediaPlaybackService.getCurrentPos()).getTitle());
                tvNowPlayingArtist.setText(trackModels.get(mediaPlaybackService.getCurrentPos()).getArtist());

                Picasso.with(getActivity()).load(trackModels.get(mediaPlaybackService.getCurrentPos()).getUrl())
                        .transform(new CircleTransformation())
                        .into(ivTrack);
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
        ibPlayPause = (MorphButton) view.findViewById(R.id.ib_play_pause);
        ibNext = (ImageButton) view.findViewById(R.id.ib_next);
        ibPrevious = (ImageButton) view.findViewById(R.id.ib_previous);
        tvNowPlayingTrack = (TextView) view.findViewById(R.id.tv_now_playing_track);
        tvNowPlayingArtist = (TextView) view.findViewById(R.id.tv_now_playing_artist);

        an = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        ivTrack.setAnimation(an);

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(Utils.getScreenWidth(getActivity()) - 100, Utils.getScreenWidth(getActivity()) - 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        ivTrack.setLayoutParams(params);

        Picasso.with(getActivity()).load(trackModel.getUrl())
                .transform(new CircleTransformation())
                .into(ivTrack);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void setController(View view){
        controller = new MusicController(getActivity());

        controller.setMediaPlayer(this);
        controller.setAnchorView(view.findViewById(R.id.iv_track_image));
        controller.setEnabled(true);

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlaybackService.playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlaybackService.playPrevious();
            }
        });
    }

    public ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlaybackService.MusicBinder binder = (MediaPlaybackService.MusicBinder)service;
            mediaPlaybackService = binder.getService();
            mediaPlaybackService.setTracks(trackModels);
            mediaPlaybackService.setTrack(findPosition());

            tvNowPlayingTrack.setText(trackModels.get(mediaPlaybackService.getCurrentPos()).getTitle());
            tvNowPlayingArtist.setText(trackModels.get(mediaPlaybackService.getCurrentPos()).getArtist());

            isBound = true;
            Log.d("isBound", isBound + "");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private int findPosition() {
        int pos = 0;

        for(int i = 0; i < trackModels.size(); i++) {
            if(trackModels.get(i).getId().equals(trackModel.getId())) {
                pos = i;
            }
        }

        return pos;
    }

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

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void pause() {
        mediaPlaybackService.pause();
        //ibPlayPause.setImageResource(android.R.drawable.ic_media_play);
        ibPlayPause.setState(MorphButton.MorphState.START, true);
        an.cancel();
    }

    @Override
    public void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlaybackService.seek(pos);
    }

    @Override
    public void start() {
        mediaPlaybackService.play();
        //ibPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        ibPlayPause.setState(MorphButton.MorphState.END, true);
        an.start();
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
