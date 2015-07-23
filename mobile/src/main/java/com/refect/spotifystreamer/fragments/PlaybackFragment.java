package com.refect.spotifystreamer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.refect.spotifystreamer.MainActivity;
import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.models.TrackModel;
import com.refect.spotifystreamer.utils.CircleTransformation;
import com.refect.spotifystreamer.utils.Utils;
import com.squareup.picasso.Picasso;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;


public class PlaybackFragment extends Fragment {

    private ImageView ivTrack;
    private MorphButton ibPlayPause;
    private ImageButton ibNext;
    private ImageButton ibPrevious;

    private TrackModel trackModel;
    private ArrayList<TrackModel> trackModels;

    private TextView tvNowPlayingTrack;
    private TextView tvNowPlayingArtist;

    MainActivity mActivity;
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
        mActivity = ((MainActivity)getActivity());

        setHasOptionsMenu(true);
        initUI(view);
        //setController(view);
        mActivity.mediaPlaybackService.setTracks(trackModels);
        mActivity.mediaPlaybackService.setTrack(findPosition());

        ibPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mActivity.isPlaying()) {
                    mActivity.pause();
                    an.cancel();

                } else {
                    an.start();

                    if (mActivity.mediaPlaybackService.isPaused()) {
                        mActivity.start();
                    } else {
                        mActivity.mediaPlaybackService.playTrack();
                    }
                }
            }
        });

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.mediaPlaybackService.playNext();

                tvNowPlayingTrack.setText(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getTitle());
                tvNowPlayingArtist.setText(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getArtist());

                Picasso.with(getActivity()).load(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getUrl())
                        .transform(new CircleTransformation())
                        .into(ivTrack);

                Picasso.with(getActivity()).load(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getUrl())
                        .into(mActivity.ivTrackImage);
            }
        });

        ibPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.mediaPlaybackService.playPrevious();

                tvNowPlayingTrack.setText(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getTitle());
                tvNowPlayingArtist.setText(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getArtist());

                Picasso.with(getActivity()).load(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getUrl())
                        .transform(new CircleTransformation())
                        .into(ivTrack);

                Picasso.with(getActivity()).load(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getUrl())
                        .into(mActivity.ivTrackImage);
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
                new RelativeLayout.LayoutParams(Utils.getScreenWidth(getActivity()) - 175, Utils.getScreenWidth(getActivity()) - 175);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        ivTrack.setLayoutParams(params);

        Picasso.with(getActivity()).load(trackModel.getUrl())
                .transform(new CircleTransformation())
                .into(ivTrack);

        Picasso.with(getActivity()).load(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getUrl())
                .into(mActivity.ivTrackImage);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

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
        Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_out_bottom);
        mActivity.viewMediaController.startAnimation(slideDown);

        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mActivity.viewMediaController.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom);
        mActivity.viewMediaController.setVisibility(View.VISIBLE);
        mActivity.viewMediaController.startAnimation(slideUp);
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
}
