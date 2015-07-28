package com.refect.spotifystreamer.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.refect.spotifystreamer.MainActivity;
import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.models.TrackModel;
import com.refect.spotifystreamer.utils.CircleTransformation;
import com.refect.spotifystreamer.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


public class PlaybackFragment extends Fragment {

    private ImageView ivBlur;
    private ImageView ivTrack;
    private FloatingActionButton ibPlayPause;
    private FloatingActionButton fabQueue;
    private ImageButton ibNext;
    private ImageButton ibPrevious;
    private SeekBar pbTrackProgress;
    private ImageButton ibFavorite;

    private TrackModel trackModel;
    private ArrayList<TrackModel> trackModels;

    private TextView tvNowPlayingTrack;
    private TextView tvNowPlayingArtist;

    private String artistImageUrl;
    MainActivity mActivity;
    RotateAnimation an;
    private Handler mHandler = new Handler();

    /**
     *
     * @return A new instance of fragment ArtistFragment.
     */
    public static PlaybackFragment newInstance(ArrayList<TrackModel> models, TrackModel model, String artistImageUrl) {
        PlaybackFragment fragment = new PlaybackFragment();
        Bundle args = new Bundle();
        args.putString(Utils.INTENT_ARTIST_IMAGE_URL, artistImageUrl);
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
            artistImageUrl = getArguments().getString(Utils.INTENT_ARTIST_IMAGE_URL);
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

        mActivity.mediaPlaybackService.setTracks(trackModels);
        mActivity.mediaPlaybackService.setTrack(findPosition());

        /**
         *
         */
        ibPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mActivity.isPlaying()) {
                    mActivity.pause();
                    an.cancel();
                    mHandler.removeCallbacks(mRunnable);
                    ibPlayPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);

                } else {
                    an.start();
                    pbTrackProgress.setMax(mActivity.mediaPlaybackService.getDur() / 1000);
                    mActivity.runOnUiThread(mRunnable);
                    ibPlayPause.setImageResource(R.drawable.ic_pause_white_24dp);

                    if (mActivity.mediaPlaybackService.isPaused()) {
                        mActivity.start();
                    } else {
                        mActivity.mediaPlaybackService.playTrack();
                    }
                }
            }
        });

        /**
         *
         */
        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.mediaPlaybackService.playNext();

                loadNewInfo();
            }
        });

        /**
         *
         */
        ibPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.mediaPlaybackService.playPrevious();

                loadNewInfo();
            }
        });

        /**
         *
         */
        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = Utils.getSetting(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getId(), "false", getActivity());
                if(id.equals("true")) {
                    Utils.storeSetting(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getId(), "false", getActivity());
                    ibFavorite.setImageResource(R.drawable.ic_star_border_white_24dp);
                } else {
                    Utils.storeSetting(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getId(), "true", getActivity());
                    ibFavorite.setImageResource(R.drawable.ic_star_white_24dp);
                }
            }
        });

        /**
         *
         */
        fabQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(TrackModel track: trackModels) {
                    Log.d("Queue: ", track.getTitle());
                }
            }
        });

        return view;
    }

    /**
     *
     */
    private Runnable mRunnable = new Runnable() {

            @Override
            public void run() {
                Log.d("RunOnUiThread", "Running");
                if (mActivity.mediaPlaybackService.mPlayer != null) {
                    int mCurrentPosition = mActivity.mediaPlaybackService.mPlayer.getCurrentPosition() / 1000;
                    pbTrackProgress.setProgress(mCurrentPosition);
                    Log.d("RunOnUiThread", mCurrentPosition + "");
                } else {
                    Log.d("RunOnUiThread", "mActivity.mediaPlaybackService.mPlayer is null");
                }
                mHandler.postDelayed(this, 1000);
            }
        };

    /**
     *
     * @param view
     */
    private void initUI(View view) {
        ivBlur = (ImageView) view.findViewById(R.id.iv_blur);
        ivTrack = (ImageView) view.findViewById(R.id.iv_track_image);
        ibPlayPause = (FloatingActionButton) view.findViewById(R.id.ib_play_pause);
        fabQueue = (FloatingActionButton) view.findViewById(R.id.fab_queue);
        ibNext = (ImageButton) view.findViewById(R.id.ib_next);
        ibPrevious = (ImageButton) view.findViewById(R.id.ib_previous);
        ibFavorite = (ImageButton) view.findViewById(R.id.ib_favorite);
        tvNowPlayingTrack = (TextView) view.findViewById(R.id.tv_now_playing_track);
        tvNowPlayingArtist = (TextView) view.findViewById(R.id.tv_now_playing_artist);
        pbTrackProgress = (SeekBar) view.findViewById(R.id.pb_track_progress);
        pbTrackProgress.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#E91E63"), PorterDuff.Mode.MULTIPLY));
        pbTrackProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mActivity.mediaPlaybackService.mPlayer != null && fromUser) {
                    mActivity.mediaPlaybackService.mPlayer.seekTo(progress * 1000);
                }
            }
        });

        //blur the background once
        Picasso.with(getActivity()).load(artistImageUrl)
                .error(R.drawable.ic_launcher)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        ivBlur.setImageBitmap(Utils.fastblur(bitmap, 100));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });

        Picasso.with(getActivity()).load(trackModel.getUrl())
                .transform(new CircleTransformation())
                .error(R.drawable.ic_launcher)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        ivTrack.setImageBitmap(bitmap);

                        RelativeLayout.LayoutParams params =
                                new RelativeLayout.LayoutParams(Utils.getScreenWidth(getActivity()) - 175, Utils.getScreenWidth(getActivity()) - 175);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        ivTrack.setLayoutParams(params);

                        an = new RotateAnimation(0, 360,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        an.setDuration(5000);
                        an.setInterpolator(new LinearInterpolator());
                        an.setRepeatCount(Animation.INFINITE);
                        ivTrack.setAnimation(an);

                        if(mActivity.mediaPlaybackService.isPlaying()) {
                            an.start();
                            ibPlayPause.setImageResource(R.drawable.ic_pause_white_24dp);
                        } else {
                            an.cancel();
                            ibPlayPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        Picasso.with(getActivity()).load(trackModel.getUrl())
                .into(mActivity.ivTrackImage);

        tvNowPlayingTrack.setText(trackModel.getTitle());
        tvNowPlayingArtist.setText(trackModel.getArtist());

        mActivity.tvMediaControllerTitle.setText(trackModel.getTitle());
        mActivity.tvMediaControllerArtist.setText(trackModel.getArtist());

        String id = Utils.getSetting(trackModel.getId(), "false", getActivity());
        if(id.equals("true")) {
            ibFavorite.setImageResource(R.drawable.ic_star_white_24dp);
        } else {
            ibFavorite.setImageResource(R.drawable.ic_star_border_white_24dp);
        }
    }

    /**
     *
     */
    private void loadNewInfo() {
        Picasso.with(getActivity()).load(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getUrl())
                .transform(new CircleTransformation())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        ivTrack.setImageBitmap(bitmap);

                        an = new RotateAnimation(0, 360,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        an.setDuration(5000);
                        an.setInterpolator(new LinearInterpolator());
                        an.setRepeatCount(Animation.INFINITE);
                        ivTrack.setAnimation(an);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        Picasso.with(getActivity()).load(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getUrl())
                .into(mActivity.ivTrackImage);

        tvNowPlayingTrack.setText(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getTitle());
        tvNowPlayingArtist.setText(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getArtist());

        mActivity.tvMediaControllerTitle.setText(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getTitle());
        mActivity.tvMediaControllerArtist.setText(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getArtist());

        String id = Utils.getSetting(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getId(), "false", getActivity());
        if(id.equals("true")) {
            ibFavorite.setImageResource(R.drawable.ic_star_white_24dp);
        } else {
            ibFavorite.setImageResource(R.drawable.ic_star_border_white_24dp);
        }

    }

    /**
     *
     */
    private void openShareDialog() {

        View promptView = getActivity().getLayoutInflater().inflate(R.layout.dialog_share, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edit_share_text);
        final TextView tvTitle = (TextView) promptView.findViewById(R.id.tv_dialog_title);
        final TextView tvArtist = (TextView) promptView.findViewById(R.id.tv_dialog_artist);
        final ImageView ivImage = (ImageView) promptView.findViewById(R.id.iv_dialog_image);

        Picasso.with(getActivity()).load(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getUrl()).into(ivImage);
        tvTitle.setText(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getTitle());
        tvArtist.setText(trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getArtist());

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "SpotifyStreamer\n" +
                                trackModels.get(mActivity.mediaPlaybackService.getCurrentPos()).getShareUrl() + "\n\n" + editText.getText().toString());
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Share this page to..."));
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
        inflater.inflate(R.menu.menu_playback, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_share:

                openShareDialog();

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
