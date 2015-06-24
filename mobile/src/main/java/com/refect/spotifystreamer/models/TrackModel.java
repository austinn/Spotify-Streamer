package com.refect.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anelson on 6/24/15.
 */
public class TrackModel implements Parcelable {

    private String id;
    private String title;
    private String album;
    private String artist;
    private String url;

    public TrackModel() {

    }

    protected TrackModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        album = in.readString();
        url = in.readString();
        artist = in.readString();
    }

    public static final Creator<TrackModel> CREATOR = new Creator<TrackModel>() {
        @Override
        public TrackModel createFromParcel(Parcel in) {
            return new TrackModel(in);
        }

        @Override
        public TrackModel[] newArray(int size) {
            return new TrackModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(album);
        parcel.writeString(url);
        parcel.writeString(artist);
    }
}
