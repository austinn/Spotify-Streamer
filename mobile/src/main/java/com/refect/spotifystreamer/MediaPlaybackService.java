package com.refect.spotifystreamer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.refect.spotifystreamer.models.TrackModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by anelson on 7/6/15.
 */
public class MediaPlaybackService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final IBinder musicBind = new MusicBinder();
    public MediaPlayer mPlayer;
    private List<TrackModel> tracks;
    private int currentPos;
    private boolean isPaused;
    private boolean isShuffle;

    @Override
    public void onCreate() {
        super.onCreate();
        currentPos = 0;
        isPaused = false;
        mPlayer = new MediaPlayer();
        Log.d("OnCreate", "MedaiPlayerService");

        initMediaPlayer();
    }

    public void initMediaPlayer(){
        mPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    public void playTrack() {
        mPlayer.reset();

        Log.d("Tracks", tracks.size() + "");
        Log.d("CurrentPos", currentPos + "");

        TrackModel track = tracks.get(currentPos);
        String trackUrl = track.getPreviewUrl();
        Uri uri = Uri.parse(trackUrl);
        Log.d("Preview Url", trackUrl);

        try{
            mPlayer.setDataSource(getApplicationContext(), uri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        mPlayer.prepareAsync();
    }

    public void setTrack(int pos){
        currentPos = pos;
    }

    public void setTracks(List<TrackModel> tracks) {
        Log.d("Set tracks", tracks.size() + "");
        this.tracks = tracks;
    }

    public void playNext(){
        if(isShuffle){
            Random rand = new Random();
            int newSong = currentPos;
            while(newSong == currentPos){
                newSong = rand.nextInt(tracks.size());
            }
            currentPos = newSong;
        }
        else{
            currentPos++;
            if(currentPos >= tracks.size()) {
                currentPos=0;
            }
        }

        playTrack();
    }

    public void playPrevious() {
        currentPos--;
        if(currentPos < 0) {
            currentPos = 0;
        } else {
            playTrack();
        }

    }

    public boolean isPaused() {
        return isPaused;
    }

    public class MusicBinder extends Binder {
        public MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mPlayer.stop();
        mPlayer.release();

        isPaused = false;
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public int getPos(){
        return mPlayer.getCurrentPosition();
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public int getDur(){
        return mPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    public void pause(){
        isPaused = true;
        mPlayer.pause();
    }

    public void seek(int pos){
        mPlayer.seekTo(pos);
    }

    public void play(){
        mPlayer.start();
    }

}
